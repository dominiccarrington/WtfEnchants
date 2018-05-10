package com.ragegamingpe.wtfenchants.client.gui.container;

import com.ragegamingpe.wtfenchants.client.gui.container.base.BaseGuiContainer;
import com.ragegamingpe.wtfenchants.common.container.ContainerSorter;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiContainerSorter extends BaseGuiContainer
{
    /**
     * Amount scrolled in Creative mode inventory (0 = top, 1 = bottom)
     */
    private float currentScroll;

    /**
     * True if the scrollbar is being dragged
     */
    private boolean isScrolling;

    /**
     * True if the left mouse button was held down last time drawScreen was called.
     */
    private boolean wasClicking;

    private final EntityPlayer player;

    //    private GuiButton filterButton;
    private static final Map<Pattern, BiFunction<Enchantment, String, Boolean>> FILTER_PATTERNS = new HashMap<>();

    private List<Enchantment> enchants = new ArrayList<>();
    private int listWidth = 0;
    private GuiScrollingEnchants scrollingEnchants;
    private GuiTextField searchBox;
    private String lastFilterText = "";
    public static Enchantment selectedEnchant = null;
    private static Enchantment lastSelectedEnchant = null;

    static {
        addFilterPattern('@', GuiContainerSorter::filterByModName);
    }

    public GuiContainerSorter(EntityPlayer player, IInventory[] bookshelves, BlockPos pos)
    {
        super(
                new ContainerSorter(player.inventory, bookshelves, pos),
                new ResourceLocation(LibMisc.MOD_ID, "textures/gui/sorter.png"),
                194,
                222
        );

        this.player = player;
    }

    @Override
    public void initGui()
    {
        super.initGui();
//        this.buttonList.add(this.filterButton = new GuiButton(0, topLeft[0] + 100, topLeft[1] + 4, 70, 12, "Filter"));

        for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValuesCollection()) {
            listWidth = Math.max(listWidth, Minecraft.getMinecraft().fontRenderer.getStringWidth(new TextComponentTranslation(enchantment.getName()).getUnformattedComponentText()) + 10);
        }
        listWidth = Math.min(listWidth, 150);

        this.scrollingEnchants = new GuiScrollingEnchants(listWidth, this.enchants);
        this.searchBox = new GuiTextField(0, this.mc.fontRenderer, GuiContainerSorter.this.topLeft[0] - listWidth, this.height - this.topLeft[1] - 30, this.listWidth, 20);
        reloadEnchants();
    }

    private void reloadEnchants()
    {
        this.enchants.clear();
        for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValuesCollection()) {
            if (checkFilter(enchantment, this.searchBox.getText())) {
                this.enchants.add(enchantment);
            }
        }

        ContainerSorter container = (ContainerSorter) this.inventorySlots;
        container.changeShownSlots(0.0F);

        this.lastFilterText = this.searchBox.getText();
        lastSelectedEnchant = selectedEnchant;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        boolean flag = Mouse.isButtonDown(0);
        int i = this.guiLeft;
        int j = this.guiTop;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;

        if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1) {
            this.isScrolling = this.needsScrollBars();
        }

        if (!flag) {
            this.isScrolling = false;
        }

        this.wasClicking = flag;

        if (this.isScrolling) {
            this.currentScroll = ((float) (mouseY - l) - 7.5F) / ((float) (j1 - l) - 15.0F);
            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
            ((ContainerSorter) this.inventorySlots).changeShownSlots(this.currentScroll);
        }

        this.scrollingEnchants.drawScreen(mouseX, mouseY, partialTicks);
        this.searchBox.drawTextBox();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        this.searchBox.updateCursorCounter();

        if (!this.searchBox.getText().equals(this.lastFilterText) || selectedEnchant != lastSelectedEnchant) {
            reloadEnchants();
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        if (i != 0 && this.needsScrollBars() && !scrollingEnchants.isMouseOver(mouseX, mouseY)) {
            int j = (((ContainerSorter) this.inventorySlots).numSlots + 9 - 1) / 9 - 5;

            if (i > 0) i = 1;

            if (i < 0) i = -1;

            this.currentScroll = (float) ((double) this.currentScroll - (double) i / (double) j);
            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
            ((ContainerSorter) this.inventorySlots).changeShownSlots(this.currentScroll);
        }
        this.scrollingEnchants.handleMouseInput(mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.searchBox.isFocused()) {
            if (keyCode == 1)
                this.searchBox.setFocused(false);
            else
                this.searchBox.textboxKeyTyped(typedChar, keyCode);
        } else
            super.keyTyped(typedChar, keyCode);
    }

    private boolean needsScrollBars()
    {
        return ((ContainerSorter) this.inventorySlots).numSlots > 54;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.searchBox.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.mc.getTextureManager().bindTexture(this.background);

        int i = 174;
        int j = 16;
        int k = j + 112;
        int y = j + (int) ((float) (k - j - 17) * this.currentScroll);
        int textureX = 194;

        if (isPointInRegion(i, y, 12, 15, mouseX, mouseY)) {
            textureX += 12;
        }

        this.drawTexturedModalRect(i, y, textureX, 0, 12, 15);
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        GuiContainerSorter.selectedEnchant = null;
    }

    public static boolean matchesSelectedEnchant(Enchantment enchant)
    {
        return selectedEnchant == null || selectedEnchant.equals(enchant);
    }

    public static boolean checkFilter(Enchantment enchantment, String filter)
    {
        String searchBox = filter.toLowerCase();

        boolean matches = true;
        for (Map.Entry<Pattern, BiFunction<Enchantment, String, Boolean>> entry : FILTER_PATTERNS.entrySet()) {
            Matcher matcher = entry.getKey().matcher(searchBox);
            if (matcher.matches()) {
                String value = matcher.group(1);
                searchBox = searchBox.replace(matcher.group(0), "").trim();

                matches = matches && entry.getValue().apply(enchantment, value);
            }
        }

        return matches && new TextComponentTranslation(enchantment.getName()).getUnformattedComponentText().toLowerCase().contains(searchBox);
    }

    private static boolean filterByModName(Enchantment enchantment, String search)
    {
        return Objects.requireNonNull(enchantment.getRegistryName()).getResourceDomain().toLowerCase().contains(search);
    }

    private static void addFilterPattern(Pattern pattern, BiFunction<Enchantment, String, Boolean> checkFunction)
    {
        FILTER_PATTERNS.put(pattern, checkFunction);
    }

    private static void addFilterPattern(char s, BiFunction<Enchantment, String, Boolean> checkFunction)
    {
        addFilterPattern(Pattern.compile(s + "([\\w_\\-]*)"), checkFunction);
    }

    public class GuiScrollingEnchants extends GuiScrollingList
    {
        private final List<Enchantment> enchantments;
        private final FontRenderer fontRenderer;

        public GuiScrollingEnchants(int width, List<Enchantment> enchantments)
        {
            super(
                    GuiContainerSorter.this.mc,
                    width,
                    GuiContainerSorter.this.height,
                    GuiContainerSorter.this.topLeft[1],
                    GuiContainerSorter.this.height - GuiContainerSorter.this.topLeft[1] - 30,
                    GuiContainerSorter.this.topLeft[0] - width,
                    35,
                    GuiContainerSorter.this.width,
                    GuiContainerSorter.this.height
            );

            this.enchantments = enchantments;
            this.fontRenderer = GuiContainerSorter.this.mc.fontRenderer;
        }

        public boolean isMouseOver(int mouseX, int mouseY)
        {
            return mouseX >= this.left && mouseX < this.left + this.listWidth && mouseY >= this.top && mouseY < this.bottom;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks)
        {
            super.drawScreen(mouseX, mouseY, partialTicks);
            if (this.selectedIndex >= 0 && this.selectedIndex < this.enchantments.size())
                GuiContainerSorter.selectedEnchant = this.enchantments.get(this.selectedIndex);
        }

        @Override
        protected int getSize()
        {
            return this.enchantments.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick)
        {
        }

        @Override
        protected boolean isSelected(int index)
        {
            return this.selectedIndex == index;
        }

        @Override
        protected void drawBackground()
        {

        }

        @Override
        protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess)
        {
            Enchantment enchantment = this.enchantments.get(slotIdx);
            String name = new TextComponentTranslation(enchantment.getName()).getUnformattedComponentText();
            String mod = Objects.requireNonNull(enchantment.getRegistryName()).getResourceDomain();
            String range = new TextComponentTranslation("enchantment.level." + enchantment.getMinLevel()).getUnformattedComponentText()
                    + " - "
                    + new TextComponentTranslation("enchantment.level." + enchantment.getMaxLevel()).getUnformattedComponentText();
            ModContainer modContainer = Loader.instance().getIndexedModList().get(mod);

            this.fontRenderer.drawString((enchantment.isCurse() ? TextFormatting.RED : TextFormatting.RESET) + this.fontRenderer.trimStringToWidth(name, listWidth - 10), this.left + 3, slotTop + 2, 0xFFFFFF);
            this.fontRenderer.drawString(this.fontRenderer.trimStringToWidth(modContainer.getName(), listWidth - 10), this.left + 3, slotTop + 12, 0xCCCCCC);
            this.fontRenderer.drawString(this.fontRenderer.trimStringToWidth(range, listWidth - 10), this.left + 3, slotTop + 22, 0xCCCCCC);
        }
    }
}

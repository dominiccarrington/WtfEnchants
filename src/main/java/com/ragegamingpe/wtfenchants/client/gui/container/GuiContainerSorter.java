package com.ragegamingpe.wtfenchants.client.gui.container;

import com.ragegamingpe.wtfenchants.client.gui.container.base.BaseGuiContainer;
import com.ragegamingpe.wtfenchants.common.container.ContainerSorter;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

import java.io.IOException;

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

    public GuiContainerSorter(IInventory player, IInventory[] bookshelves, BlockPos pos)
    {
        super(
                new ContainerSorter(player, bookshelves, pos),
                new ResourceLocation(LibMisc.MOD_ID, "textures/gui/sorter.png"),
                194,
                222
        );
    }

    @Override
    public void initGui()
    {
        super.initGui();
//        this.buttonList.add(scanButton = new TooltipButton(0, topLeft[0] + 173, topLeft[1] + 139, 14, 17, "S", "Scan"));
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0 && this.needsScrollBars()) {
            int j = (((ContainerSorter) this.inventorySlots).numSlots + 9 - 1) / 9 - 5;

            if (i > 0) i = 1;

            if (i < 0) i = -1;


            this.currentScroll = (float) ((double) this.currentScroll - (double) i / (double) j);
            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
            ((ContainerSorter) this.inventorySlots).changeShownSlots(this.currentScroll);
        }
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
    }

    private boolean needsScrollBars()
    {
        return ((ContainerSorter) this.inventorySlots).numSlots > 54;
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

    private class TooltipButton extends GuiButton
    {
        private final String tooltip;

        public TooltipButton(int buttonId, int x, int y, String buttonText, String tooltip)
        {
            this(buttonId, x, y, 200, 16, buttonText, tooltip);
        }

        public TooltipButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, String tooltip)
        {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
            this.tooltip = tooltip;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
        {
            super.drawButton(mc, mouseX, mouseY, partialTicks);
            if (this.isMouseOver()) GuiContainerSorter.this.drawHoveringText(this.tooltip, mouseX, mouseY);
        }
    }
}

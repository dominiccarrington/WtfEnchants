package com.ragegamingpe.wtfenchants.client.gui.container;

import com.ragegamingpe.wtfenchants.client.gui.container.base.BaseGuiContainer;
import com.ragegamingpe.wtfenchants.common.container.ContainerDisenchantment;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiContainerDisenchantment extends BaseGuiContainer
{
    private final IInventory playerInv;

    public GuiContainerDisenchantment(EntityPlayer player, World world, BlockPos pos)
    {
        super(
                new ContainerDisenchantment(player, world, pos),
                new ResourceLocation(LibMisc.MOD_ID, "textures/gui/disenchanting_table.png")
        );

        this.playerInv = player.inventory;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks, false);
        this.drawDisenchantingLevels();
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.fontRenderer.drawString(I18n.format("container.wtfenchants:disenchantment.name"), 8, 6, 4210752);
        this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    private void drawDisenchantingLevels()
    {
        final int ENOUGH_XP_COLOR = 8453920;
        final int NOT_ENOUGH_XP_COLOR = 16736352;

        IInventory outputSlots = ((ContainerDisenchantment) this.inventorySlots).outputSlots;
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        for (int i = 0; i < outputSlots.getSizeInventory(); i++) {
            ItemStack stack = outputSlots.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            int x = this.topLeft[0] + 123 + i % 2 * 20;
            int y = this.topLeft[1] + 15 + (int) Math.floor(i / 2) * 18;

            EntityPlayer player = Minecraft.getMinecraft().player;

            int levelToDisenchant = ((ContainerDisenchantment) this.inventorySlots).disenchantLevels[i];
            String s = "" + levelToDisenchant;
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableBlend();
            this.fontRenderer.drawStringWithShadow(s, (float) (x - this.fontRenderer.getStringWidth(s)), (float) (y), (player.experienceLevel >= levelToDisenchant || player.isCreative() ? ENOUGH_XP_COLOR : NOT_ENOUGH_XP_COLOR));
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
        }
    }
}

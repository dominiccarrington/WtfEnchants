package com.ragegamingpe.wtfenchants.client.gui.container;

import com.ragegamingpe.wtfenchants.client.gui.container.base.BaseGuiContainer;
import com.ragegamingpe.wtfenchants.common.block.te.base.TEBasicExperience;
import com.ragegamingpe.wtfenchants.common.container.ContainerXp;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiContainerXp extends BaseGuiContainer
{
    public GuiContainerXp(TEBasicExperience te)
    {
        super(
                new ContainerXp(te),
                new ResourceLocation(LibMisc.MOD_ID, "textures/gui/xp_store.png"),
                176,
                86
        );
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        int calcWidth = this.xSize / 2;
        int calcHeight = this.ySize / 2;

        String s = "" + ((ContainerXp) this.inventorySlots).level;
        int stringWidth = this.fontRenderer.getStringWidth(s);
        this.fontRenderer.drawString(s, calcWidth - stringWidth / 2, calcHeight - this.fontRenderer.FONT_HEIGHT, 8453920, true);

        this.mc.getTextureManager().bindTexture(this.background);

        int height = 3;
        int width = 75;
        int left = 51;
        int top = 45;

        int a = (int) Math.floor(((ContainerXp) this.inventorySlots).percentToLevel * width);
        GlStateManager.color(128 / 255.0F, 255 / 255.0F, 32 / 255.0F);
        this.drawTexturedModalRect(left, top, 176, 0, a, height);
    }
}

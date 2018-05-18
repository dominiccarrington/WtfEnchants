package com.ragegamingpe.wtfenchants.client.gui.container;

import com.ragegamingpe.wtfenchants.client.gui.container.base.BaseGuiContainer;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityXpStore;
import com.ragegamingpe.wtfenchants.common.container.ContainerXpStore;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import com.ragegamingpe.wtfenchants.common.network.MessageHandler;
import com.ragegamingpe.wtfenchants.common.network.message.MessageXpStore;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiContainerXpStore extends BaseGuiContainer
{
    public GuiContainerXpStore(TileEntityXpStore te)
    {
        super(
                new ContainerXpStore(te),
                new ResourceLocation(LibMisc.MOD_ID, "textures/gui/xp_store.png"),
                176,
                86
        );
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int calcWidth = this.width / 2;
        int calcHeight = this.height / 2;

        for (int i = 0; i < 3; i++) {
            StringBuilder text = new StringBuilder();
            for (int j = 0; j < i + 1; j++) text.append("+");
            this.buttonList.add(new GuiTooltipButton(i, calcWidth - 55 + 40 * i, calcHeight - 35, 30, 20, text.toString(), "Move " + (int) Math.pow(10, i) + " levels"));
        }

        for (int i = 0; i < 3; i++) {
            StringBuilder text = new StringBuilder();
            for (int j = 0; j < i + 1; j++) text.append("-");
            this.buttonList.add(new GuiTooltipButton(10 + i, calcWidth - 55 + 40 * i, calcHeight + 15, 30, 20, text.toString(), "Gain " + (int) Math.pow(10, i) + " levels"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id >= 0 && button.id <= 3) {
            // Player -> Store
            MessageHandler.INSTANCE.sendToServer(new MessageXpStore(
                    ((ContainerXpStore) this.inventorySlots).te.getPos(),
                    MessageXpStore.PLAYER_TO_XP_STORE,
                    (int) Math.pow(10, button.id)
            ));
        } else if (button.id >= 10 && button.id <= 13) {
            // Store -> Player
            MessageHandler.INSTANCE.sendToServer(new MessageXpStore(
                    ((ContainerXpStore) this.inventorySlots).te.getPos(),
                    MessageXpStore.XP_STORE_TO_PLAYER,
                    (int) Math.pow(10, button.id - 10)
            ));
        } else {
            super.actionPerformed(button);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        int calcWidth = this.xSize / 2;
        int calcHeight = this.ySize / 2;

        String s = "" + ((ContainerXpStore) this.inventorySlots).level;
        int stringWidth = this.fontRenderer.getStringWidth(s);
        this.fontRenderer.drawString(s, calcWidth - stringWidth / 2, calcHeight - this.fontRenderer.FONT_HEIGHT, 8453920, true);

        this.mc.getTextureManager().bindTexture(this.background);

        int height = 3;
        int width = 75;
        int left = 51;
        int top = 45;

        int a = (int) Math.floor(((ContainerXpStore) this.inventorySlots).percentToLevel * width);
        GlStateManager.color(128 / 255.0F, 255 / 255.0F, 32 / 255.0F);
        this.drawTexturedModalRect(left, top, 176, 0, a, height);
    }
}

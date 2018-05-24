package com.ragegamingpe.wtfenchants.client.gui.container;

import com.ragegamingpe.wtfenchants.common.block.te.TileEntityXpStore;
import com.ragegamingpe.wtfenchants.common.container.ContainerXp;
import com.ragegamingpe.wtfenchants.common.network.MessageHandler;
import com.ragegamingpe.wtfenchants.common.network.message.MessageXpStore;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;

public class GuiContainerXpStore extends GuiContainerXp
{
    public GuiContainerXpStore(TileEntityXpStore te)
    {
        super(te);
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
        BlockPos pos = ((TileEntity) ((ContainerXp) this.inventorySlots).inventory).getPos();
        if (button.id >= 0 && button.id <= 3) {
            // Player -> Store
            MessageHandler.INSTANCE.sendToServer(new MessageXpStore(
                    pos,
                    MessageXpStore.PLAYER_TO_XP_STORE,
                    (int) Math.pow(10, button.id)
            ));
        } else if (button.id >= 10 && button.id <= 13) {
            // Store -> Player
            MessageHandler.INSTANCE.sendToServer(new MessageXpStore(
                    pos,
                    MessageXpStore.XP_STORE_TO_PLAYER,
                    (int) Math.pow(10, button.id - 10)
            ));
        } else {
            super.actionPerformed(button);
        }
    }
}

package com.ragegamingpe.wtfenchants.common.container;

import com.ragegamingpe.wtfenchants.common.block.te.TileEntityXpStore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerXpStore extends Container
{
    public TileEntityXpStore te;
    public int level;
    public float percentToLevel;

    public ContainerXpStore(TileEntityXpStore te)
    {
        this.te = te;
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        this.broadcastInformation(listener);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (IContainerListener icontainerlistener : this.listeners) {
            this.broadcastInformation(icontainerlistener);
        }
    }

    private void broadcastInformation(IContainerListener listener)
    {
        listener.sendWindowProperty(this, 0, this.te.getHeldLevel());
        listener.sendWindowProperty(this, 1, (int) (this.te.getPercentToNextLevel() * 100));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        super.updateProgressBar(id, data);
        if (id == 0) {
            this.level = data;
        } else if (id == 1) {
            this.percentToLevel = data / 100.0F;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return playerIn.getDistanceSq(this.te.getPos()) <= 64;
    }
}

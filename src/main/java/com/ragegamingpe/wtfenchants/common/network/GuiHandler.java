package com.ragegamingpe.wtfenchants.common.network;

import com.ragegamingpe.wtfenchants.client.gui.container.GuiContainerBookshelf;
import com.ragegamingpe.wtfenchants.client.gui.container.GuiContainerSorter;
import com.ragegamingpe.wtfenchants.common.block.BlockSorter;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityBookshelf;
import com.ragegamingpe.wtfenchants.common.container.ContainerBookshelf;
import com.ragegamingpe.wtfenchants.common.container.ContainerSorter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    public static final int BOOKSHELF = 0;
    public static final int SORTER = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
            case BOOKSHELF:
                return new ContainerBookshelf(player.inventory, (TileEntityBookshelf) te);
            case SORTER:
                return new ContainerSorter(player.inventory, BlockSorter.getAllBookshelvesConnected(world, new BlockPos(x, y, z)), new BlockPos(x, y, z));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
            case BOOKSHELF:
                return new GuiContainerBookshelf(player.inventory, (TileEntityBookshelf) te);
            case SORTER:
                return new GuiContainerSorter(player, BlockSorter.getAllBookshelvesConnected(world, new BlockPos(x, y, z)), new BlockPos(x, y, z));
        }
        return null;
    }
}

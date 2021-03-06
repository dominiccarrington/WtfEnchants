package com.ragegamingpe.wtfenchants.common.network;

import com.ragegamingpe.wtfenchants.client.gui.container.*;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityBookshelf;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntitySorter;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityXpStore;
import com.ragegamingpe.wtfenchants.common.block.te.base.TEBasicExperience;
import com.ragegamingpe.wtfenchants.common.container.ContainerBookshelf;
import com.ragegamingpe.wtfenchants.common.container.ContainerDisenchantment;
import com.ragegamingpe.wtfenchants.common.container.ContainerSorter;
import com.ragegamingpe.wtfenchants.common.container.ContainerXp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    public static final int BOOKSHELF = 0;
    public static final int SORTER = 1;
    public static final int DISENCHANTING_TABLE = 2;
    public static final int XP_STORE = 3;
    public static final int GENERAL_XP = 4;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
            case BOOKSHELF:
                return new ContainerBookshelf(player.inventory, (TileEntityBookshelf) te);
            case SORTER:
                return new ContainerSorter(player.inventory, (TileEntitySorter) te, new BlockPos(x, y, z));
            case DISENCHANTING_TABLE:
                return new ContainerDisenchantment(player, world, new BlockPos(x, y, z));
            case XP_STORE:
                return new ContainerXp((TileEntityXpStore) te);
            case GENERAL_XP:
                return new ContainerXp((TEBasicExperience) te);
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
                return new GuiContainerSorter(player, (TileEntitySorter) te, new BlockPos(x, y, z));
            case DISENCHANTING_TABLE:
                return new GuiContainerDisenchantment(player, world, new BlockPos(x, y, z));
            case XP_STORE:
                return new GuiContainerXpStore((TileEntityXpStore) te);
            case GENERAL_XP:
                return new GuiContainerXp((TEBasicExperience) te);
        }
        return null;
    }
}

package com.ragegamingpe.wtfenchants.common.block;

import com.ragegamingpe.wtfenchants.common.WtfEnchants;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlock;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityBookshelf;
import com.ragegamingpe.wtfenchants.common.lib.ModBlocks;
import com.ragegamingpe.wtfenchants.common.network.GuiHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class BlockSorter extends ModBlock
{
    public BlockSorter()
    {
        super(Material.IRON, "sorter");
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        System.out.println(Arrays.toString(getAllBookshelvesConnected(worldIn, pos)));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote) {
            playerIn.openGui(WtfEnchants.instance, GuiHandler.SORTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    public static IInventory[] getAllBookshelvesConnected(World world, BlockPos pos)
    {
        if (world.getBlockState(pos).getBlock() != ModBlocks.SORTER) return null;

        List<IInventory> bookshelves = new ArrayList<>(); // What I am finding

        List<BlockPos> checkedLocations = new ArrayList<>(); // List of checked BlockPoses
        Queue<BlockPos> currentChecks = new PriorityQueue<>(); // Queue of checks

        currentChecks.add(pos);

        while (currentChecks.size() > 0) {
            BlockPos current = currentChecks.peek();
            if (current == null) break; // WTF? Really, should not happen

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        BlockPos check = current.add(i, j, k);

                        if (checkedLocations.contains(check)) continue;

                        if (world.getBlockState(check).getBlock() == ModBlocks.BOOKSHELF && world.getTileEntity(check) instanceof TileEntityBookshelf) {
                            bookshelves.add((IInventory) world.getTileEntity(check));
                            checkedLocations.add(check);
                            currentChecks.add(check);
                        }
                    }
                }
            }

            currentChecks.remove(current);
        }

        return bookshelves.toArray(new IInventory[0]);
    }
}

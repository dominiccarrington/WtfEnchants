package com.ragegamingpe.wtfenchants.common.enchantment.handler;

import com.google.common.collect.ImmutableList;
import com.ragegamingpe.wtfenchants.common.lib.ModEnchantments;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HandlerHeightWidth
{
    private static final Method RAY_TRACE;
    private static boolean currentlyHandling;
    private static List<ItemStack> storeDrops;

    static {
        RAY_TRACE = ReflectionHelper.findMethod(Item.class, "rayTrace", null, World.class, EntityPlayer.class, boolean.class);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreakBlock(BlockEvent.BreakEvent event)
    {
        if (event.isCanceled()) return;
        EntityPlayer player = event.getPlayer();
        ItemStack heldItem = player.getHeldItemMainhand();

        if (!ForgeHooks.isToolEffective(event.getWorld(), event.getPos(), heldItem))
            return;

        if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.WIDTH, heldItem) == 0 && EnchantmentHelper.getEnchantmentLevel(ModEnchantments.HEIGHT, heldItem) == 0)
            return;

        List<ItemStack> drops = new ArrayList<>();
        handleMine(player, heldItem, event.getState(), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand()), event.getPos(), drops);
        storeDrops = drops;
    }

    public static void handleMine(EntityPlayer player, ItemStack stack, IBlockState state, int fortuneLevel, BlockPos origin, List<ItemStack> drops)
    {
        if (currentlyHandling) return;

        currentlyHandling = true;
        World world = player.getEntityWorld();
        ImmutableList<BlockPos> extraBlocks = calculateAOEBlocks(
                stack,
                world,
                player,
                origin,
                1 + 2 * EnchantmentHelper.getEnchantmentLevel(ModEnchantments.WIDTH, stack),
                1 + 2 * EnchantmentHelper.getEnchantmentLevel(ModEnchantments.HEIGHT, stack),
                1,
                -1,
                false,
                blockPos -> ForgeHooks.isToolEffective(world, blockPos, stack)
        );

        for (BlockPos blockPos : extraBlocks) {
            IBlockState blockState = world.getBlockState(blockPos);

            world.destroyBlock(blockPos, false);
            if (!player.isCreative())
                stack.attemptDamageItem(1, world.rand, (EntityPlayerMP) player);
            drops.add(new ItemStack(blockState.getBlock().getItemDropped(blockState, world.rand, fortuneLevel), blockState.getBlock().quantityDropped(blockState, fortuneLevel, world.rand), blockState.getBlock().damageDropped(blockState)));
        }

        currentlyHandling = false;
    }

    public static List<ItemStack> getDrops()
    {
        List<ItemStack> returnDrops = new ArrayList<>(storeDrops);
        storeDrops.clear();
        return returnDrops;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderBoundingBox(RenderWorldLastEvent event)
    {
        PlayerControllerMP controllerMP = Minecraft.getMinecraft().playerController;
        EntityPlayer player = Minecraft.getMinecraft().player;
        World world = player.getEntityWorld();

        ItemStack heldStack = player.getHeldItemMainhand();

        // AOE preview
        if (heldStack != ItemStack.EMPTY && (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.HEIGHT, heldStack) > 0 || EnchantmentHelper.getEnchantmentLevel(ModEnchantments.WIDTH, heldStack) > 0)) {
            Entity renderEntity = Minecraft.getMinecraft().getRenderViewEntity();
            double distance = controllerMP.getBlockReachDistance();
            assert renderEntity != null;
            RayTraceResult mop = renderEntity.rayTrace(distance, event.getPartialTicks());
            if (mop != null) {
                ImmutableList<BlockPos> extraBlocks = calculateAOEBlocks(
                        heldStack,
                        world,
                        player,
                        mop.getBlockPos(),
                        1 + 2 * EnchantmentHelper.getEnchantmentLevel(ModEnchantments.WIDTH, heldStack),
                        1 + 2 * EnchantmentHelper.getEnchantmentLevel(ModEnchantments.HEIGHT, heldStack),
                        1,
                        -1,
                        false
                );

                for (BlockPos pos : extraBlocks) {
                    event.getContext().drawSelectionBox(player, new RayTraceResult(new Vec3d(0, 0, 0), mop.sideHit, pos), 0, event.getPartialTicks());
                }
            }
        }
    }

    public static ImmutableList<BlockPos> calculateAOEBlocks(EntityPlayer player, BlockPos start, int height, int width, int depth)
    {
        Vec3i vec = player.getHorizontalFacing().getDirectionVec();
//        Vec3i vec = EnumFacing.NORTH.getDirectionVec();
        int x = vec.getX() * height + vec.getZ() * width;
        int y = EnumFacing.UP.getAxisDirection().getOffset() * -depth;
        int z = vec.getX() * width + vec.getZ() * height;
        start = start.add(-x / 2, 0, -z / 2);
        if (x % 2 == 0) {
            if (x > 0) {
                start = start.add(1, 0, 0);
            } else if (x < 0) {
                start = start.add(-1, 0, 0);
            }
        }
        if (z % 2 == 0) {
            if (z > 0) {
                start = start.add(0, 0, 1);
            } else if (z < 0) {
                start = start.add(0, 0, -1);
            }
        }

        ImmutableList.Builder<BlockPos> poses = ImmutableList.builder();
        for (int xp = start.getX(); xp != start.getX() + x; xp += x / MathHelper.abs(x)) {
            for (int yp = start.getY(); yp != start.getY() + y; yp += y / MathHelper.abs(y)) {
                for (int zp = start.getZ(); zp != start.getZ() + z; zp += z / MathHelper.abs(z)) {
                    BlockPos pos = new BlockPos(xp, yp, zp);
                    poses.add(pos);
                }
            }
        }
        return poses.build();
    }

    // Edited from Tinkers Construct
    public static ImmutableList<BlockPos> calculateAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin, int width, int height, int depth, int distance, boolean includeOrigin, Predicate<BlockPos> predicate)
    {
        if (stack == null) {
            return ImmutableList.of();
        }

        // find out where the player is hitting the block
        IBlockState state = world.getBlockState(origin);

        if (state.getMaterial() == Material.AIR) {
            // what are you DOING?
            return ImmutableList.of();
        }

        // raytrace to get the side, but has to result in the same block
        RayTraceResult mop;
        try {
            mop = (RayTraceResult) RAY_TRACE.invoke(stack.getItem(), world, player, true);
            if (mop == null || !origin.equals(mop.getBlockPos())) {
                mop = (RayTraceResult) RAY_TRACE.invoke(stack.getItem(), world, player, false);
                if (mop == null || !origin.equals(mop.getBlockPos())) {
                    return ImmutableList.of();
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return ImmutableList.of();
        }

        // we know the block and we know which side of the block we're hitting. time to calculate the depth along the different axes
        int x, y, z;
        BlockPos start = origin;
        switch (mop.sideHit) {
            case DOWN:
            case UP:
                // x y depends on the angle we look?
                Vec3i vec = player.getHorizontalFacing().getDirectionVec();
                x = vec.getX() * height + vec.getZ() * width;
                y = mop.sideHit.getAxisDirection().getOffset() * -depth;
                z = vec.getX() * width + vec.getZ() * height;
                start = start.add(-x / 2, 0, -z / 2);
                if (x % 2 == 0) {
                    if (x > 0 && mop.hitVec.x - mop.getBlockPos().getX() > 0.5d) {
                        start = start.add(1, 0, 0);
                    } else if (x < 0 && mop.hitVec.x - mop.getBlockPos().getX() < 0.5d) {
                        start = start.add(-1, 0, 0);
                    }
                }
                if (z % 2 == 0) {
                    if (z > 0 && mop.hitVec.z - mop.getBlockPos().getZ() > 0.5d) {
                        start = start.add(0, 0, 1);
                    } else if (z < 0 && mop.hitVec.z - mop.getBlockPos().getZ() < 0.5d) {
                        start = start.add(0, 0, -1);
                    }
                }
                break;
            case NORTH:
            case SOUTH:
                x = width;
                y = height;
                z = mop.sideHit.getAxisDirection().getOffset() * -depth;
                start = start.add(-x / 2, -y / 2, 0);
                if (x % 2 == 0 && mop.hitVec.x - mop.getBlockPos().getX() > 0.5d) {
                    start = start.add(1, 0, 0);
                }
                if (y % 2 == 0 && mop.hitVec.y - mop.getBlockPos().getY() > 0.5d) {
                    start = start.add(0, 1, 0);
                }

                start = start.up(height / 2 - 1);
                break;
            case WEST:
            case EAST:
                x = mop.sideHit.getAxisDirection().getOffset() * -depth;
                y = height;
                z = width;
                start = start.add(-0, -y / 2, -z / 2);
                if (y % 2 == 0 && mop.hitVec.y - mop.getBlockPos().getY() > 0.5d) {
                    start = start.add(0, 1, 0);
                }
                if (z % 2 == 0 && mop.hitVec.z - mop.getBlockPos().getZ() > 0.5d) {
                    start = start.add(0, 0, 1);
                }
                start = start.up(height / 2 - 1);
                break;
            default:
                x = y = z = 0;
        }

        ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
        for (int xp = start.getX(); xp != start.getX() + x; xp += x / MathHelper.abs(x)) {
            for (int yp = start.getY(); yp != start.getY() + y; yp += y / MathHelper.abs(y)) {
                for (int zp = start.getZ(); zp != start.getZ() + z; zp += z / MathHelper.abs(z)) {
                    if (!includeOrigin && xp == origin.getX() && yp == origin.getY() && zp == origin.getZ()) {
                        continue;
                    }
                    if (distance > 0 && MathHelper.abs(xp - origin.getX()) + MathHelper.abs(yp - origin.getY()) + MathHelper.abs(zp - origin.getZ()) > distance) {
                        continue;
                    }
                    BlockPos pos = new BlockPos(xp, yp, zp);
                    if (predicate.test(pos)) {
                        builder.add(pos);
                    }
                }
            }
        }

        return builder.build();
    }

    public static ImmutableList<BlockPos> calculateAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin, int width, int height, int depth, int distance, boolean includeOrigin)
    {
        return calculateAOEBlocks(stack, world, player, origin, width, height, depth, distance, includeOrigin, blockPos -> true);
    }
}

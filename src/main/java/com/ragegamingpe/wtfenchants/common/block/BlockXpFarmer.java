package com.ragegamingpe.wtfenchants.common.block;

import com.ragegamingpe.wtfenchants.common.block.base.ModBlockContainer;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityXpFarmer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

import static com.ragegamingpe.wtfenchants.common.block.te.TileEntityXpFarmer.FAKE_PLAYER_NAME;

public class BlockXpFarmer extends ModBlockContainer
{
    public static int configRadius;
    public static double configXpPenalty;

    public BlockXpFarmer()
    {
        super(Material.IRON, "xp_farmer");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityXpFarmer();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void onExperienceDrops(LivingExperienceDropEvent event)
    {
        if (event.getAttackingPlayer() instanceof FakePlayer) {
            if (event.getAttackingPlayer().getGameProfile().getName() == FAKE_PLAYER_NAME) {
                event.setDroppedExperience((int) Math.floor(event.getOriginalExperience() * configXpPenalty));
            }
        }
    }
}

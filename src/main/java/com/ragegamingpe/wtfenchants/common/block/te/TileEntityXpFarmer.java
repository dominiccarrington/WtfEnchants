package com.ragegamingpe.wtfenchants.common.block.te;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.ref.WeakReference;
import java.util.UUID;

import static com.ragegamingpe.wtfenchants.common.block.BlockXpFarmer.configRadius;

public class TileEntityXpFarmer extends TileEntity implements ITickable
{
    public static final String FAKE_PLAYER_NAME = "xp_farmer";

    private WeakReference<FakePlayer> fakePlayer;

    @Override
    public void update()
    {
        world.getEntitiesWithinAABB(
                EntityLiving.class,
                new AxisAlignedBB(this.pos.add(configRadius, configRadius, configRadius), this.pos.add(-configRadius, -configRadius, -configRadius)),
                (entity) -> {
                    int recentlyHit = ReflectionHelper.getPrivateValue(EntityLivingBase.class, entity, "recentlyHit");

                    return recentlyHit <= 0;
                }
        ).forEach((entity) -> {
            ReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, getFakePlayer(), "attackingPlayer");
            ReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, 200, "recentlyHit");
        });
    }

    public FakePlayer getFakePlayer()
    {
        if (fakePlayer == null && this.world != null && !this.world.isRemote) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), FAKE_PLAYER_NAME);
            fakePlayer = new WeakReference<>(FakePlayerFactory.get((WorldServer) this.world, profile));
        }
        return fakePlayer == null ? null : fakePlayer.get();
    }
}

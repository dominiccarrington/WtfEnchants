package com.ragegamingpe.wtfenchants.common.block.te;

import com.mojang.authlib.GameProfile;
import com.ragegamingpe.wtfenchants.common.block.te.base.TEBasicExperience;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class TileEntityXpStore extends TEBasicExperience
{
    public int renderXpColor = 0;

    @Override
    public FakePlayer getFakePlayer()
    {
        if (fakePlayer == null && this.world != null && !this.world.isRemote) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "xp_store");
            fakePlayer = new WeakReference<>(FakePlayerFactory.get((WorldServer) this.world, profile));
        }
        return fakePlayer.get();
    }
}

package com.ragegamingpe.wtfenchants.common.block.te;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class TileEntityXpStore extends TileEntity
{
    private WeakReference<FakePlayer> fakePlayer;
    private NBTTagCompound delayedInitNbt;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        compound.setFloat("XpP", this.getFakePlayer().experience);
        compound.setInteger("XpLevel", this.getFakePlayer().experienceLevel);
        compound.setInteger("XpTotal", this.getFakePlayer().experienceTotal);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if (this.world == null) {
            this.delayedInitNbt = compound;
        } else {
            if (!this.world.isRemote) {
                this.getFakePlayer().experience = compound.getFloat("XpP");
                this.getFakePlayer().experienceLevel = compound.getInteger("XpLevel");
                this.getFakePlayer().experienceTotal = compound.getInteger("XpTotal");
            }
        }
    }

    @Override
    public void setWorld(World worldIn)
    {
        super.setWorld(worldIn);

        if (delayedInitNbt != null) {
            this.readFromNBT(delayedInitNbt);
            delayedInitNbt = null;
        }
    }

    public int getHeldLevel()
    {
        return this.getFakePlayer().experienceLevel;
    }

    public float getPercentToNextLevel()
    {
        return this.getFakePlayer().experience;
    }

    public int getExperienceTotal()
    {
        return this.getFakePlayer().experienceTotal;
    }

    public void addExperienceLevels(int levels)
    {
        this.getFakePlayer().addExperienceLevel(levels);
    }

    public void addExperience(int amount)
    {
        this.getFakePlayer().addExperience(amount);
    }

    public FakePlayer getFakePlayer()
    {
        if (fakePlayer == null && this.world != null && !this.world.isRemote) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "xp_store");
            fakePlayer = new WeakReference<>(FakePlayerFactory.get((WorldServer) this.world, profile));
        }
        return fakePlayer.get();
    }
}

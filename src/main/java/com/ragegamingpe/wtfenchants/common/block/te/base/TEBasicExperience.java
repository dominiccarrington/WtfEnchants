package com.ragegamingpe.wtfenchants.common.block.te.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import java.lang.ref.WeakReference;

public abstract class TEBasicExperience extends TileEntity
{
    protected WeakReference<FakePlayer> fakePlayer;
    protected NBTTagCompound delayedInitNbt;

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

    public int getTotalExperience()
    {
        return calculatePlayerExperience(this.getFakePlayer());
    }

    public void addExperienceLevel(int levels)
    {
        if (levels <= 0) {
            this.getFakePlayer().addExperienceLevel(levels);
        } else {
            for (int i = 0; i < levels; i++) {
                this.addExperience(calculateXpCap(this.getHeldLevel() + i));
            }
        }
        this.markDirty();
    }

    public int addExperience(int amount)
    {
        FakePlayer player = this.getFakePlayer();
        int currentXp = calculatePlayerExperience(player);

        currentXp += amount;

        int leftOver = 0;
        if (currentXp > this.getMaximumStorage()) {
            leftOver = currentXp - this.getMaximumStorage();
            currentXp = this.getMaximumStorage();
        }

        player.addExperienceLevel(-Integer.MAX_VALUE);
        player.addExperience(currentXp);

        this.markDirty();

        return leftOver;
    }

    public float getPercentToNextLevel()
    {
        return this.getFakePlayer().experience;
    }

    /**
     * Store must be returned as the experience rather than the level
     */
    public int getMaximumStorage()
    {
        return Integer.MAX_VALUE;
    }

    public boolean isFull()
    {
        return this.getTotalExperience() >= this.getMaximumStorage();
    }

    public int getStorageRemaining()
    {
        return this.getMaximumStorage() - this.getTotalExperience();
    }

    public int getComparatorOutput()
    {
        return (int) (((double) this.getTotalExperience() / (double) this.getMaximumStorage()) * (double) 15);
    }

    public abstract FakePlayer getFakePlayer();

    public static int calculatePlayerExperience(EntityPlayer player)
    {
        if (player.experienceTotal > 0) return player.experienceTotal;

        int xpPoints = 0;

        for (int i = 0; i < player.experienceLevel; i++) {
            xpPoints += calculateXpCap(i);
        }

        return xpPoints + (int) Math.floor(player.experience);
    }

    public static int calculateXpCap(int level)
    {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }
}

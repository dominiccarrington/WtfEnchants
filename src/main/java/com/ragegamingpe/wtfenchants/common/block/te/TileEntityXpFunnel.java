package com.ragegamingpe.wtfenchants.common.block.te;

import com.mojang.authlib.GameProfile;
import com.ragegamingpe.wtfenchants.common.block.BlockXpFunnel;
import com.ragegamingpe.wtfenchants.common.block.te.base.TEBasicExperience;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

public class TileEntityXpFunnel extends TEBasicExperience implements ITickable
{
    public static int configTransferPerOperation;
    private WeakReference<FakePlayer> fakePlayer;
    private int cooldown;

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.cooldown = compound.getInteger("Cooldown");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound nbt = super.writeToNBT(compound);
        compound.setInteger("Cooldown", this.cooldown);
        return nbt;
    }

    @Override
    public void update()
    {
        if (this.world == null || this.world.isRemote) return;

        this.cooldown--;
        if (this.cooldown <= 0) {
            if (BlockXpFunnel.isEnabled(this.world.getBlockState(pos)))
                this.handleXpTransfer();
            this.cooldown = 8;
        }

        if (!this.isFull()) {
            List<EntityXPOrb> orbList = this.world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(this.pos.up()));
            for (EntityXPOrb orb : orbList) {
                orb.xpValue = this.addExperience(orb.xpValue);
                if (orb.xpValue <= 0) orb.setDead();
            }
        }
    }

    private void handleXpTransfer()
    {
        if (this.getTotalExperience() <= 0) {
            return;
        }

        EnumFacing facing = BlockXpFunnel.getFacing(this.getBlockMetadata());
        BlockPos check = pos.offset(facing);

        if (this.world.isBlockLoaded(check) &&
                this.world.getTileEntity(check) instanceof TEBasicExperience) {
            TEBasicExperience receiver = (TEBasicExperience) this.world.getTileEntity(check);
            assert receiver != null;

            if (receiver.isFull()) return;

            int amount = Math.min(configTransferPerOperation, this.getTotalExperience());
            amount = Math.min(amount, receiver.getStorageRemaining());

            this.addExperience(-amount);
            receiver.addExperience(amount);
        }
    }

    @Override
    public int getMaximumStorage()
    {
        int sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += calculateXpCap(i);
        }

        return sum;
    }

    @Override
    public FakePlayer getFakePlayer()
    {
        if (fakePlayer == null && this.world != null && !this.world.isRemote) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "xp_funnel");
            fakePlayer = new WeakReference<>(FakePlayerFactory.get((WorldServer) this.world, profile));
        }
        return fakePlayer.get();
    }
}

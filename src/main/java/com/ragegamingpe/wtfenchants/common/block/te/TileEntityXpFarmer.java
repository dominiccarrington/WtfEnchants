package com.ragegamingpe.wtfenchants.common.block.te;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import static com.ragegamingpe.wtfenchants.common.block.BlockXpFarmer.configRadius;

public class TileEntityXpFarmer extends TileEntity implements ITickable
{

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
            if (entity.isServerWorld()) {
                ReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, FakePlayerFactory.getMinecraft((WorldServer) entity.getEntityWorld()), "attackingPlayer");
                ReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, 200, "recentlyHit");
            }
        });
    }
}

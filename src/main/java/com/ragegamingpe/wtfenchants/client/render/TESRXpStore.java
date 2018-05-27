package com.ragegamingpe.wtfenchants.client.render;

import com.ragegamingpe.wtfenchants.common.block.te.TileEntityXpStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityXPOrb;

public class TESRXpStore extends TileEntitySpecialRenderer<TileEntityXpStore>
{
    private static EntityXPOrb entity;

    @Override
    public void render(TileEntityXpStore te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y, (float) z + 0.5F);
        renderMob(te, x, y, z, partialTicks);
        GlStateManager.popMatrix();
    }

    /**
     * Render the mob inside the mob spawner.
     */
    public static void renderMob(TileEntityXpStore te, double posX, double posY, double posZ, float partialTicks)
    {
//        Entity entity = mobSpawnerLogic.getCachedEntity();
        if (entity == null)
            entity = new EntityXPOrb(Minecraft.getMinecraft().world, posX, posY, posZ, 2477);

        entity.xpColor++;
        float f = 0.53125F;
        float f1 = Math.max(entity.width, entity.height);

        if ((double) f1 > 1.0D) {
            f /= f1;
        }

        GlStateManager.translate(0.0F, 0.4F, 0.0F);
//            GlStateManager.rotate((float)(mobSpawnerLogic.getPrevMobRotation() + (mobSpawnerLogic.getMobRotation() - mobSpawnerLogic.getPrevMobRotation()) * (double)partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
//        GlStateManager.translate(0.0F, 0.1F, 0.0F);
//        GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(f, f, f);
        entity.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
        Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
    }
}

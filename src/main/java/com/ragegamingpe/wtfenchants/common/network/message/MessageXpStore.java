package com.ragegamingpe.wtfenchants.common.network.message;

import com.ragegamingpe.wtfenchants.common.block.te.TileEntityXpStore;
import com.ragegamingpe.wtfenchants.common.network.Message;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageXpStore extends Message
{
    public static final int PLAYER_TO_XP_STORE = 0;
    public static final int XP_STORE_TO_PLAYER = 1;

    public BlockPos pos;
    public int action;
    public int amount;

    public MessageXpStore()
    {

    }

    public MessageXpStore(BlockPos pos, int action, int amount)
    {
        this.pos = pos;
        this.action = action;
        this.amount = amount;
    }

    @Override
    public void handleMessage(MessageContext context)
    {
        EntityPlayerMP playerMP = context.getServerHandler().player;
        World world = playerMP.getEntityWorld();

        if (world.isBlockLoaded(this.pos)) { // Double check that the block is loaded
            TileEntity te = world.getTileEntity(this.pos);

            if (te instanceof TileEntityXpStore) {
                int playerXP = calculatePlayerExperience(playerMP);
                int tileXP = calculatePlayerExperience(((TileEntityXpStore) te).getFakePlayer());

                if (this.action == PLAYER_TO_XP_STORE) {
                    for (int i = 0; i < this.amount && playerXP > 0; i++) {
                        int lvl = ((TileEntityXpStore) te).getHeldLevel() + i;
                        int amount = Math.min(calculateXpCap(lvl), playerXP);

                        playerXP -= amount;
                        tileXP += amount;
                    }
                } else if (this.action == XP_STORE_TO_PLAYER) {
                    for (int i = 0; i < this.amount && tileXP > 0; i++) {
                        int lvl = playerMP.experienceLevel + i;
                        int amount = Math.min(calculateXpCap(lvl), tileXP);

                        playerXP += amount;
                        tileXP -= amount;
                    }
                }

                playerMP.addExperienceLevel(-Integer.MAX_VALUE);
                playerMP.addExperience(playerXP);
                ((TileEntityXpStore) te).addExperienceLevels(-Integer.MAX_VALUE);
                ((TileEntityXpStore) te).addExperience(tileXP);

                te.markDirty();
            }
        }
    }

    private int calculatePlayerExperience(EntityPlayerMP playerMP)
    {
        int xpPoints = 0;

        for (int i = 0; i < playerMP.experienceLevel; i++) {
            xpPoints += calculateXpCap(i);
        }

        return xpPoints + (playerMP.experienceTotal > 0 ? playerMP.experienceTotal - xpPoints : 0);
    }

    private int calculateXpCap(int level)
    {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }
}

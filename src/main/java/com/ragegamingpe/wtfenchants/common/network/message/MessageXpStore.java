package com.ragegamingpe.wtfenchants.common.network.message;

import com.ragegamingpe.wtfenchants.common.block.te.base.TEBasicExperience;
import com.ragegamingpe.wtfenchants.common.network.Message;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.ragegamingpe.wtfenchants.common.block.te.base.TEBasicExperience.calculatePlayerExperience;
import static com.ragegamingpe.wtfenchants.common.block.te.base.TEBasicExperience.calculateXpCap;

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

            if (te instanceof TEBasicExperience) {
                int playerXP = calculatePlayerExperience(playerMP);
                int tileXP = ((TEBasicExperience) te).getTotalExperience();

                if (this.action == PLAYER_TO_XP_STORE) {
                    for (int i = 0; i < this.amount && playerXP > 0; i++) {
                        int lvl = ((TEBasicExperience) te).getHeldLevel() + i;
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

                ((TEBasicExperience) te).addExperienceLevel(-Integer.MAX_VALUE);
                playerXP += ((TEBasicExperience) te).addExperience(tileXP);
                playerMP.addExperienceLevel(-Integer.MAX_VALUE);
                playerMP.addExperience(playerXP);
                playerMP.addScore(-playerXP);
            }
        }
    }
}

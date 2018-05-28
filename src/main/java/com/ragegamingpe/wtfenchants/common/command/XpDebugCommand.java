package com.ragegamingpe.wtfenchants.common.command;

import com.ragegamingpe.wtfenchants.common.block.te.base.TEBasicExperience;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.List;

public class XpDebugCommand extends CommandBase
{
    @Override
    public String getName()
    {
        return "wtf_xp";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayer player;
        TEBasicExperience teBasicExperience = null;
        if (args.length >= 3) {
            World world = server.getEntityWorld();
            BlockPos pos = parseBlockPos(sender, args, 0, false);

            if (world.isBlockLoaded(pos)) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TEBasicExperience) {
                    teBasicExperience = (TEBasicExperience) te;
                    player = teBasicExperience.getFakePlayer();
                } else {
                    throw new CommandException("Tile Entity must be a TEBasicExperience");
                }
            } else {
                throw new CommandException("Block must be loaded");
            }
        } else {
            player = getCommandSenderAsPlayer(sender);
        }

        if (args.length == 0 || args.length == 3) {
            sender.sendMessage(new TextComponentString("==== " + player.toString() + " ===="));
            sender.sendMessage(new TextComponentString("XP: " + player.experience));
            sender.sendMessage(new TextComponentString("XP Level: " + player.experienceLevel));
            sender.sendMessage(new TextComponentString("XP Total: " + player.experienceTotal));

            if (player instanceof FakePlayer && teBasicExperience != null) {
                sender.sendMessage(new TextComponentString("Max Store: " + teBasicExperience.getMaximumStorage()));
                sender.sendMessage(new TextComponentString("Is Full: " + teBasicExperience.isFull()));
            }
        } else if (args.length == 5) {
            String action = args[3];
            String amount = args[4];

            switch (action) {
                case "add":
                    if (amount.toLowerCase().contains("l")) {
                        int leftOver = teBasicExperience.addExperienceLevel(Integer.valueOf(amount.substring(0, amount.length() - 1)));
                        sender.sendMessage(new TextComponentString("Added " + amount + " experience levels to TE"));
                        sender.sendMessage(new TextComponentString("Left Over: " + leftOver));
                    } else {
                        int leftOver = teBasicExperience.addExperience(Integer.valueOf(amount));
                        sender.sendMessage(new TextComponentString("Added " + amount + " experience to TE"));
                        sender.sendMessage(new TextComponentString("Left Over: " + leftOver));
                    }
                    break;
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length > 0 && args.length <= 3) {
            return getTabCompletionCoordinate(args, 0, targetPos);
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }
}

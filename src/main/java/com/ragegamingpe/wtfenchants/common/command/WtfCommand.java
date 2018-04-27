package com.ragegamingpe.wtfenchants.common.command;

import com.ragegamingpe.wtfenchants.common.enchantment.WtfEnchantEnchantment;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.ragegamingpe.wtfenchants.common.enchantment.WtfEnchantEnchantment.EVENTS;

public class WtfCommand extends CommandBase
{
    @Override
    public String getName()
    {
        return "wtf_enchant";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/wtf_enchant <event>|! <command>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayer player = getCommandSenderAsPlayer(sender);

        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("Valid Events: "));
            for (Pair<Integer, String> event : EVENTS.keySet())
                sender.sendMessage(new TextComponentString(event.getRight() + " - " + event.getLeft()));
        } else {
            if (args[0].equals("!")) {
                // Subcommand
                if (args[1].equals("regenerate_weights")) {
                    sender.sendMessage(new TextComponentString("Recalculating!"));
                    WtfEnchantEnchantment.calculateWeights();
                }
            } else {
                boolean flag = false;
                for (Pair<Integer, String> event : EVENTS.keySet()) {
                    if (event.getRight().equals(args[0])) {
                        EVENTS.get(event).accept(player);
                        flag = true;
                    }
                }

                if (!flag) {
                    throw new CommandException("Event not found.");
                }
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 0 || args.length == 1) {
            String[] keys = new String[EVENTS.size()];

            int i = 0;
            for (Pair<Integer, String> event : EVENTS.keySet()) {
                keys[i] = event.getRight();
                i++;
            }

            return getListOfStringsMatchingLastWord(args, keys);
        }

        return Collections.emptyList();
    }
}

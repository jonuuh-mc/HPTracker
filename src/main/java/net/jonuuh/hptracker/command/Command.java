package net.jonuuh.hptracker.command;

import net.jonuuh.hptracker.config.Config;
import net.jonuuh.hptracker.config.TargetPlayerNameSet;
import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.regex.Pattern;

public class Command extends CommandBase
{
    private static final Pattern PLAYERNAME = Pattern.compile("^\\w{3,16}$");
    private static final Pattern TIME = Pattern.compile("^\\d{1,3}$");
    private static final Pattern HEXCOLOR = Pattern.compile("^(0x)?[0-9a-fA-F]{6}$");
    private static final Pattern FLOAT = Pattern.compile("^[+-]?\\d+\\.?\\d*$");

    private final Minecraft mc;
    private final Config config;
    private final TargetPlayerNameSet<String> targetPlayerNames;

    public Command(Minecraft mc, Config config)
    {
        this.mc = mc;
        this.config = config;
        this.targetPlayerNames = config.getTargetPlayerNames();
    }

    /**
     * Gets the name of the command
     */
    @Override
    public String getCommandName()
    {
        return "hptracker";
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender The command sender that executed the command
     */
    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "";
    }

    /**
     * Gets the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The command sender that executed the command
     * @param args   The arguments that were passed
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (!(sender.getCommandSenderEntity() instanceof EntityPlayerSP))
        {
            return;
        }

        if (args.length == 0)
        {
            displayCommandHelp();
            return;
        }

        switch (Commands.valueOf(args[0].toLowerCase()))
        {
            case config:
                if (args.length == 1)
                {
                    config.displayConfig();
                }
                break;
            case list:
                if (args.length == 1)
                {
                    Utilities.addChatMessage(mc, targetPlayerNames.toString());
                }
                break;
            case add:
                if (args.length == 2 && PLAYERNAME.matcher(args[1]).matches())
                {
                    Utilities.addChatMessage(mc, String.valueOf(targetPlayerNames.add(args[1])));
                }
                break;
            case remove:
                if (args.length == 2 && PLAYERNAME.matcher(args[1]).matches())
                {
                    Utilities.addChatMessage(mc, String.valueOf(targetPlayerNames.remove(args[1])));
                }
                break;
            case addall:
                // TODO
                break;
            case removeall:
                if (args.length == 1)
                {
                    targetPlayerNames.clear();
                }
                break;
            case sethp:
                if (args.length == 2 && FLOAT.matcher(args[1]).matches())
                {
                    config.setThresholdHPPercent(Integer.parseInt(args[1]));
                }
                break;
            case setscale:
                if (args.length == 2 && FLOAT.matcher(args[1]).matches())
                {
                    config.setRenderScale(Float.parseFloat(args[1]));
                }
                break;
            case setoffset:
                if (args.length == 2 && FLOAT.matcher(args[1]).matches())
                {
                    config.setRenderYOffset(Float.parseFloat(args[1]));
                }
                break;
            default:
                displayCommandHelp();
                break;
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, Commands.getNames());
        }
        else if (args.length == 2 && (args[0].equals(Commands.add.name()) || args[0].equals(Commands.remove.name())))
        {
            if (args[0].equals(Commands.add.name()))
            {
                return getListOfStringsMatchingLastWord(args, Utilities.getOnlinePlayerNames(mc));
            }
            if (args[0].equals(Commands.remove.name()))
            {
                return getListOfStringsMatchingLastWord(args, targetPlayerNames);
            }
        }
        return null;
    }

    private void displayCommandHelp()
    {
        Utilities.addChatMessage(mc, "[HPTracker]", EnumChatFormatting.GOLD);
        for (String command : Commands.getNames())
        {
            Utilities.addChatMessage(mc, "/hptracker " + command, EnumChatFormatting.DARK_GRAY);
        }
        Utilities.addChatMessage(mc, "[HPTracker]", EnumChatFormatting.GOLD);
    }
}


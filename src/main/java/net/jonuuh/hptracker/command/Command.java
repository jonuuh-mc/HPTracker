package net.jonuuh.hptracker.command;

import net.jonuuh.hptracker.config.Config;
import net.jonuuh.hptracker.config.TargetPlayerNameSet;
import net.jonuuh.hptracker.util.ChatLogger;
import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class Command extends CommandBase
{
    private final Minecraft mc;
    private final ChatLogger chatLogger;
    private final Config config;
    private final TargetPlayerNameSet<String> targetPlayerNames;

    public Command(Minecraft mc, ChatLogger chatLogger, Config config)
    {
        this.mc = mc;
        this.chatLogger = chatLogger;
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

        Commands command = Commands.none;
        try
        {
            command = Commands.valueOf(args[0].toLowerCase());
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }

        switch (command)
        {
            case config:
                if (isArgsLenUnexpected(args, 1))
                {
                    break;
                }
                config.displayConfig();
                break;

            case add:
                if (isArgsLenUnexpected(args, 2))
                {
                    break;
                }
                else if (!Utilities.getOnlinePlayerNames(mc).contains(args[1]))
                {
                    chatLogger.addFailureLog("Player not found.");
                    break;
                }
                else if (targetPlayerNames.contains(args[1]))
                {
                    chatLogger.addFailureLog("Player already in target players.");
                    break;
                }
                targetPlayerNames.add(args[1]);
                chatLogger.addSuccessLog("Added \"" + args[1] + "\" to target players.");
                break;

            case remove:
                if (isArgsLenUnexpected(args, 2))
                {
                    break;
                }
                else if (!targetPlayerNames.contains(args[1]))
                {
                    chatLogger.addFailureLog("Player not in target players.");
                    break;
                }
                targetPlayerNames.remove(args[1]);
                chatLogger.addSuccessLog("Removed \"" + args[1] + "\" from target players.");
                break;

            case addteam:
                if (isArgsLenUnexpected(args, 1))
                {
                    break;
                }
                targetPlayerNames.addAll(Utilities.getPlayerSPTeammates(mc));
                chatLogger.addSuccessLog("Added all teammates to target players.");
                break;

            case clear:
                if (isArgsLenUnexpected(args, 1))
                {
                    break;
                }
                targetPlayerNames.clear();
                chatLogger.addSuccessLog("Cleared target players.");
                break;

            case sethp:
                if (isArgsLenUnexpected(args, 2))
                {
                    break;
                }
                try
                {
                    int hpPercent = parseInt(args[1], 0, 100);
                    config.setThresholdHPPercent(hpPercent);
                    chatLogger.addSuccessLog("Set threshold hp to: " + hpPercent + "%");
                }
                catch (NumberInvalidException e)
                {
                    chatLogger.addFailureLog("Invalid number. bounds = (0, 100)");
                }
                break;

            case setmaxdist:
                if (isArgsLenUnexpected(args, 2))
                {
                    break;
                }
                try
                {
                    double maxDist = parseDouble(args[1], 0, 50);
                    config.setMaxDistance((float) maxDist);
                    chatLogger.addSuccessLog("Set max distance to: " + maxDist + "m");
                }
                catch (NumberInvalidException e)
                {
                    chatLogger.addFailureLog("Invalid number. bounds = (0, 50)");
                }
                break;

            case setscale:
                if (isArgsLenUnexpected(args, 2))
                {
                    break;
                }
                try
                {
                    double renderScale = parseDouble(args[1], 0, 10);
                    config.setRenderScale((float) renderScale);
                    chatLogger.addSuccessLog("Set render scale to: " + renderScale);
                }
                catch (NumberInvalidException e)
                {
                    chatLogger.addFailureLog("Invalid number. bounds = (0, 10)");
                }
                break;

            case setoffset:
                if (isArgsLenUnexpected(args, 2))
                {
                    break;
                }
                try
                {
                    double renderOffset = parseDouble(args[1], -100, 100);
                    config.setRenderYOffset((float) renderOffset);
                    chatLogger.addSuccessLog("Set render offset to: " + renderOffset);
                }
                catch (NumberInvalidException e)
                {
                    chatLogger.addFailureLog("Invalid number. bounds = (-100, 100)");
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
        else if (args.length == 2)
        {
            if (args[0].equals(Commands.add.name()))
            {
                return getListOfStringsMatchingLastWord(args, Utilities.getOnlinePlayerNames(mc));
            }
            else if (args[0].equals(Commands.remove.name()))
            {
                return getListOfStringsMatchingLastWord(args, targetPlayerNames);
            }
        }
        return null;
    }

    private boolean isArgsLenUnexpected(String[] args, int expectedLen)
    {
        if (args.length != expectedLen)
        {
            chatLogger.addFailureLog("Command failed, wrong number of arguments.");
            return true;
        }
        return false;
    }

    private void displayCommandHelp()
    {
        chatLogger.addCenteredLogNoHeader(getWhiteComp(" HPTracker Commands "), '-', new ChatStyle().setColor(EnumChatFormatting.GOLD).setStrikethrough(true));
        for (String command : Commands.getNames())
        {
            chatLogger.addCenteredLogNoHeader(getWhiteComp("/hptracker ").appendSibling(getGoldComp(command)));
        }
        chatLogger.addCenteredLogNoHeader(getWhiteComp(""), '-', new ChatStyle().setColor(EnumChatFormatting.GOLD).setStrikethrough(true));
    }

    private ChatComponentText getGoldComp(String s)
    {
        return new ChatComponentText(EnumChatFormatting.GOLD + s);
    }

    private ChatComponentText getWhiteComp(String s)
    {
        return new ChatComponentText(EnumChatFormatting.WHITE + s);
    }

    private enum Commands
    {
        none,
        config,
        add,
        remove,
        addteam,
        clear,
        sethp,
        setmaxdist,
        setscale,
        setoffset;

        private static String[] getNames()
        {
            return new String[]{config.name(), add.name(), remove.name(), addteam.name(), clear.name(),
                    sethp.name(), setmaxdist.name(), setscale.name(), setoffset.name()};
        }
    }

}


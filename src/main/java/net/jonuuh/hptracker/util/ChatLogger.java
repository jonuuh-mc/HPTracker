package net.jonuuh.hptracker.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
// TODO: this class should probably be static
/**
 * Logs info client-side only in the in-game chat
 */
public class ChatLogger
{
    private final Minecraft mc;
    private final ChatComponentText headerComp;

    /**
     * Instantiates a new ChatLogger.
     *
     * @param mc      the minecraft object
     * @param header  the ChatLogger's header
     * @param mainC   the header main color
     * @param accentC the header accent color
     * @param isBold  whether the header is bold
     */
    public ChatLogger(Minecraft mc, String header, EnumChatFormatting mainC, EnumChatFormatting accentC, boolean isBold)
    {
        this.mc = mc;
        ChatComponentText headerComp = new ChatComponentText(header);
        headerComp.getChatStyle().setColor(mainC).setBold(isBold);

        ChatComponentText openBrComp = new ChatComponentText("[");
        openBrComp.getChatStyle().setColor(accentC).setBold(isBold);

        ChatComponentText closeBrComp = new ChatComponentText("] ");
        closeBrComp.getChatStyle().setColor(accentC).setBold(isBold);

        openBrComp.appendSibling(headerComp).appendSibling(closeBrComp);
        this.headerComp = openBrComp;
    }

    /**
     * Add a log to the ChatLogger.
     *
     * @param log the log
     */
    public void addLog(String log)
    {
        addLog(log, EnumChatFormatting.WHITE, false);
    }

    public void addSuccessLog(String log)
    {
        addLog(log, EnumChatFormatting.GREEN, false);
    }

    public void addFailureLog(String log)
    {
        addLog(log, EnumChatFormatting.RED, false);
    }

    /**
     * Add a log to the ChatLogger.
     *
     * @param log    the log
     * @param color  the log color
     * @param isBold whether the log is bold
     */
    public void addLog(String log, EnumChatFormatting color, boolean isBold)
    {
        if (mc.thePlayer == null)
        {
            return;
        }

        ChatComponentText logComp = new ChatComponentText(log);
        logComp.getChatStyle().setColor(color).setBold(isBold);

        mc.thePlayer.addChatMessage(headerComp.createCopy().appendSibling(logComp));
    }

    public void addLogNoHeader(IChatComponent logComponent)
    {
        if (mc.thePlayer == null)
        {
            return;
        }
        mc.thePlayer.addChatMessage(logComponent);
    }

    public void addCenteredLogNoHeader(IChatComponent logComponent)
    {
        ChatComponentText padding = new ChatComponentText(getPaddingToCenter(logComponent.getUnformattedText(), ' '));
        addLogNoHeader(padding.appendSibling(logComponent));
    }

    public void addCenteredLogNoHeader(IChatComponent logComponent, char paddingChar, ChatStyle paddingStyle)
    {
        ChatComponentText padding = new ChatComponentText(getPaddingToCenter(logComponent.getUnformattedText(), paddingChar));
        padding.setChatStyle(paddingStyle);
        addLogNoHeader(padding.createCopy().appendSibling(logComponent).appendSibling(padding));
    }

    private String getPaddingToCenter(String log, char paddingChar) // doesn't quite work with even #'ed logs
    {
        int chatWidth = mc.ingameGUI.getChatGUI().getChatWidth();
        int logWidth = mc.fontRendererObj.getStringWidth(log);
        int paddingCharWidth = mc.fontRendererObj.getStringWidth(String.valueOf(paddingChar));

        if (logWidth >= chatWidth)
        {
            return "";
        }

        char[] padding = new char[(((chatWidth - logWidth) / paddingCharWidth) - 1) / 2];
        Arrays.fill(padding, paddingChar);
        return new String(padding);
    }
}
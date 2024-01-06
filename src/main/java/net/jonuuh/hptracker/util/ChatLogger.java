package net.jonuuh.hptracker.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

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
    // TODO: make logs prettier (center them, more colors and stuff)
}
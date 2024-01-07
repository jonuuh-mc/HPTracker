package net.jonuuh.hptracker.config;

import net.jonuuh.hptracker.util.ChatLogger;
import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.stream.Collectors;

/**
 * Main config.
 */
public class Config
{
    private final Minecraft mc;
    private final ChatLogger chatLogger;
    private final TargetPlayerNameSet<String> targetPlayerNames;
    private int thresholdHPPercent;
    private float maxDistance;
    private float renderScale;
    private float renderYOffset;

    public Config(Minecraft mc, ChatLogger chatLogger)
    {
        this.mc = mc;
        this.chatLogger = chatLogger;
        this.targetPlayerNames = new TargetPlayerNameSet<>(mc);
        this.thresholdHPPercent = 50;
        this.maxDistance = 15;
        this.renderScale = 1.0F;
        this.renderYOffset = -40.0F;
    }

    public TargetPlayerNameSet<String> getTargetPlayerNames()
    {
        return targetPlayerNames;
    }

    public int getThresholdHPPercent()
    {
        return thresholdHPPercent;
    }

    public float getMaxDistance()
    {
        return maxDistance;
    }

    public float getRenderScale()
    {
        return renderScale;
    }

    public float getRenderYOffset()
    {
        return renderYOffset;
    }

    public void setThresholdHPPercent(int thresholdHPPercent)
    {
        this.thresholdHPPercent = thresholdHPPercent;
    }

    public void setMaxDistance(float maxDistance)
    {
        this.maxDistance = maxDistance;
    }

    public void setRenderScale(float renderScale)
    {
        this.renderScale = renderScale;
    }

    public void setRenderYOffset(float renderYOffset)
    {
        this.renderYOffset = renderYOffset;
    }

    public void displayConfig()
    {
        String targetPlayerDisplayNames = targetPlayerNames.stream().map(targetPlayerName -> Utilities.getPlayerDisplayName(mc, targetPlayerName)).collect(Collectors.toSet()).toString();
        targetPlayerDisplayNames = targetPlayerDisplayNames.substring(1, targetPlayerDisplayNames.length() - 1);
        IChatComponent targetPlayerDisplayNamesComp = getGoldComp("{").appendSibling(new ChatComponentText(targetPlayerDisplayNames)).appendSibling(getGoldComp("}")).appendSibling(getGoldComp(" (" + targetPlayerNames.size() + ")"));

        chatLogger.addCenteredLogNoHeader(getWhiteComp(" HPTracker Config "), '-', new ChatStyle().setColor(EnumChatFormatting.GOLD).setStrikethrough(true));
        chatLogger.addCenteredLogNoHeader(getWhiteComp("Targets: ").appendSibling(targetPlayerDisplayNamesComp));
        chatLogger.addCenteredLogNoHeader(getWhiteComp("Threshold HP: ").appendSibling(getGoldComp(thresholdHPPercent + "%")));
        chatLogger.addCenteredLogNoHeader(getWhiteComp("Max distance: ").appendSibling(getGoldComp(maxDistance + "m")));
        chatLogger.addCenteredLogNoHeader(getWhiteComp("Render scale: ").appendSibling(getGoldComp(String.valueOf(renderScale))));
        chatLogger.addCenteredLogNoHeader(getWhiteComp("Render offset: ").appendSibling(getGoldComp(String.valueOf(renderYOffset))));
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
}


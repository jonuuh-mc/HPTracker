package net.jonuuh.hptracker.config;

import net.jonuuh.hptracker.util.ChatLogger;
import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

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
        this.maxDistance = 20;
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
        chatLogger.addLog("targets: " + Utilities.getDisplayNames(mc, targetPlayerNames), EnumChatFormatting.GRAY, false); // TODO: fix closing bracket color
        chatLogger.addLog("threshold HP%: " + thresholdHPPercent, EnumChatFormatting.GRAY, false);
        chatLogger.addLog("max distance: " + maxDistance, EnumChatFormatting.GRAY, false);
        chatLogger.addLog("render scale: " + renderScale, EnumChatFormatting.GRAY, false);
        chatLogger.addLog("render offset: " + renderYOffset, EnumChatFormatting.GRAY, false);
    }
}


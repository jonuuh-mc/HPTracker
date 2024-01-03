package net.jonuuh.hptracker.config;

import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

/**
 * Main config.
 */
public class Config
{
    private final Minecraft mc;
    private final TargetPlayerNameSet<String> targetPlayerNames;
    private int thresholdHPPercent;
    private float maxDistance;
    private float renderScale;
    private float renderYOffset;

    public Config(Minecraft mc)
    {
        this.mc = mc;
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
        Utilities.addChatMessage(mc, "[HPTracker]", EnumChatFormatting.GOLD);
        Utilities.addChatMessage(mc, "targets: " + Utilities.getDisplayNames(mc, targetPlayerNames), EnumChatFormatting.GRAY); // TODO: fix closing bracket color
        Utilities.addChatMessage(mc, "threshold HP%: " + thresholdHPPercent, EnumChatFormatting.GRAY);
        Utilities.addChatMessage(mc, "max distance: " + maxDistance, EnumChatFormatting.GRAY);
        Utilities.addChatMessage(mc, "render scale: " + renderScale, EnumChatFormatting.GRAY);
        Utilities.addChatMessage(mc, "render offset: " + renderYOffset, EnumChatFormatting.GRAY);
        Utilities.addChatMessage(mc, "[HPTracker]", EnumChatFormatting.GOLD);
    }
}


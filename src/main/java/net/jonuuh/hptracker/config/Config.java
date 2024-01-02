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
        this.thresholdHPPercent = 100;
        this.maxDistance = 15;
        this.renderScale = 1.0F;
        this.renderYOffset = -30.0F;
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
        Utilities.addChatMessage(mc, "targets: " + targetPlayerNames, EnumChatFormatting.DARK_GRAY);
        Utilities.addChatMessage(mc, "threshold HP%: " + thresholdHPPercent, EnumChatFormatting.DARK_GRAY);
        Utilities.addChatMessage(mc, "max distance: " + maxDistance, EnumChatFormatting.DARK_GRAY);
        Utilities.addChatMessage(mc, "render scale: " + renderScale, EnumChatFormatting.DARK_GRAY);
        Utilities.addChatMessage(mc, "render offset: " + renderYOffset, EnumChatFormatting.DARK_GRAY);
        Utilities.addChatMessage(mc, "[HPTracker]", EnumChatFormatting.GOLD);
    }
}


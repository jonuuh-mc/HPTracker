package net.jonuuh.hptracker.render;

import net.jonuuh.hptracker.config.Config;
import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class Renderer
{
    private final Minecraft mc;
    private final Config config;
    private final RendererUtils rendererUtils;

    private String message;
    private int time;
    private int color;
    private float scale;
    private float yOffset;

    Renderer(Minecraft mc, Config config)
    {
        this.mc = mc;
        this.config = config;
        this.rendererUtils = new RendererUtils();
    }

    /**
     * Client tick event listener. (called 40 times per second)
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (mc.theWorld != null)
        {
            RendererUtils.Player player = rendererUtils.getMinHealthValidTargetPlayer();
            if (player != null)
            {
                if ((player.getHealth() / player.getMaxHealth()) * 100 <= config.getThresholdHPPercent())
                {
                    startRender(player.getName() + " is on " + Utilities.roundToHalf(player.getHealth()) + " HP!", player.getColor());
                }
            }
        }
    }

    /**
     * Render overlay text.
     * - see line 224 {@link net.minecraft.client.gui.GuiIngame#renderGameOverlay(float)}
     *
     * @param event the event
     */
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Text event)
    {
        if (time > 0)
        {
            ScaledResolution sr = new ScaledResolution(mc);
            time--;

            float f2 = (float) time - event.partialTicks;
            int l1 = (int) (f2 * 255.0F / 20.0F); // normalizing?

            if (l1 > 255)
            {
                l1 = 255;
            }

            if (l1 > 8)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) (sr.getScaledWidth() / 2), (float) (sr.getScaledHeight() / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.scale(scale, scale, scale);
                getFontRenderer().drawStringWithShadow(message, getXOffset(), yOffset, getColor(l1));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

    private void startRender(String message, int color)
    {
        this.message = message;
        this.time = 60;
        this.color = color;
        this.scale = config.getRenderScale();
        this.yOffset = config.getRenderYOffset();
    }

    private float getXOffset()
    {
        return (float) (-getFontRenderer().getStringWidth(message) / 2);
    }

    private int getColor(int l1)
    {
        return color + (l1 << 24 & -color);
    }

    private FontRenderer getFontRenderer()
    {
        return mc.fontRendererObj;
    }

    private class RendererUtils
    {
        /**
         * Creates and gets a Player object, containing the name, max health, current health, and name color
         *
         * @return the player OR null if there no non-spectator target players within the max config distance
         */
        public Player getMinHealthValidTargetPlayer()
        {
            String minHealthTargetPlayerName = getMinHealthTargetPlayerName();
            if (minHealthTargetPlayerName != null)
            {
                float maxHealth = getPlayerMaxHealthByName(minHealthTargetPlayerName);
                float health = getPlayerHealthByName(minHealthTargetPlayerName);
                int color = getPlayerColorHexByName(minHealthTargetPlayerName);
                return new Player(minHealthTargetPlayerName, maxHealth, health, color);
            }
            return null;
        }

        /**
         * Gets name of the valid target player with least health OR null if no valid target players.
         */
        private String getMinHealthTargetPlayerName()
        {
            Set<String> validPlayers = config.getTargetPlayerNames().stream().filter(this::verifyPlayer).collect(Collectors.toSet());
            return (validPlayers.size() != 0) ? Collections.min(validPlayers, Comparator.comparing(this::getPlayerHealthByName)) : null;
        }

        /**
         * Verify by name whether an EntityPlayer exists in WorldClient, is not a spectator, and is within max config distance<br>
         * Note: WorldClient assumed != null
         */
        private boolean verifyPlayer(String name)
        {
            EntityPlayer player = mc.theWorld.getPlayerEntityByName(name);
            return player != null && !isPlayerSpectatorByName(name) && mc.thePlayer.getDistanceToEntity(player) <= config.getMaxDistance();
        }

        /**
         * Verify by name whether an EntityPlayer is a spectator.<br>
         * Note: NetHandlerPlayClient and player NetworkPlayerInfo assumed != null
         */
        private boolean isPlayerSpectatorByName(String name)
        {
            return mc.getNetHandler().getPlayerInfo(name).getGameType() != WorldSettings.GameType.SPECTATOR;
        }

        /**
         * Gets a EntityPlayer's max health by name.<br>
         * Note: WorldClient and EntityPlayer assumed != null
         */
        private float getPlayerMaxHealthByName(String name)
        {
            return mc.theWorld.getPlayerEntityByName(name).getMaxHealth();
        }

        /**
         * Gets a EntityPlayer's health by name.<br>
         * Note: WorldClient and EntityPlayer assumed != null
         */
        private float getPlayerHealthByName(String name)
        {
            return mc.theWorld.getPlayerEntityByName(name).getHealth();
        }

        /**
         * Gets an EntityPlayer's displayName color hex by name.<br>
         * Note: WorldClient and EntityPlayer assumed != null
         */
        private int getPlayerColorHexByName(String name)
        {
            EntityPlayer player = mc.theWorld.getPlayerEntityByName(name);
            String formattedName = player.getDisplayName().getFormattedText();
            String unformattedName = player.getName();
            char formattingColorCode = formattedName.charAt(formattedName.indexOf(unformattedName) - 1);
            return Utilities.formattingColorCodeToHex.get(formattingColorCode);
        }

        private class Player
        {
            private final String name;
            private final float maxHealth;
            private final float health;
            private final int color;

            private Player(String name, float maxHealth, float health, int color)
            {
                this.name = name;
                this.maxHealth = maxHealth;
                this.health = health;
                this.color = color;
            }

            private String getName()
            {
                return name;
            }

            private float getMaxHealth()
            {
                return maxHealth;
            }

            private float getHealth()
            {
                return health;
            }

            private int getColor()
            {
                return color;
            }
        }
    }
}


package net.jonuuh.hptracker.render;

import net.jonuuh.hptracker.config.Config;
import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
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
    private float angle;
    private ResourceLocation skin;

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
                    startRender((player.getHealth() / 2) + "\u2764", player.getColor(), player.getAngle(), player.getSkin());
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
            time--;
            ScaledResolution sr = new ScaledResolution(mc);

            GlStateManager.pushMatrix();
            GlStateManager.translate((float) (sr.getScaledWidth() / 2), (float) (sr.getScaledHeight() / 2), 0.0F);
            GlStateManager.translate(-getFontRenderer().getStringWidth(message) * scale / 2.0F, -getFontRenderer().FONT_HEIGHT * scale, 0.0F); // insanity
            GlStateManager.scale(scale, scale, scale);

            GlStateManager.rotate(angle, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0, yOffset * scale, 0.0F);
            GlStateManager.rotate(-angle, 0.0F, 0.0F, 1.0F);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            mc.getTextureManager().bindTexture(skin);
            Gui.drawScaledCustomSizeModalRect(getFontRenderer().getStringWidth(message) / 4, getFontRenderer().FONT_HEIGHT, 8, 8, 8, 8, 12, 12, 64.0F, 64.0F);
            getFontRenderer().drawStringWithShadow(message, 0, 0, color);

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();

        }
    }

    private void startRender(String message, int color, float angle, ResourceLocation skin)
    {
        this.time = 60;
        this.message = message;
        this.color = color;
        this.angle = angle;
        this.skin = skin;
        this.scale = config.getRenderScale();
        this.yOffset = config.getRenderYOffset();
    }

    private float getXOffset()
    {
        return (-getFontRenderer().getStringWidth(message) / 2.0F);
    }

    private float getYOffset()
    {
        return (-getFontRenderer().FONT_HEIGHT / 2.0F);
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
            String minHealthTargetPlayerName = getMinHealthValidTargetPlayerName();
            if (minHealthTargetPlayerName != null)
            {
                float maxHealth = getPlayerMaxHealthByName(minHealthTargetPlayerName);
                float health = getPlayerHealthByName(minHealthTargetPlayerName);
                int color = getPlayerColorHexByName(minHealthTargetPlayerName);
                float angle = getAngleToPlayerByName(minHealthTargetPlayerName);
                ResourceLocation skin = getPlayerSkinByName(minHealthTargetPlayerName);
                return new Player(minHealthTargetPlayerName, maxHealth, health, color, angle, skin);
            }
            return null;
        }

        /**
         * Gets name of the valid target player with least health OR null if no valid target players.
         */
        private String getMinHealthValidTargetPlayerName()
        {
            Set<String> validPlayers = config.getTargetPlayerNames().stream().filter(this::verifyPlayer).collect(Collectors.toSet());
            return (validPlayers.size() != 0) ? Collections.min(validPlayers, Comparator.comparing(this::getPlayerHealthByName)) : null;
        }

        /**
         * Verify by name whether an EntityPlayer exists in WorldClient, is in survival, and is within max config distance<br>
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
        private boolean isPlayerSpectatorByName(String name) // TODO: nullpointer on world change?
        {
            return mc.getNetHandler().getPlayerInfo(name).getGameType() == WorldSettings.GameType.SPECTATOR;
        }

        /**
         * Gets a NetworkPlayerInfo's skin by name.<br>
         * Note: NetHandlerPlayClient, player NetworkPlayerInfo, and ResourceLocation assumed != null
         */
        private ResourceLocation getPlayerSkinByName(String name)
        {
            return mc.getNetHandler().getPlayerInfo(name).getLocationSkin();
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
            return Utilities.formattingColorCodeToHex.getOrDefault(formattingColorCode, 0xFFFFFF);
        }

        /**
         * Gets the angle from the player's look vector to EntityPlayer by name.<br>
         * Note: WorldClient and EntityPlayer and EntityPlayerSP assumed != null
         */
        private float getAngleToPlayerByName(String name)
        {
            EntityPlayer player = mc.theWorld.getPlayerEntityByName(name);
            Vec3 playerDiffVec = new Vec3(player.posX, player.posY, player.posZ).subtract(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).normalize();
            Vec3 playerLookVec = mc.thePlayer.getLookVec();

            // TODO: FIX LOOK VECTOR 0 WHEN LOOKING STRAIGHT DOWN, BREAKS ANGLES
//            double lookX = playerLookVec.xCoord;
//            double lookY = playerLookVec.yCoord;
//            double lookZ = playerLookVec.zCoord;
//
////            // value must be between MIN_VALUE and MAX_VALUE
////            value = value > MAX_VALUE ? MAX_VALUE : value < MIN_VALUE ? MIN_VALUE : value;
//            lookX = lookX > 1.0 ? 1.0 : lookX < -1.0 ? -1.0 : lookX;
//            Vec3 playerLookVecClamped = new Vec3(lookX, lookY, lookZ);
//            Utilities.addChatMessage(mc, playerLookVec.toString());
//
//            if (playerLookVec.xCoord == 0.0)
//            {
//                playerLookVec = playerLookVec.addVector(0.1, 0, 0);
//            }
//            if (playerLookVec.zCoord == 0.0)
//            {
//                playerLookVec = playerLookVec.addVector(0, 0, 0.1);
//            }
//
//            Utilities.addChatMessage(mc, playerLookVec.toString() + "\n");

            double angle_A = Math.atan2(playerDiffVec.zCoord, playerDiffVec.xCoord);
            double angle_B = Math.atan2(playerLookVec.zCoord, playerLookVec.xCoord);
            double angle_from_B_to_A = angle_A - (angle_B);

            // TODO: TAKE PITCH INTO ACCOUNT
//            if (mc.thePlayer.rotationPitch < 0.0F)
//            {
//                Utilities.addChatMessage(mc, String.valueOf(mc.thePlayer.rotationPitch));
//                angle_from_B_to_A = angle_from_B_to_A + 360.0F;
//            }
            return (float) Math.toDegrees(angle_from_B_to_A);
        }

        private class Player
        {
            private final String name;
            private final float maxHealth;
            private final float health;
            private final int color;
            private final float angle;
            private final ResourceLocation skin;

            private Player(String name, float maxHealth, float health, int color, float angle, ResourceLocation skin)
            {
                this.name = name;
                this.maxHealth = maxHealth;
                this.health = health;
                this.color = color;
                this.angle = angle;
                this.skin = skin;
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

            private float getAngle()
            {
                return angle;
            }

            public ResourceLocation getSkin()
            {
                return skin;
            }
        }
    }
}


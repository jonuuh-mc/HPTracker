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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Renderer
{
    private final Minecraft mc;
    private final Config config;
    private final RendererUtils rendererUtils;

    Renderer(Minecraft mc, Config config)
    {
        this.mc = mc;
        this.config = config;
        this.rendererUtils = new RendererUtils();
    }

    /**
     * Render overlay.
     * - see line 224 {@link net.minecraft.client.gui.GuiIngame#renderGameOverlay(float)}
     *
     * @param event the event
     */
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Text event)
    {
        ScaledResolution sr = new ScaledResolution(mc);

        for (RendererUtils.Player validTargetPlayer : rendererUtils.getValidTargetPlayers())
        {
            String hpStr = Utilities.roundToHalf(validTargetPlayer.getHealth() / 2.0F) + "\u2764";
            float scale = config.getRenderScale();

            GlStateManager.pushMatrix();
            GlStateManager.translate((float) (sr.getScaledWidth() / 2), (float) (sr.getScaledHeight() / 2), 0.0F);
            GlStateManager.translate(-getFontRenderer().getStringWidth(hpStr) * scale / 2.0F, -getFontRenderer().FONT_HEIGHT * scale, 0.0F); // insanity
            GlStateManager.scale(scale, scale, scale);

            GlStateManager.rotate(validTargetPlayer.getAngle(), 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0, config.getRenderYOffset() * scale, 0.0F);
            GlStateManager.rotate(-validTargetPlayer.getAngle(), 0.0F, 0.0F, 1.0F);

            mc.getTextureManager().bindTexture(validTargetPlayer.getSkin());
            Gui.drawScaledCustomSizeModalRect(getFontRenderer().getStringWidth(hpStr) / 4, getFontRenderer().FONT_HEIGHT, 8, 8, 8, 8, 12, 12, 64.0F, 64.0F);
            getFontRenderer().drawStringWithShadow(hpStr, 0, 0, validTargetPlayer.getColor());
            GlStateManager.popMatrix();
        }
    }

    private FontRenderer getFontRenderer()
    {
        return mc.fontRendererObj;
    }

    private class RendererUtils
    {
        public Set<Player> getValidTargetPlayers()
        {
            Set<String> validTargetPlayerNames = config.getTargetPlayerNames().stream().filter(this::verifyPlayer).collect(Collectors.toSet());
            Set<Player> validTargetPlayers = new HashSet<>();

            for (String validTargetPlayerName : validTargetPlayerNames)
            {
                float health = getPlayerByName(validTargetPlayerName).getHealth();
                int color = getPlayerColorHexByName(validTargetPlayerName);
                float angle = getAngleToPlayerByName(validTargetPlayerName);
                ResourceLocation skin = getPlayerSkinByName(validTargetPlayerName);
                validTargetPlayers.add(new Player(validTargetPlayerName, health, color, angle, skin));
            }
            return validTargetPlayers;
        }

        /**
         * Verify by name whether an EntityPlayer exists in WorldClient, exists in the playerInfoMap,
         * is not a spectator, is within max distance, and is within hp threshold.<br>
         * Note: WorldClient assumed != null
         */
        private boolean verifyPlayer(String name)
        {
            if (getPlayerByName(name) == null || !Utilities.getOnlinePlayerNames(mc).contains(name))
            {
                return false;
            }
            return !isPlayerSpectatorByName(name) && isPlayerWithinMaxDistByName(name) && isPlayerWithinHPThresholdByName(name);
        }

        /**
         * Verify by name whether an EntityPlayer is a spectator.<br>
         * Note: NetHandlerPlayClient and player NetworkPlayerInfo assumed != null
         */
        private boolean isPlayerSpectatorByName(String name)
        {
            return mc.getNetHandler().getPlayerInfo(name).getGameType() == WorldSettings.GameType.SPECTATOR;
        }

        /**
         * Verify by name whether an EntityPlayer is within max configured distance.<br>
         * Note: WorldClient and EntityPlayer assumed != null
         */
        private boolean isPlayerWithinMaxDistByName(String name)
        {
            return mc.thePlayer.getDistanceToEntity(getPlayerByName(name)) <= config.getMaxDistance();
        }

        /**
         * Verify by name whether an EntityPlayer is within configured hp threshold.<br>
         * Note: WorldClient and EntityPlayer assumed != null
         */
        private boolean isPlayerWithinHPThresholdByName(String name)
        {
            double hpPercent = (getPlayerByName(name).getHealth() / getPlayerByName(name).getMaxHealth()) * 100;
            return hpPercent <= config.getThresholdHPPercent();
        }

        /**
         * Gets an EntityPlayer's displayName color hex by name.<br>
         * Note: WorldClient and EntityPlayer assumed != null
         */
        private int getPlayerColorHexByName(String name)
        {
            EntityPlayer player = getPlayerByName(name);
            String formattedName = player.getDisplayName().getFormattedText();
            char tryFormattingColorCode = formattedName.charAt(formattedName.indexOf(player.getName()) - 1);
            return Utilities.formattingColorCodeToHex.getOrDefault(tryFormattingColorCode, 0xFFFFFF);
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
         * Gets the angle from the player's look vector to EntityPlayer by name.<br>
         * Note: WorldClient and EntityPlayer and EntityPlayerSP assumed != null
         */
        private float getAngleToPlayerByName(String name)
        {
            EntityPlayer player = getPlayerByName(name);
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

        private EntityPlayer getPlayerByName(String name)
        {
            return mc.theWorld.getPlayerEntityByName(name);
        }

        private class Player
        {
            private final String name;
            private final float health;
            private final int color;
            private final float angle;
            private final ResourceLocation skin;

            private Player(String name, float health, int color, float angle, ResourceLocation skin)
            {
                this.name = name;
                this.health = health;
                this.color = color;
                this.angle = angle;
                this.skin = skin;
            }

            private String getName()
            {
                return name;
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


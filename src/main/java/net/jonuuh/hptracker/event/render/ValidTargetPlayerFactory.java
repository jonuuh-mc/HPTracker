package net.jonuuh.hptracker.event.render;

import net.jonuuh.hptracker.config.Config;
import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings;

import java.util.Set;
import java.util.stream.Collectors;

class ValidTargetPlayerFactory
{
    static Set<ValidTargetPlayer> getValidTargetPlayers(Minecraft mc, Config config)
    {
        return config.getTargetPlayerNames().stream().filter(targetPlayerName -> verifyEntityPlayer(mc, config, targetPlayerName))
                .map(validTargetPlayerName -> createPlayer(mc, validTargetPlayerName)).collect(Collectors.toSet());
    }

    private static ValidTargetPlayer createPlayer(Minecraft mc, String name)
    {
        ResourceLocation skin = getEntityPlayerSkin(mc, name);
        int color = getEntityPlayerColorHex(mc, name);
        float health = getEntityPlayer(mc, name).getHealth();
        float angle = getAngleToEntityPlayer(mc, name);
        return new ValidTargetPlayer(name, skin, color, health, angle);
    }


    private static EntityPlayer getEntityPlayer(Minecraft mc, String name)
    {
        return mc.theWorld.getPlayerEntityByName(name);
    }

    /**
     * Verify by name whether an EntityPlayer exists in WorldClient, exists in the playerInfoMap,
     * is not a spectator, is within max distance, and is within hp threshold.<br>
     * Note: WorldClient, EntityPlayer, NetHandlerPlayClient, NetworkPlayerInfo assumed != null
     */
    private static boolean verifyEntityPlayer(Minecraft mc, Config config, String name)
    {
        // EntityPlayer does not exist in WorldClient (render dist) or is not online (tab)
        if (getEntityPlayer(mc, name) == null || !Utilities.getOnlinePlayerNames(mc).contains(name))
        {
            return false;
        }
        // EntityPlayer is a spectator
        if (mc.getNetHandler().getPlayerInfo(name).getGameType() == WorldSettings.GameType.SPECTATOR)
        {
            return false;
        }
        // EntityPlayer is not within hp threshold
        if ((getEntityPlayer(mc, name).getHealth() / getEntityPlayer(mc, name).getMaxHealth()) * 100 > config.getThresholdHPPercent())
        {
            return false;
        }
        // EntityPlayer is not within max distance
        if (mc.thePlayer.getDistanceToEntity(getEntityPlayer(mc, name)) > config.getMaxDistance())
        {
            return false;
        }
        return true;
    }

    /**
     * Gets a NetworkPlayerInfo's skin by name.<br>
     * Note: NetHandlerPlayClient, player NetworkPlayerInfo, and ResourceLocation assumed != null
     */
    private static ResourceLocation getEntityPlayerSkin(Minecraft mc, String name)
    {
        return mc.getNetHandler().getPlayerInfo(name).getLocationSkin();
    }

    /**
     * Gets an EntityPlayer's displayName color hex by name.<br>
     * Note: WorldClient and EntityPlayer assumed != null
     */
    private static int getEntityPlayerColorHex(Minecraft mc, String name)
    {
        EntityPlayer player = getEntityPlayer(mc, name);
        String formattedName = player.getDisplayName().getFormattedText();
        char tryFormattingColorCode = formattedName.charAt(formattedName.indexOf(player.getName()) - 1);
        return Utilities.formattingColorCodeToHex.getOrDefault(tryFormattingColorCode, 0xFFFFFF);
    }

    /**
     * Gets the angle from the player's look vector to EntityPlayer by name.<br>
     * Note: WorldClient and EntityPlayer and EntityPlayerSP assumed != null
     */
    private static float getAngleToEntityPlayer(Minecraft mc, String name)
    {
        EntityPlayer player = getEntityPlayer(mc, name);
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
}

package net.jonuuh.hptracker.util;

import net.jonuuh.hptracker.config.TargetPlayerNameSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Misc. Utilities.
 */
public class Utilities
{
    /**
     * Creates and gets a Player object, containing the name, current health, and name color
     *
     * @param mc                  the minecraft object
     * @param targetPlayerNameSet the target player names
     * @param maxDist             the max valid distance away from the client player
     * @return the player OR null if there no non-spectator target players within the max config distance
     */
    public static Player getTargetPlayer(Minecraft mc, TargetPlayerNameSet<String> targetPlayerNameSet, float maxDist)
    {
        String minHealthTargetPlayerName = getMinHealthTargetPlayerName(mc, targetPlayerNameSet, maxDist);
        if (minHealthTargetPlayerName != null)
        {
            float maxHealth = getPlayerMaxHealthByName(mc, minHealthTargetPlayerName);
            float health = getPlayerHealthByName(mc, minHealthTargetPlayerName);
            int color = getPlayerColorHexByName(mc, minHealthTargetPlayerName);
            return new Player(minHealthTargetPlayerName, maxHealth, health, color);
        }
        return null;
    }

    /**
     * Gets name of target player with least health.
     *
     * @param mc                  the minecraft object
     * @param targetPlayerNameSet the target player names
     * @param maxDist             the max valid distance away from the client player
     * @return the min health target player OR null if no valid target players exist
     */
    private static String getMinHealthTargetPlayerName(Minecraft mc, TargetPlayerNameSet<String> targetPlayerNameSet, float maxDist)
    {
        Set<String> validPlayers = targetPlayerNameSet.stream().filter(player -> verifyPlayer(mc, player, maxDist)).collect(Collectors.toSet());
        return (validPlayers.size() != 0) ? Collections.min(validPlayers, Comparator.comparing(player -> getPlayerHealthByName(mc, player))) : null;
    }

    /**
     * Verify a player by name.<br>
     * Note: WorldClient assumed != null
     *
     * @param mc      the minecraft object
     * @param name    the name
     * @param maxDist the max valid distance away from the client player
     * @return whether an EntityPlayer with the name exists in WorldClient, is not a spectator, and is within the max config distance
     */
    private static boolean verifyPlayer(Minecraft mc, String name, float maxDist)
    {
        EntityPlayer player = mc.theWorld.getPlayerEntityByName(name);
        return player != null && !isPlayerSpectatorByName(mc, name) && mc.thePlayer.getDistanceToEntity(player) <= maxDist;
    }

    /**
     * Is the player a spectator.<br>
     * Note: NetHandlerPlayClient and player NetworkPlayerInfo assumed != null
     *
     * @param mc   the minecraft object
     * @param name the name
     * @return whether the player with the name is not a spectator.
     */
    private static boolean isPlayerSpectatorByName(Minecraft mc, String name)
    {
        return mc.getNetHandler().getPlayerInfo(name).getGameType() != WorldSettings.GameType.SPECTATOR;
    }

    /**
     * Gets a EntityPlayer's max health by name.<br>
     * Note: WorldClient and EntityPlayer assumed != null
     *
     * @param mc   the minecraft object
     * @param name the name
     * @return the EntityPlayer's health
     */
    private static float getPlayerMaxHealthByName(Minecraft mc, String name)
    {
        return mc.theWorld.getPlayerEntityByName(name).getMaxHealth();
    }

    /**
     * Gets a EntityPlayer's health by name.<br>
     * Note: WorldClient and EntityPlayer assumed != null
     *
     * @param mc   the minecraft object
     * @param name the name
     * @return the EntityPlayer's health
     */
    private static float getPlayerHealthByName(Minecraft mc, String name)
    {
        return mc.theWorld.getPlayerEntityByName(name).getHealth();
    }

    /**
     * Gets an EntityPlayer's displayName color hex by name.<br>
     * Note: WorldClient and EntityPlayer assumed != null
     *
     * @param mc   the minecraft object
     * @param name the name
     * @return the EntityPlayer's displayName color hex
     */
    private static int getPlayerColorHexByName(Minecraft mc, String name)
    {
        EntityPlayer player = mc.theWorld.getPlayerEntityByName(name);
        String formattedName = player.getDisplayName().getFormattedText();
        String unformattedName = player.getName();
        char formattingColorCode = formattedName.charAt(formattedName.indexOf(unformattedName) - 1);
        return formattingColorCodeToHex(formattingColorCode);
    }

    /**
     * Convert a minecraft formatting color code to a color hex value.
     *
     * @param code the minecraft formatting color code
     * @return a color hex value
     */
    private static int formattingColorCodeToHex(char code)
    {
        switch (code)
        {
            case '0':
                return 0x000000;
            case '1':
                return 0x0000AA;
            case '2':
                return 0x00AA00;
            case '3':
                return 0x00AAAA;
            case '4':
                return 0xAA0000;
            case '5':
                return 0xAA00AA;
            case '6':
                return 0xFFAA00;
            case '7':
                return 0xAAAAAA;
            case '8':
                return 0x555555;
            case '9':
                return 0x5555FF;
            case 'a':
                return 0x55FF55;
            case 'b':
                return 0x55FFFF;
            case 'c':
                return 0xFF5555;
            case 'd':
                return 0xFF55FF;
            case 'e':
                return 0xFFFF55;
            case 'f':
                return 0xFFFFFF;
            default:
                return 0xFFFFFF; //
        }
    }

    /**
     * Gets the names of all online players (in the world).
     *
     * @param mc the minecraft object
     * @return the online player names
     */
    public static List<String> getOnlinePlayerNames(Minecraft mc)
    {
        List<String> onlinePlayers = new ArrayList<>();
        if (mc.thePlayer != null)
        {
            for (NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap())
            {
                onlinePlayers.add(networkPlayerInfo.getGameProfile().getName());
            }
        }
        return onlinePlayers;
    }

    /**
     * Add a client side chat message.
     *
     * @param mc      the minecraft object
     * @param message the message
     */
    public static void addChatMessage(Minecraft mc, String message)
    {
        addChatMessage(mc, message, EnumChatFormatting.WHITE);
    }

    /**
     * Add a client side colored chat message.
     *
     * @param mc      the minecraft object
     * @param message the message
     * @param color   the color
     */
    public static void addChatMessage(Minecraft mc, String message, EnumChatFormatting color)
    {
        mc.thePlayer.addChatMessage(new ChatComponentText(color + message));
    }

    /**
     * Round a number to the nearest 0.5.
     *
     * @param num the number
     * @return the rounded number
     */
    public static float roundToHalf(double num)
    {
        return (float) (Math.round(num * 2) / 2.0);
    }
}

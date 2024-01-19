package net.jonuuh.hptracker.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Misc. Utilities.
 */
public class Utilities
{
    public static final Map<Character, Integer> formattingColorCodeToHex = initMap();

    private static Map<Character, Integer> initMap()
    {
        Map<Character, Integer> map = new HashMap<>();
        map.put('0', 0x000000);
        map.put('1', 0x0000AA);
        map.put('2', 0x00AA00);
        map.put('3', 0x00AAAA);
        map.put('4', 0xAA0000);
        map.put('5', 0xAA00AA);
        map.put('6', 0xFFAA00);
        map.put('7', 0xAAAAAA);
        map.put('8', 0x555555);
        map.put('9', 0x5555FF);
        map.put('a', 0x55FF55);
        map.put('b', 0x55FFFF);
        map.put('c', 0xFF5555);
        map.put('d', 0xFF55FF);
        map.put('e', 0xFFFF55);
        map.put('f', 0xFFFFFF);
        return map;
    }

    public static Set<String> getPlayerSPTeammates(Minecraft mc)
    {
        Set<String> teammateNames = new HashSet<>();

        if (mc.getNetHandler() != null && mc.getNetHandler().getPlayerInfoMap() != null)
        {
            Set<NetworkPlayerInfo> teammateInfo = mc.getNetHandler().getPlayerInfoMap().stream().filter(networkPlayerInfo -> verifyTeammate(mc, networkPlayerInfo)).collect(Collectors.toSet());
            teammateNames = teammateInfo.stream().map(networkPlayerInfo -> networkPlayerInfo.getGameProfile().getName()).collect(Collectors.toSet());
        }
        return teammateNames;
    }

    private static boolean verifyTeammate(Minecraft mc, NetworkPlayerInfo networkPlayerInfo)
    {
        ScorePlayerTeam playerSPTeam = mc.theWorld.getScoreboard().getPlayersTeam(mc.thePlayer.getName()); // TODO: null if nicked

        // if the 'teammate' is the client player themself
        if (mc.thePlayer.getName().equals(networkPlayerInfo.getGameProfile().getName()))
        {
            return false;
        }

        if (networkPlayerInfo.getPlayerTeam().getTeamName().equals(playerSPTeam.getTeamName()))
        {
            return true;
        }
        if (networkPlayerInfo.getPlayerTeam().getColorPrefix().equals(playerSPTeam.getColorPrefix()))
        {
            return true;
        }
        return false;
    }

    public static String getPlayerDisplayName(Minecraft mc, String name)
    {
        if (mc.getNetHandler() != null && mc.getNetHandler().getPlayerInfoMap() != null)
        {
            NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfoMap().stream().filter(networkPlayerInfo -> networkPlayerInfo.getGameProfile().getName().equals(name)).findFirst().orElse(null);
            if (playerInfo != null)
            {
                if (playerInfo.getDisplayName() != null)
                {
                    return playerInfo.getDisplayName().getFormattedText() + EnumChatFormatting.RESET;
                }
                return ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName() + EnumChatFormatting.RESET);
            }
        }
        return name;
    }

//    public static Set<String> getOnlinePlayerDisplayNames(Minecraft mc)
//    {
//        Set<String> displayNames = new HashSet<>();
//        if (mc.getNetHandler() != null && mc.getNetHandler().getPlayerInfoMap() != null)
//        {
//            displayNames = mc.getNetHandler().getPlayerInfoMap().stream().map(Utilities::getPlayerDisplayName).collect(Collectors.toSet());
//        }
//        return displayNames;
//    }
//
//    private static String getPlayerDisplayName(NetworkPlayerInfo networkPlayerInfo)
//    {
//        if (networkPlayerInfo.getDisplayName() != null)
//        {
//            return networkPlayerInfo.getDisplayName().getFormattedText() + EnumChatFormatting.RESET;
//        }
//        return ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), networkPlayerInfo.getGameProfile().getName() + EnumChatFormatting.RESET);
//    }

    /**
     * Gets the names of all online players (in the world).
     *
     * @param mc the minecraft object
     * @return the online player names
     */
    public static Set<String> getOnlinePlayerNames(Minecraft mc)
    {
        Set<String> onlinePlayers = new HashSet<>();
        if (mc.getNetHandler() != null && mc.getNetHandler().getPlayerInfoMap() != null)
        {
            onlinePlayers = mc.getNetHandler().getPlayerInfoMap().stream()
                    .map(networkPlayerInfo -> networkPlayerInfo.getGameProfile().getName())
                    .collect(Collectors.toSet());
        }
        return onlinePlayers;
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

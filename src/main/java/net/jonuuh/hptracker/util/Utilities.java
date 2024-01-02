package net.jonuuh.hptracker.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

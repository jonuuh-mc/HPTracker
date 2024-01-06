package net.jonuuh.hptracker.event;

import net.jonuuh.hptracker.config.Config;
import net.jonuuh.hptracker.event.render.Renderer;
import net.jonuuh.hptracker.util.ChatLogger;
import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class MainController
{
    private final Minecraft mc;
    private final ChatLogger chatLogger;
    private final KeyBinding debugKey;
    private final Renderer renderer;

    private boolean doRendering = false;

    public MainController(Minecraft mc, ChatLogger chatLogger, Config config, KeyBinding debugKey)
    {
        this.mc = mc;
        this.chatLogger = chatLogger;
        this.debugKey = debugKey;
        renderer = new Renderer(mc, config);
    }

    /**
     * Key input event handler.
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event)
    {
        if (debugKey.isPressed())
        {
            chatLogger.addLog(Utilities.getOnlinePlayerDisplayNames(mc).toString());

            if (!doRendering)
            {
                chatLogger.addLog("registered renderer");
                MinecraftForge.EVENT_BUS.register(renderer);
            }
            else
            {
                chatLogger.addLog("unregistered renderer");
                MinecraftForge.EVENT_BUS.unregister(renderer);
            }
            doRendering = !doRendering;
        }
    }

//    @SubscribeEvent
//    public void onWorldLoad(WorldEvent.Load event)
//    {
//        System.out.println("world loaded");
//        System.out.println(mc.thePlayer);
//        System.out.println(mc.getNetHandler());
//    }

//    /**
//     * Client chat received event handler.
//     *
//     * @param event the event
//     */
//    @SubscribeEvent
//    public void onClientChatReceived(ClientChatReceivedEvent event)
//    {
//    }
}

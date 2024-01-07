package net.jonuuh.hptracker.event;

import net.jonuuh.hptracker.config.Config;
import net.jonuuh.hptracker.event.render.Renderer;
import net.jonuuh.hptracker.util.ChatLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class MainController
{
    private final Minecraft mc;
    private final ChatLogger chatLogger;
    private final KeyBinding toggleKey;
    private final KeyBinding debugKey;
    private final Renderer renderer;

    private boolean doRendering = true;
    private boolean doMining = false;

    public MainController(Minecraft mc, ChatLogger chatLogger, Config config, KeyBinding toggleKey, KeyBinding debugKey)
    {
        this.mc = mc;
        this.chatLogger = chatLogger;
        this.toggleKey = toggleKey;
        this.debugKey = debugKey;
        renderer = new Renderer(mc, config);
        MinecraftForge.EVENT_BUS.register(renderer);
    }

    /**
     * Key input event handler.
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event)
    {
        if (toggleKey.isPressed())
        {
            if (doRendering)
            {
                chatLogger.addFailureLog("Unregistered renderer.");
                MinecraftForge.EVENT_BUS.unregister(renderer);
            }
            else
            {
                chatLogger.addSuccessLog("Registered renderer.");
                MinecraftForge.EVENT_BUS.register(renderer);
            }
            doRendering = !doRendering;
        }

        if (debugKey.isPressed())
        {
            if (doMining)
            {
                chatLogger.addFailureLog("Stopped miner.");
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
            }
            else
            {
                chatLogger.addSuccessLog("Started miner.");
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
            }
            doMining = !doMining;
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

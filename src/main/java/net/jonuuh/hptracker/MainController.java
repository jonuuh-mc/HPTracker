package net.jonuuh.hptracker;

import net.jonuuh.hptracker.config.Config;
import net.jonuuh.hptracker.render.RendererController;
import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class MainController
{
    private final Minecraft mc;
    private final KeyBinding debugKey;
    private final RendererController rendererController;

    private boolean doRendering = false;

    public MainController(Minecraft mc, Config config, KeyBinding debugKey)
    {
        this.mc = mc;
        this.debugKey = debugKey;
        this.rendererController = new RendererController(mc, config);
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
            System.out.println("debugKey pressed");
            if (!doRendering)
            {
                Utilities.addChatMessage(mc, "bound controller");
                rendererController.bind();
            }
            else
            {
                Utilities.addChatMessage(mc, "unbound controller");
                rendererController.unbind();
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

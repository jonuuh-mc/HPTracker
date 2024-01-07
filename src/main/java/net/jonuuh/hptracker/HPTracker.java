package net.jonuuh.hptracker;

import net.jonuuh.hptracker.command.Command;
import net.jonuuh.hptracker.config.Config;
import net.jonuuh.hptracker.event.MainController;
import net.jonuuh.hptracker.util.ChatLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = HPTracker.MODID, version = HPTracker.VERSION)
public class HPTracker
{
    public static final String MODID = "hptracker";
    public static final String VERSION = "1.2.1";

    private final Minecraft mc;
    private final ChatLogger chatLogger;
    private final Config config;
    private final KeyBinding toggleKey;
    private final KeyBinding debugKey;

    public HPTracker()
    {
        this.mc = Minecraft.getMinecraft();
        this.chatLogger = new ChatLogger(mc, "HPTracker", EnumChatFormatting.WHITE, EnumChatFormatting.GOLD, false);
        this.config = new Config(mc, chatLogger);
        this.toggleKey = new KeyBinding("Toggle", Keyboard.KEY_BACKSLASH, "HPTracker");
        this.debugKey = new KeyBinding("Test", Keyboard.KEY_L, "HPTracker");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ClientRegistry.registerKeyBinding(toggleKey);
        ClientRegistry.registerKeyBinding(debugKey);
        ClientCommandHandler.instance.registerCommand(new Command(mc, chatLogger, config));
        MinecraftForge.EVENT_BUS.register(new MainController(mc, chatLogger, config, toggleKey, debugKey));
    }
}

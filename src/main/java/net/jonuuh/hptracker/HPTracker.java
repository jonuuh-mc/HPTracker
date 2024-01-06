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
    public static final String VERSION = "1.1.0";

    private final Minecraft mc;
    private final ChatLogger chatLogger;
    private final Config config;
    private final KeyBinding debugKey;

    public HPTracker()
    {
        this.mc = Minecraft.getMinecraft();
        this.chatLogger = new ChatLogger(mc, "HPTracker", EnumChatFormatting.DARK_GRAY, EnumChatFormatting.GOLD, true);
        this.config = new Config(mc, chatLogger);
        this.debugKey = new KeyBinding("Test", Keyboard.KEY_BACKSLASH, "HPTracker");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ClientRegistry.registerKeyBinding(debugKey);
        ClientCommandHandler.instance.registerCommand(new Command(mc, chatLogger, config));
        MinecraftForge.EVENT_BUS.register(new MainController(mc, chatLogger, config, debugKey));
    }
}

package net.jonuuh.hptracker.render;

import net.jonuuh.hptracker.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class RendererController
{
    private final Renderer renderer;

    public RendererController(Minecraft mc, Config config)
    {
        renderer = new Renderer(mc, config);
    }

    public void bind()
    {
        MinecraftForge.EVENT_BUS.register(renderer);
    }

    public void unbind()
    {
        MinecraftForge.EVENT_BUS.unregister(renderer);
    }
}
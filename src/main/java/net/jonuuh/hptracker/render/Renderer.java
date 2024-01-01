package net.jonuuh.hptracker.render;

import net.jonuuh.hptracker.config.Config;
import net.jonuuh.hptracker.util.Player;
import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Renderer
{
    private final Minecraft mc;
    private final Config config;

    private String message;
    private int time;
    private int color;
    private float scale;
    private float yOffset;

    Renderer(Minecraft mc, Config config)
    {
        this.mc = mc;
        this.config = config;
    }

    /**
     * Client tick event listener. (called 40 times per second)
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (mc.theWorld != null)
        {
            Player player = Utilities.getTargetPlayer(mc, config.getTargetPlayerNames(), config.getMaxDistance());
            if (player != null)
            {
                if ((player.getHealth() / player.getMaxHealth()) * 100 <= config.getThresholdHPPercent())
                {
                    startRender(player.getName() + " is on " + Utilities.roundToHalf(player.getHealth()) + " HP!", player.getColor());
                }
            }
        }
    }

    /**
     * Render overlay text.
     * - see line 224 {@link net.minecraft.client.gui.GuiIngame#renderGameOverlay(float)}
     *
     * @param event the event
     */
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Text event)
    {
        if (time > 0)
        {
            ScaledResolution sr = new ScaledResolution(mc);
            time--;

            float f2 = (float) time - event.partialTicks;
            int l1 = (int) (f2 * 255.0F / 20.0F); // normalizing?

            if (l1 > 255)
            {
                l1 = 255;
            }

            if (l1 > 8)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) (sr.getScaledWidth() / 2), (float) (sr.getScaledHeight() / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.scale(scale, scale, scale);
                getFontRenderer().drawStringWithShadow(message, getXOffset(), yOffset, getColor(l1));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

    private void startRender(String message, int color)
    {
        this.message = message;
        this.time = 60;
        this.color = color;
        this.scale = config.getRenderScale();
        this.yOffset = config.getRenderYOffset();
    }

    private float getXOffset()
    {
        return (float) (-getFontRenderer().getStringWidth(message) / 2);
    }

    private int getColor(int l1)
    {
        return color + (l1 << 24 & -color);
    }

    private FontRenderer getFontRenderer()
    {
        return mc.fontRendererObj;
    }
}


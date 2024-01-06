package net.jonuuh.hptracker.event.render;

import net.jonuuh.hptracker.config.Config;
import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Renderer
{
    private final Minecraft mc;
    private final Config config;

    public Renderer(Minecraft mc, Config config)
    {
        this.mc = mc;
        this.config = config;
    }

    /**
     * Render overlay.
     * - see line 224 {@link net.minecraft.client.gui.GuiIngame#renderGameOverlay(float)}
     *
     * @param event the event
     */
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Text event)
    {
        ScaledResolution sr = new ScaledResolution(mc);

        for (ValidTargetPlayer validTargetPlayer : ValidTargetPlayerFactory.getValidTargetPlayers(mc, config))
        {
            String hpStr = Utilities.roundToHalf(validTargetPlayer.getHealth() / 2.0F) + "\u2764";
            float scale = config.getRenderScale();

            GlStateManager.pushMatrix();
            GlStateManager.translate((float) (sr.getScaledWidth() / 2), (float) (sr.getScaledHeight() / 2), 0.0F);
            GlStateManager.translate(-getFontRenderer().getStringWidth(hpStr) * scale / 2.0F, -getFontRenderer().FONT_HEIGHT * scale, 0.0F); // insanity
            GlStateManager.scale(scale, scale, scale);

            GlStateManager.rotate(validTargetPlayer.getAngle(), 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0, config.getRenderYOffset() * scale, 0.0F);
            GlStateManager.rotate(-validTargetPlayer.getAngle(), 0.0F, 0.0F, 1.0F);

            mc.getTextureManager().bindTexture(validTargetPlayer.getSkin());
            Gui.drawScaledCustomSizeModalRect(getFontRenderer().getStringWidth(hpStr) / 4, getFontRenderer().FONT_HEIGHT, 8, 8, 8, 8, 12, 12, 64.0F, 64.0F);
            getFontRenderer().drawStringWithShadow(hpStr, 0, 0, validTargetPlayer.getColor());
            GlStateManager.popMatrix();
        }
    }

    private FontRenderer getFontRenderer()
    {
        return mc.fontRendererObj;
    }
}


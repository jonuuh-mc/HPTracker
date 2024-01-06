package net.jonuuh.hptracker.event.render;

import net.minecraft.util.ResourceLocation;

class ValidTargetPlayer
{
    private final String name;
    private final ResourceLocation skin;
    private final int color;
    private final float health;
    private final float angle;

    ValidTargetPlayer(String name, ResourceLocation skin, int color, float health, float angle)
    {
        this.name = name;
        this.skin = skin;
        this.color = color;
        this.health = health;
        this.angle = angle;
    }

    String getName()
    {
        return name;
    }

    ResourceLocation getSkin()
    {
        return skin;
    }

    int getColor()
    {
        return color;
    }

    float getHealth()
    {
        return health;
    }

    float getAngle()
    {
        return angle;
    }
}

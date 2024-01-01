package net.jonuuh.hptracker.util;

public class Player
{
    private final String name;
    private final float maxHealth;
    private final float health;
    private final int color;

    Player(String name, float maxHealth, float health, int color)
    {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = health;
        this.color = color;
    }

    public String getName()
    {
        return name;
    }

    public float getMaxHealth()
    {
        return maxHealth;
    }

    public float getHealth()
    {
        return health;
    }

    public int getColor()
    {
        return color;
    }
}
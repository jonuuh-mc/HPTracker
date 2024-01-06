package net.jonuuh.hptracker.config;

import net.jonuuh.hptracker.util.Utilities;
import net.minecraft.client.Minecraft;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TargetPlayerNameSet<T extends String> extends HashSet<String>
{
    private final Minecraft mc;

    TargetPlayerNameSet(Minecraft mc)
    {
        super();
        this.mc = mc;
    }

    @Override
    public boolean add(String s)
    {
        return Utilities.getOnlinePlayerNames(mc).contains(s) && super.add(s);
    }

    @Override
    public boolean addAll(Collection<? extends String> c)
    {
        Set<String> cFiltered = new HashSet<>();
        for (String str: c)
        {
            if (Utilities.getOnlinePlayerNames(mc).contains(str))
            {
                cFiltered.add(str);
            }
        }
        return super.addAll(cFiltered);
//        return super.addAll(c.stream().filter(str -> Utilities.getOnlinePlayerNames(mc).contains(str)).collect(Collectors.toSet()));
    }
}

package net.jonuuh.hptracker.command;

enum Commands
{
    config,
    list,
    add,
    remove,
    addall,
    removeall,
    sethp,
    setscale,
    setoffset;

    public static String[] getNames()
    {
        return new String[]{config.name(), list.name(), add.name(), remove.name(), addall.name(), removeall.name(),
                sethp.name(), setscale.name(), setoffset.name()};
    }
}

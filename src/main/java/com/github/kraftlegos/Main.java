package com.github.kraftlegos;

import com.github.kraftlegos.commands.Join;
import com.github.kraftlegos.constructors.Game;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class Main extends JavaPlugin {

    private static Main instance;
    private Set<Game> games = new HashSet<>();

    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveDefaultConfig();

        getCommand("join").setExecutor(new Join());
        getLogger();
    }

    @Override
    public void onDisable() {

        instance = null;

    }

    public static Main getInstance() { return  instance; }

    public static Main get() {
        return instance;
    }


}

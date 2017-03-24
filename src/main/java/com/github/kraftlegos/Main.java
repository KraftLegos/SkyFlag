package com.github.kraftlegos;

import com.github.kraftlegos.commands.Join;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("join").setExecutor(new Join());
        getLogger();
    }

    public static Main get() {
        return instance;
    }


}

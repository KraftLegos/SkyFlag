package com.github.kraftlegos;

import com.github.kraftlegos.commands.Join;
import com.github.kraftlegos.listeners.onJoin;
import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public final class Main extends JavaPlugin {

    private static Main instance;
    private Set<Game> games = new HashSet<>();
    private int gamesList = 0;

    @Override
    public void onEnable() {
        instance = this;

        //getConfig().options().copyDefaults(true);
        //getConfig().options().copyHeader(true);
        //saveDefaultConfig();

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents( new onJoin(), this);

        getCommand("join").setExecutor(new Join());
        getLogger();

        Game game = new Game("one");
        GameManager.addGame(game);
        this.registerGame(game);
        game.setState(Game.GameState.LOBBY);
    }



    @Override
    public void onDisable() {

        instance = null;

    }

    public static Main getInstance() { return  instance; }

    public static Main get() {
        return instance;
    }

    public boolean registerGame(Game game) {
        if (games.size() == gamesList && gamesList != 1) {
            return false;
        }
        games.add(game);

        return true;
    }


}

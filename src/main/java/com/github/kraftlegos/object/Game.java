package com.github.kraftlegos.object;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import javax.activation.DataHandler;
import java.util.Set;

public class Game {

    private int maxPlayers;
    private int minPlayers;
    private World world;
    private Set<Location> spawnPoints;
    private boolean isTeamGame;

    //Active Game Objects
    private Set<GamePlayer> players;
    private Set<GameTeam> team;

    public Game(String gamename) {
        //FileConfiguration fileConfiguration = DataHandler.getInstance().getGameInfo();
        this.maxPlayers = 16;
            this.minPlayers = 2;
            //this.world =
            //TODO spawnpoints
        return;
    }
}

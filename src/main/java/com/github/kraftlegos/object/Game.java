package com.github.kraftlegos.object;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import javax.activation.DataHandler;
import java.util.Set;

public class Game {

    private String displayname;
    private int maxPlayers;
    private int minPlayers;
    private World world;
    private Set<Location> spawnPoints;
    private boolean isTeamGame;
    private Location lobbyPoint;

    //Active Game Objects
    private Set<GamePlayer> players;
    private Set<GamePlayer> spectators;
    private Set<GameTeam> team;
    private GameState gameState;

    public Game(String gamename) {
        //FileConfiguration fileConfiguration = DataHandler.getInstance().getGameInfo();
        this.maxPlayers = 16;
        this.minPlayers = 2;
        this.world = Bukkit.getServer().getWorld("world");
        this.isTeamGame = true;

        double redx = 0.0;
        double redy = 0.0;
        double redz = 0.0;

        double bluex = 0.0;
        double bluey = 0.0;
        double bluez = 0.0;

        double lobbyx = 0.0;
        double lobbyy = 0.0;
        double lobbyz = 0.0;
        //TODO Locations of spawnpoints in the world

        Location redSpawn = new Location(world, redx, redy, redz);
        Location blueSpawn = new Location(world, bluex, bluey, bluez);
        Location lobbySpawn = new Location(world, lobbyx, lobbyy, lobbyz);
        spawnPoints.add(redSpawn);
        spawnPoints.add(blueSpawn);
    }

    public boolean joinGame(GamePlayer gamePlayer) {
        if (gamePlayer.isTeamClass() && !isTeamGame) {
        return false;
        }

        if (isState(GameState.LOBBY) || isState(GameState.STARTING)) {
            if (getPlayers().size() == getMaxPlayers()) {
                gamePlayer.sendMessage("&cThis game has already started! Please try again in a few minutes!");
                return false;
            }
            gamePlayer.teleport(isState(gameState.LOBBY) ? lobbyPoint : null);
            getPlayers().add(gamePlayer);
            sendMessage(gamePlayer.getPlayer().getCustomName() + "&ejoined! (" + getPlayers().size() + "/" +  getMaxPlayers() + ")");

            if (getPlayers().size() == getMinPlayers() && !isState(GameState.STARTING)) {
                setState(GameState.STARTING);
                sendMessage(ChatColor.GREEN + "The game will now start in 30 seconds...");
            }
            return true;
        } else {
            getSpectators().add(gamePlayer);
            //TODO: Process as spectator
            return true;
        }
    }

    public void startCountdown() {
        //TODO
    }

    public void setState(GameState state) {
        this.gameState = state;
    }

    public Set<GamePlayer> getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Set<GamePlayer> getSpectators() {
        return spectators;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public boolean isTeamGame() {
        return isTeamGame;
    }

    public String getDisplayname() {
        return displayname;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isState(GameState state) {
        return getGameState() == state;
    }

    public void sendMessage(String message) {
        for (GamePlayer gamePlayer : getPlayers()) {
            gamePlayer.sendMessage(message);
        }
    }

    public enum GameState {
        LOBBY, STARTING, ACTIVE, DEATHMATCH, ENDING
    }
}

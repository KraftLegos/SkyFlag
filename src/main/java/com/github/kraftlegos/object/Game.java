package com.github.kraftlegos.object;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

public class Game {

    private String displayName;
    private int maxPlayers;
    private int minPlayers;
    private World world;
    private ArrayList<Location> spawnPoints = new ArrayList<>();
    private boolean isTeamGame;
    private Location lobbyPoint;

    //Active Game Objects
    private ArrayList<GamePlayer> players = new ArrayList<>();
    private ArrayList<GamePlayer> spectators = new ArrayList<>();
    private ArrayList<GameTeam> team = new ArrayList<>();
    private GameState gameState;

    public Game(String gameName) {
        //FileConfiguration fileConfiguration = DataHandler.getInstance().getGameInfo();
        this.displayName = gameName;
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
        spawnPoints.add(lobbySpawn);
    }

    public boolean joinGame(GamePlayer gamePlayer) {
        if (gamePlayer.isTeamClass() && !isTeamGame) {
            gamePlayer.sendMessage("TEST");
            return false;
        }

        if (isState(GameState.LOBBY) || isState(GameState.STARTING)) {
            if (getPlayers().size() == getMaxPlayers()) {
                gamePlayer.sendMessage("&cThis game has already started! Please try again in a few minutes!");
                return false;
            }
            if (isState(gameState.LOBBY)) {
                gamePlayer.teleport(lobbyPoint);
            } else {
                gamePlayer.teleport(null);
            }
            getPlayers().add(gamePlayer);
            sendMessage(gamePlayer.getPlayer().getCustomName() + "&ejoined! (" + getPlayers().size() + "/" + getMaxPlayers() + ")");

            if (getPlayers().size() == getMinPlayers() && !isState(GameState.STARTING)) {
                setState(GameState.STARTING);
                sendMessage(ChatColor.GREEN + "The game will now start in 30 seconds...");
            }
            return true;
        } else {
            getSpectators().add(gamePlayer);
            gamePlayer.sendMessage("YOU ARE NOW A SPECTATOR");
            //TODO: Process as spectator
            return true;
        }
    }

    public void startCountdown() {
        //TODO
    }

    public Game getGame() {
        return this;
    }

    public void setState(GameState state) {
        this.gameState = state;
    }

    public ArrayList<GamePlayer> getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public ArrayList<GamePlayer> getSpectators() {
        return spectators;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public boolean isTeamGame() {
        return isTeamGame;
    }

    public String getDisplayname() {
        return displayName;
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

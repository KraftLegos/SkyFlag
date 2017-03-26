package com.github.kraftlegos.object;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Game {

    private String displayName;
    private int maxPlayers;
    private int minPlayers;
    private World world;
    private ArrayList<Location> spawnPoints = new ArrayList<>();
    private boolean isTeamGame;
    private Location lobbyPoint;

    //Active Game Objects
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Player> spectators = new ArrayList<>();
    public ArrayList<GameTeam> team = new ArrayList<>();
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

        double lobbyx = -48.5;
        double lobbyy = 153.0;
        double lobbyz = 596.5;
        //TODO Locations of spawnpoints in the world

        Location redSpawn = new Location(world, redx, redy, redz);
        Location blueSpawn = new Location(world, bluex, bluey, bluez);
        Location lobbySpawn = new Location(world, lobbyx, lobbyy, lobbyz);
        spawnPoints.add(redSpawn);
        spawnPoints.add(blueSpawn);
        this.lobbyPoint = lobbySpawn;
    }

    public boolean joinGame(GamePlayer gamePlayer) {
        //if (gamePlayer.isTeamClass() && !isTeamGame) {
        //    gamePlayer.sendMessage("TEST");
        //    return false;
        //}

        //if (getPlayers().contains(gamePlayer)) {
        //    return false;
        //}

        Bukkit.getServer().broadcastMessage(players.toString());

        Player p = gamePlayer.getPlayer();

        if (isState(GameState.LOBBY) || isState(GameState.STARTING) && !players.contains(p)) {

            if (getPlayers().size() == getMaxPlayers()) {
                p.sendMessage("&cThis game has already started! Please try again in a few minutes!");
                return false;
            }
            if (isState(gameState.LOBBY) || isState(gameState.STARTING)) {
                gamePlayer.teleport(lobbyPoint, gamePlayer);
            } else if (isState(gameState.ACTIVE) || isState(gameState.DEATHMATCH) || isState(gameState.ENDING)){
                gamePlayer.teleport(spawnPoints.get(1), gamePlayer);
                p.sendMessage("You are now a spectator!");
                return false;
            }
            getPlayers().add(p);
            Bukkit.getServer().broadcastMessage(gamePlayer.getPlayer().getCustomName() + ChatColor.YELLOW + " joined! (" + getPlayers().size() + "/" + getMaxPlayers() + ")");

            if (getPlayers().size() == getMinPlayers() && !isState(GameState.STARTING)) {
                setState(GameState.STARTING);
                Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "The game will now start in 30 seconds...");
            }
            return true;
        } else {
            getSpectators().add(p);
            p.sendMessage("YOU ARE NOW A SPECTATOR");
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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public ArrayList<Player> getSpectators() {
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
        for (Player player : getPlayers()) {
            player.sendMessage(message);
        }
    }

    public enum GameState {
        LOBBY, STARTING, ACTIVE, DEATHMATCH, ENDING
    }
}

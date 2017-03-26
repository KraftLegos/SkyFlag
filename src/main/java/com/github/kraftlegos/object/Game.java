package com.github.kraftlegos.object;

import com.github.kraftlegos.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Game {

    //Active Game Objects
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Player> spectators = new ArrayList<>();
    public ArrayList<GameTeam> team = new ArrayList<>();
    private String displayName;
    private int maxPlayers;
    private int minPlayers;
    private World world;
    private ArrayList<Location> spawnPoints = new ArrayList<>();
    private boolean isTeamGame;
    private Location lobbyPoint;
    private GameState gameState;

    private int timeUntilStart;

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

        //DEBUG: Bukkit.getServer().broadcastMessage(players.toString());

        Player p = gamePlayer.getPlayer();

        if (isState(GameState.LOBBY) || isState(GameState.STARTING)) {
            if (players.contains(p)) {
                p.sendMessage(ChatColor.RED + "You are already in the game!");
                return false;
            }

            if (getPlayers().size() == getMaxPlayers()) {
                p.sendMessage("&cThis game has already started! Please try again in a few minutes!");
                return false;
            }
            gamePlayer.teleport(lobbyPoint, gamePlayer);
            players.add(p);
            Bukkit.getServer().broadcastMessage(gamePlayer.getPlayer().getCustomName() + ChatColor.YELLOW + " joined! (" + getPlayers().size() + "/" + getMaxPlayers() + ")");

            if (getPlayers().size() == getMinPlayers() && !isState(GameState.STARTING)) {
                setState(GameState.STARTING);
                startCountdown();
                Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Minimum players reached! The game will now start in 30 seconds...");
            }
        } else if (isState(gameState.ACTIVE) || isState(gameState.DEATHMATCH) || isState(gameState.ENDING)) {
            gamePlayer.teleport(spawnPoints.get(1), gamePlayer);
            p.sendMessage("You are now a spectator!");
            return false;
        } else {
            p.sendMessage("You are already in the game!");
        }
        return true;
    }

    public void startCountdown() {
        timeUntilStart = 30;
        while(true) {
            for(; timeUntilStart >= 0; timeUntilStart--) {
                if(timeUntilStart == 0) {
                    //TODO Start
                    break;
                }

                if (timeUntilStart == 30 || timeUntilStart == 20 || timeUntilStart == 10 || timeUntilStart == 5 || timeUntilStart == 4 || timeUntilStart == 3 || timeUntilStart == 2 || timeUntilStart == 1) {
                    sendMessage(ChatColor.YELLOW + "Starting in " + ChatColor.RED + timeUntilStart + ChatColor.YELLOW + " seconds!");
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Bukkit.getLogger().severe("GAME CRASHED: READ ERROR ABOVE!");
                    for (Player player : players) {
                        player.kickPlayer("A fatal error occured, please report this to Kraft!");
                    }
                }
            }
        }
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

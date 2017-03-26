package com.github.kraftlegos.object;

import org.bukkit.Location;

import java.util.HashMap;

public class GameTeam {

    private GameTeamState gameTeamState;
    private HashMap<GamePlayer, GameTeam> teams = new HashMap<>();

    public void teleport(Location location, GamePlayer p) {
        if (teams.containsKey(p)) {
            p.getPlayer().teleport(location);
        }
    }

    public void sendMessage(String message) {
        //TODO
    }

    public HashMap<GamePlayer, GameTeam> getPlayerTeam() {
        return teams;
    }

    public enum GamePlayerTeam {
        RED, BLUE;
    }

    public enum GameTeamState {

    }

}

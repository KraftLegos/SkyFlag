package com.github.kraftlegos.constructors;

import org.bukkit.entity.Player;

public class GamePlayer {

    private Player player = null;
    private GameTeam team = null;

    public GamePlayer(Player player) {
        this.player = player;
    }

    public GamePlayer(GameTeam team) {
        this.team = team;
    }

}

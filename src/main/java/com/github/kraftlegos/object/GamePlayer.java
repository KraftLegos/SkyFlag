package com.github.kraftlegos.object;

import com.github.kraftlegos.utility.ChatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GamePlayer {

    private Player player = null;
    private GameTeam team = null;
    private GamePlayerState gamePlayerState;

    public GamePlayer(Player player) { this.player = player;}

    public GamePlayer(GameTeam team) { this.team = team; }

    public boolean isTeamClass() {
        return team == null && player == null;
    }

    public Player getPlayer() {
        return player;
    }

    public GameTeam getTeam() {
        return team;
    }

    public void sendMessage(String message) {
        if (isTeamClass()) {
            player.sendMessage(message);
        } else {
            ChatUtil.format(message);
        }
    }

    public void teleport(Location location, GamePlayer p) {
        p.getPlayer().teleport(location);
    }

    public enum GamePlayerState {

    }
}

package com.github.kraftlegos.listeners;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by kgils on 3/26/2017.
 */
public class OnFoodChange implements Listener {

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        if (GameManager.getGame().isState(Game.GameState.ENDING) || GameManager.getGame().isState(Game.GameState.STARTING) || GameManager.getGame().isState(Game.GameState.LOBBY)) {
            e.setCancelled(true);
        }

        Player p = (Player) e.getEntity();
        if (GameManager.getGame().spectators.contains(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPing(ServerListPingEvent e){
        e.setMotd(ChatColor.RED + "" + ChatColor.BOLD + "Kraft's SKYFLAG Server!" + ChatColor.RESET + "\n" + ChatColor.GOLD + "Current State: " + GameManager.getGame().getGameState().toString());
    }

}

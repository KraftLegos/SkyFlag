package com.github.kraftlegos.listeners;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Created by kgils on 3/26/2017.
 */
public class OnDeath implements Listener {

    private Player t;
    private Player p;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

        this.t = e.getEntity();

        if (GameManager.getGame().getGameState() != Game.GameState.STARTING || GameManager.getGame().getGameState() != Game.GameState.LOBBY){
            if (!GameManager.getGame().players.contains(t.getName())) {
                e.setDeathMessage(null);
            }
        }

        if (e.getDeathMessage().contains("was slain by")) {
            this.p = e.getEntity().getKiller();
            e.setDeathMessage(t.getCustomName() + ChatColor.YELLOW + " had their head chopped off by " + p.getCustomName());

            t.getLocation().getWorld().strikeLightningEffect(t.getLocation());
            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.6F);
        }

        if (e.getDeathMessage().contains("fell out of the world")) {
            if (OnPlayerDamage.lastDamager.containsKey(t.getName())) {
                this.p = Bukkit.getServer().getPlayer(OnPlayerDamage.lastDamager.get(t.getName()));
                e.setDeathMessage(t.getCustomName() + ChatColor.YELLOW + " was thrown into the abyss by " + p.getCustomName());
                OnPlayerDamage.lastDamager.remove(t.getName());
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 2F);
            }
        }

        if (e.getDeathMessage().contains("fell from a high place") || e.getDeathMessage().contains("was thrown off a cliff")) {
            if (OnPlayerDamage.lastDamager.containsKey(t.getName())) {
                this.p = Bukkit.getServer().getPlayer(OnPlayerDamage.lastDamager.get(t.getName()));
                e.setDeathMessage(t.getCustomName() + ChatColor.YELLOW + " was thrown off a cliff by " + p.getCustomName());
                OnPlayerDamage.lastDamager.remove(t.getName());
                t.getLocation().getWorld().strikeLightningEffect(t.getLocation());
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.6F);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        if (GameManager.getGame().players.contains(player)) {
            if (GameManager.getGame().getGameState() != Game.GameState.STARTING || GameManager.getGame().getGameState() != Game.GameState.LOBBY) {
                if (GameManager.getGame().getRedTeam().contains(player.getName())) {
                    e.setRespawnLocation(GameManager.getGame().redSpawn);
                } else if (GameManager.getGame().getGameState() == Game.GameState.STARTING || GameManager.getGame().getGameState() == Game.GameState.LOBBY){
                    e.setRespawnLocation(GameManager.getGame().lobbyPoint);
                } else {
                    e.setRespawnLocation(GameManager.getGame().blueSpawn);
                }
            } else {
                e.setRespawnLocation(GameManager.getGame().lobbyPoint);
            }
        }
    }
}

package com.github.kraftlegos.listeners;

import com.github.kraftlegos.Main;
import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class OnPlayerDamage implements Listener {

    private Main plugin;
    public OnPlayerDamage(Main instance) { plugin = instance; }

    public HashMap<String, Integer> taskList = new HashMap<>();
    public static HashMap<String, String> lastDamager = new HashMap<>();
    private Player p;
    private Player t;
    private int task;
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {

            if (e.getDamager() instanceof Player) this.p = (Player) e.getDamager();
        if (GameManager.getGame().spectators.contains(p)) {
            e.setCancelled(true);
            return;
        }

            if (e.getDamager() instanceof LightningStrike) {
                e.setCancelled(true);
                return;
            }

            if (e.getEntity() instanceof Player) {
                this.t = (Player) e.getEntity();


                if (GameManager.getGame().getGameState() == Game.GameState.STARTING || GameManager.getGame().getGameState() == Game.GameState.LOBBY || GameManager.getGame().getGameState() == Game.GameState.GRACE) {
                    e.setCancelled(true);
                    return;
                    //Bukkit.getServer().broadcastMessage("TEST");
                } else {
                    if (GameManager.getGame().getBlueTeam().contains(p.getName())) {
                        if (GameManager.getGame().getBlueTeam().contains(t.getName())) {
                            e.setCancelled(true);
                            return;
                        }
                    }
                    if (GameManager.getGame().getRedTeam().contains(p.getName())) {
                        if (GameManager.getGame().getRedTeam().contains(t.getName())) {
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
                if (!lastDamager.containsKey(t.getName())) {
                    lastDamager.put(t.getName(), p.getName());
                } else {
                    lastDamager.remove(t.getName());
                    lastDamager.put(t.getName(), p.getName());
                }
                if (taskList.containsKey(t.getName())) {
                 Bukkit.getServer().getScheduler().cancelTask(taskList.get(t.getName()));
                    this.task = Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            lastDamager.remove(t.getName());
                            taskList.remove(t.getName());
                        }
                    }, 15 * 20L);

                    taskList.put(t.getName(), task);
                } else {
                    this.task = Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            lastDamager.remove(t.getName());
                            taskList.remove(t.getName());
                        }
                    }, 15 * 20L);

                    taskList.put(t.getName(), task);
                }

            }


            //Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
              //  @Override
                //public void run() {
                  //  lastDamager.remove(t.getName());
                //}
            //}, 20*25);
        }

    @EventHandler
    public  void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING)) {
                e.setCancelled(true);
            }
        }
    }
}

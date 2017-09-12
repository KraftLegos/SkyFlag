package com.github.kraftlegos.listeners;

import com.github.kraftlegos.Main;
import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

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

        if (e.getDamager() instanceof EnderDragon) {
            if (e.getEntity() instanceof Player) {
                Vector dir = e.getDamager().getLocation().getDirection();
                Vector vec = new Vector(-dir.getX() * 4.1D, dir.getY(), -dir.getZ() * 4.1D);
                e.getEntity().setVelocity(vec);
            }
        }

        if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();

            if (arrow.getShooter() instanceof Player) {

                if (GameManager.getGame().isState(Game.GameState.STARTING) || GameManager.getGame().isState(Game.GameState.LOBBY) || GameManager.getGame().isState(Game.GameState.GRACE)) {
                    e.setCancelled(true);
                    if (GameManager.getGame().isState(Game.GameState.GRACE)) (e.getDamager()).sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Please wait unill PvP enables!");
                    return;
                    //Bukkit.getServer().broadcastMessage("TEST");
                }

                this.p = (Player) arrow.getShooter();
                this.t = (Player) e.getEntity();

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
                this.task = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        lastDamager.remove(t.getName());
                        taskList.remove(t.getName());
                    }
                }, 15 * 20L);

                taskList.put(t.getName(), task);
            } else {
                this.task = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        lastDamager.remove(t.getName());
                        taskList.remove(t.getName());
                    }
                }, 15 * 20L);

                taskList.put(t.getName(), task);
            }
        }

        if (e.getDamager() instanceof Snowball) {
            Snowball snowball = (Snowball) e.getDamager();

            if (snowball.getShooter() instanceof Player) {

                if (GameManager.getGame().isState(Game.GameState.STARTING) || GameManager.getGame().isState(Game.GameState.LOBBY) || GameManager.getGame().isState(Game.GameState.GRACE)) {
                    e.setCancelled(true);
                    if (GameManager.getGame().isState(Game.GameState.GRACE)) (e.getDamager()).sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Please wait unill PvP enables!");
                    return;
                    //Bukkit.getServer().broadcastMessage("TEST");
                }

                this.p = (Player) snowball.getShooter();
                this.t = (Player) e.getEntity();
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
                this.task = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        lastDamager.remove(t.getName());
                        taskList.remove(t.getName());
                    }
                }, 15 * 20L);

                taskList.put(t.getName(), task);
            } else {
                this.task = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        lastDamager.remove(t.getName());
                        taskList.remove(t.getName());
                    }
                }, 15 * 20L);

                taskList.put(t.getName(), task);
            }
        }

        if (e.getDamager() instanceof FishHook) {
            FishHook fishHook = (FishHook) e.getDamager();
            if (fishHook.getShooter() instanceof Player) {

                if (GameManager.getGame().isState(Game.GameState.STARTING) || GameManager.getGame().isState(Game.GameState.LOBBY) || GameManager.getGame().isState(Game.GameState.GRACE)) {
                    e.setCancelled(true);
                    if (GameManager.getGame().isState(Game.GameState.GRACE)) (e.getDamager()).sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Please wait unill PvP enables!");
                    return;
                    //Bukkit.getServer().broadcastMessage("TEST");
                }

                this.p = (Player) fishHook.getShooter();
                this.t = (Player) e.getEntity();
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
                this.task = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        lastDamager.remove(t.getName());
                        taskList.remove(t.getName());
                    }
                }, 15 * 20L);

                taskList.put(t.getName(), task);
            } else {
                this.task = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        lastDamager.remove(t.getName());
                        taskList.remove(t.getName());
                    }
                }, 15 * 20L);

                taskList.put(t.getName(), task);
            }
        }

        if (e.getDamager() instanceof LightningStrike) {
            e.setCancelled(true);
            return;
        }

        if (e.getDamager() instanceof Player) {
            this.p = (Player) e.getDamager();
            if (GameManager.getGame().spectators.contains(p)) {
                e.setCancelled(true);
                return;
            }

            if (e.getEntity() instanceof Player) {
                this.t = (Player) e.getEntity();


                if (GameManager.getGame().isState(Game.GameState.STARTING) || GameManager.getGame().isState(Game.GameState.LOBBY) || GameManager.getGame().isState(Game.GameState.GRACE)) {
                    e.setCancelled(true);
                    if (GameManager.getGame().isState(Game.GameState.GRACE)) (e.getDamager()).sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Please wait unill PvP enables!");
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
                    this.task = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            lastDamager.remove(t.getName());
                            taskList.remove(t.getName());
                        }
                    }, 15 * 20L);

                    taskList.put(t.getName(), task);
                } else {
                    this.task = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            lastDamager.remove(t.getName());
                            taskList.remove(t.getName());
                        }
                    }, 15 * 20L);

                    taskList.put(t.getName(), task);
                }

                if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill " + t.getName());
                }

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



            Player p = (Player) e.getEntity();
            if (OnDeath.spawnProt.contains(p.getName())) {
                e.setCancelled(true);
            }

            if (GameManager.getGame().isState(Game.GameState.LOBBY) || GameManager.getGame().isState(Game.GameState.STARTING) || GameManager.getGame().isState(Game.GameState.ENDING)) {

                e.setCancelled(true);
            }
            //if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            //    ((Player) e.getEntity()).damage(50);
            //}
        }

        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (GameManager.getGame().spectators.contains(p)) {
                e.setCancelled(true);
            }
        }
    }
}

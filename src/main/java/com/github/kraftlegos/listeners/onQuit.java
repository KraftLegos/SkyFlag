package com.github.kraftlegos.listeners;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class onQuit implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Game game = GameManager.getGame();
        e.setQuitMessage(null);
        if (game.getGameState() == Game.GameState.LOBBY || game.getGameState() == Game.GameState.STARTING) {
            if (game.players.contains(p) || game != null) {
                game.players.remove(p);
                game.sendMessage(p.getCustomName() + ChatColor.YELLOW + " quit! (" + game.players.size() + "/" + game.getMaxPlayers() + ")");

                game.board.resetScores(ChatColor.GREEN + "Players: " + (game.players.size()+1) + "/" + game.getMaxPlayers());
                game.line2 = game.objective.getScore( ChatColor.GREEN + "Players: " + game.players.size() + "/" + game.getMaxPlayers());
                game.line2.setScore(6);
                return;
            }
        } else {
            if (!game.spectators.contains(p)) {

                    // First step: Go through his armor and drop it at his location
                    ItemStack[] armorContents = p.getInventory().getArmorContents();
                    for (ItemStack content : armorContents) {
                        if (content.getAmount() != 0) {
                            p.getWorld().dropItemNaturally(p.getLocation(), content);
                        }
                    }
                // Second step: delete his armor
                p.getInventory().setArmorContents(new ItemStack[4]);

                // First step: Go through his armor and drop it at his location
                for (ItemStack i : p.getInventory().getContents()) {
                    if (i != null) {
                        p.getWorld().dropItemNaturally(p.getLocation(), i);
                        p.getInventory().remove(i);
                    }
                }


                game.getRedTeam().remove(p.getName());
                game.getBlueTeam().remove(p.getName());
                game.getPlayerTeams().remove(p.getUniqueId());
                game.players.remove(p);
                // Second step: delete his armor
                p.getInventory().clear();

                p.getLocation().getWorld().strikeLightningEffect(p.getLocation());
                game.sendMessage(p.getCustomName() + ChatColor.YELLOW + " was killed when they disconnected!");

                game.board.resetScores(ChatColor.GREEN + "Players: " + (game.players.size() + 1));
                game.line2 = game.objective.getScore(ChatColor.GREEN + "Players: " + game.players.size());
                game.line2.setScore(6);
            }
        }
        //TODO Fix double leave messages
        //TODO Fix bugged message
    }
}

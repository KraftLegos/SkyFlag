package com.github.kraftlegos.commands;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Kraft on 4/10/2017.
 */
public class ForceAdd implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.hasPermission("skyflag.forceadd")) {
                if (GameManager.getGame().isState(Game.GameState.GRACE) || GameManager.getGame().isState(Game.GameState.ACTIVE)) {
                    if (Bukkit.getServer().getPlayer(args[0]) != null) {
                        Player t = Bukkit.getServer().getPlayer(args[0]);
                        if (!GameManager.getGame().players.contains(t)) {

                            if (GameManager.getGame().getRedTeam().size() < GameManager.getGame().getBlueTeam().size()) {
                                GameManager.getGame().sendMessage(t.getCustomName() + ChatColor.YELLOW + " was moved into the game!");

                                t.sendMessage(ChatColor.GREEN + "You were added to the " + ChatColor.RED + "RED " + ChatColor.GREEN + "team!");
                                p.sendMessage(ChatColor.GREEN + "You added " + t.getCustomName() + ChatColor.GREEN + " to the current game! They were placed on the " + ChatColor.RED + "RED " + ChatColor.GREEN + "team!");
                                GameManager.getGame().removeSpectator(t, Game.TeamType.RED);
                                GameManager.getGame().getPlayerTeams().put(p.getUniqueId(), Game.TeamType.RED);
                                GameManager.getGame().redScoreTeam.addEntry(t.getName());
                                GameManager.getGame().redScoreTeam.setPrefix("§c[R] ");

                            } else if (GameManager.getGame().getRedTeam().size() > GameManager.getGame().getBlueTeam().size()) {
                                GameManager.getGame().sendMessage(t.getCustomName() + ChatColor.YELLOW + " was moved into the game!");

                                t.sendMessage(ChatColor.GREEN + "You were added to the " + ChatColor.BLUE + "BLUE " + ChatColor.GREEN + "team!");
                                p.sendMessage(ChatColor.GREEN + "You added " + t.getCustomName() + ChatColor.GREEN + " to the current game! They were placed on the " + ChatColor.BLUE + "BLUE " + ChatColor.GREEN + "team!");
                                GameManager.getGame().removeSpectator(t, Game.TeamType.BLUE);
                                GameManager.getGame().blueScoreTeam.addEntry(t.getName());
                                GameManager.getGame().getPlayerTeams().put(p.getUniqueId(), Game.TeamType.BLUE);
                                GameManager.getGame().blueScoreTeam.setPrefix("§9[B] ");

                            } else {
                                int randomNumber = (int) (Math.random() * 2 + 1);

                                if (randomNumber == 1) {
                                    GameManager.getGame().sendMessage(t.getCustomName() + ChatColor.YELLOW + " was moved into the game!");

                                    t.sendMessage(ChatColor.GREEN + "You were added to the " + ChatColor.BLUE + "BLUE " + ChatColor.GREEN + "team!");
                                    p.sendMessage(ChatColor.GREEN + "You added " + t.getCustomName() + ChatColor.GREEN + " to the current game! They were placed on the " + ChatColor.BLUE + "BLUE " + ChatColor.GREEN + "team!");
                                    GameManager.getGame().removeSpectator(t, Game.TeamType.BLUE);
                                    GameManager.getGame().getPlayerTeams().put(p.getUniqueId(), Game.TeamType.BLUE);
                                    GameManager.getGame().blueScoreTeam.addEntry(t.getName());
                                    GameManager.getGame().blueScoreTeam.setPrefix("§9[B] ");
                                } else if (randomNumber == 2) {
                                    GameManager.getGame().sendMessage(t.getCustomName() + ChatColor.YELLOW + " was moved into the game!");

                                    t.sendMessage(ChatColor.GREEN + "You were added to the " + ChatColor.RED + "RED " + ChatColor.GREEN + "team!");
                                    p.sendMessage(ChatColor.GREEN + "You added " + t.getCustomName() + ChatColor.GREEN + " to the current game! They were placed on the " + ChatColor.RED + "RED " + ChatColor.GREEN + "team!");
                                    GameManager.getGame().removeSpectator(t, Game.TeamType.RED);
                                    GameManager.getGame().getPlayerTeams().put(p.getUniqueId(), Game.TeamType.RED);
                                    GameManager.getGame().redScoreTeam.addEntry(t.getName());
                                    GameManager.getGame().redScoreTeam.setPrefix("§c[R] ");
                                }
                            }

                            GameManager.getGame().board.resetScores(ChatColor.GREEN + "Players: " + (GameManager.getGame().players.size() - 1));
                            GameManager.getGame().line2 = GameManager.getGame().objective.getScore(ChatColor.GREEN + "Players: " + GameManager.getGame().players.size());
                            GameManager.getGame().line2.setScore(6);
                        } else {
                            p.sendMessage(ChatColor.RED + "That player is already in a game!");
                            return true;
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "I could not find that player, did you misstype?");
                        return true;
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "The game is not even running silly!");
                    return true;
                }
            } else {
                p.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
                return true;
            }
        }
        return true;
    }
}

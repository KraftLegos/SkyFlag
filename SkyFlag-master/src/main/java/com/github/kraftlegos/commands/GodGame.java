package com.github.kraftlegos.commands;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Kraft on 4/13/2017.
 */
public class GodGame implements CommandExecutor {

    Game game = GameManager.getGame();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("skyflag.godgame")) {
            if (game.isState(Game.GameState.STARTING) || game.isState(Game.GameState.LOBBY)) {
                sender.sendMessage(ChatColor.GREEN + "Successfully changed the game type to GODGAME!");
                Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "The server's mode was set to GODGAME by " + sender.getName());
                //TODO Change the gamemode to GODGAME

                GameManager.getGame().setGameType(Game.GameType.GODGAME);

            } else {
                sender.sendMessage(ChatColor.RED + "That game has already started!");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to run that command!");
        }
        return true;
    }
}
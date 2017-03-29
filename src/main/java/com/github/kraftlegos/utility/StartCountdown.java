package com.github.kraftlegos.utility;

import com.avaje.ebeaninternal.server.cluster.Packet;
import com.github.kraftlegos.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

/**
 * Created by kgils on 3/25/2017.
 */
public class StartCountdown implements Runnable{

    private static int timeUntilStart;

    public void run() {

            timeUntilStart = 30;
            for (; timeUntilStart >= 0; timeUntilStart--) {
                GameManager.getGame().board.resetScores(timeUntilStart-1 + " until start!");
                GameManager.getGame().line4 = GameManager.getGame().objective.getScore(timeUntilStart + " until start!");
                GameManager.getGame().line4.setScore(4);

                if (timeUntilStart == 0) {
                    //GameManager.getGame().sendMessage(ChatColor.GREEN + "DEBUG: STARTED");
                    GameManager.getGame().startGame();
                    break;

                }

                if (timeUntilStart == 20 || timeUntilStart == 10 || timeUntilStart == 5 || timeUntilStart == 4 || timeUntilStart == 3 || timeUntilStart == 2 || timeUntilStart == 1) {
                    GameManager.getGame().sendMessage(ChatColor.YELLOW + "Starting in " + ChatColor.RED + timeUntilStart + ChatColor.YELLOW + " seconds!");
                    for (Player pl : GameManager.getGame().players) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + pl.getName() +" title {\"text\":\"" + timeUntilStart + "\",\"color\":\"red\"}"); //JSON formatting is invalid!
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + pl.getName() + " times 0 20 0");
                        pl.playSound(pl.getLocation(), Sound.NOTE_PLING, 1.0F, 0.6F);
                    }

                }

                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Bukkit.getLogger().severe("GAME CRASHED: READ ERROR ABOVE!");
                    for (Player player : GameManager.getGame().players) {
                        player.kickPlayer("A fatal error occured, please report this to Kraft!");
                    }
                }
            }
/*
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("GAME CRASHED: READ ERROR ABOVE!");
                for (Player player : GameManager.getGame().players) {
                    player.kickPlayer("A fatal error occured, please report this to Kraft!");
                }

        }
  */  }
}

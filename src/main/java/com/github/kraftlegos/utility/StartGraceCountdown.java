package com.github.kraftlegos.utility;

import com.github.kraftlegos.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class StartGraceCountdown implements Runnable{

    private static int timeUntilStart;

    public void run() {

        timeUntilStart = 120;
        for (; timeUntilStart >= 0; timeUntilStart--) {
            if (timeUntilStart == 0) {
                //GameManager.getGame().sendMessage(ChatColor.GREEN + "DEBUG: STARTED");
                GameManager.getGame().startActive();
                break;

            }

            if (timeUntilStart == 60 || timeUntilStart == 30 || timeUntilStart == 10 || timeUntilStart == 5 || timeUntilStart == 4 || timeUntilStart == 3 || timeUntilStart == 2 || timeUntilStart == 1) {
                GameManager.getGame().sendMessage(ChatColor.YELLOW + "PvP enables in " + timeUntilStart + ChatColor.YELLOW + " seconds!");
                for (Player pl : GameManager.getGame().players) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + pl.getName() +" title {\"text\":\"" + timeUntilStart + "\",\"color\":\"red\"}"); //JSON formatting is invalid!
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + pl.getName() + " times 0 20 0");
                    pl.playSound(pl.getLocation(), Sound.NOTE_STICKS, 1.0F, 2F);
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
    }
}

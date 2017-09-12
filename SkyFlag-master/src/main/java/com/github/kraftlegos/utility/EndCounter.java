package com.github.kraftlegos.utility;

import com.github.kraftlegos.listeners.OnDeath;
import com.github.kraftlegos.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by Kraft on 4/3/2017.
 */
public class EndCounter implements Runnable {

    private static int timeUntilStart;
    public void run() {
    timeUntilStart = 15;
        for (; timeUntilStart >= 0; timeUntilStart--) {

            if (timeUntilStart == 1) {
                //GameManager.getGame().kickAllPlayers();
            }
            if (timeUntilStart == 0) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
                break;
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

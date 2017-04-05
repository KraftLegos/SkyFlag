package com.github.kraftlegos.utility;

import com.github.kraftlegos.listeners.OnDeath;
import com.github.kraftlegos.managers.GameManager;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by kgils on 4/2/2017.
 */
public class ReturnRedFlag implements Runnable {
    private static int timeUntilStart;

    public void run() {

        timeUntilStart = 15;
        for (; timeUntilStart >= 0; timeUntilStart--) {


            OnDeath.redFlagDropLocation.getWorld().playEffect(OnDeath.redFlagDropLocation, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

            double x = OnDeath.redFlagDropLocation.getX();
            double y = OnDeath.redFlagDropLocation.getY();
            double z = OnDeath.redFlagDropLocation.getZ();
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=ArmorStand,r=1,x=" + x + ",y=" + y + ",z=" + z + ",r=0]");
            createHologram(OnDeath.redFlagDropLocation, ChatColor.RED + "" + timeUntilStart + "s");
            //if (timeUntilStart == 0 || OnDeath.redFlagDropped == false) {
            //    break;
            //}

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

    public static void createHologram(Location l, String name) {
        ArmorStand am = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
        am.setArms(false);
        am.setGravity(false);
        am.setVisible(false);
        am.setCustomName(name);
        am.setCustomNameVisible(true);
    }
}


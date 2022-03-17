package me.stahrry.CabsHGKits;

import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Miner implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();

    int MINER_IRON_ORE_CHANCE = Integer.parseInt(configFile.getString("MINER_IRON_ORE_CHANCE"));

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak (BlockBreakEvent event) {
        Player breaker = event.getPlayer();
        Block block = event.getBlock();

        if (this.api.getPlayerCanUseAbility(breaker, 20, true)) {
            if (block.getType() == Material.IRON_ORE) {
                Location centerOfBlock = block.getLocation();

                Random r = new Random();
                int Chance = MINER_IRON_ORE_CHANCE;
                int Amount = 1;
                int randInt = r.nextInt(200) + 1;
                event.setDropItems(false);

                if (randInt == 1) {
                    block.getWorld().dropItemNaturally(centerOfBlock, new ItemStack(Material.DIAMOND, 2));
                } else {
                    if (randInt > Chance) {
                        Amount = 3;
                    }
                    block.getWorld().dropItemNaturally(centerOfBlock, new ItemStack(Material.IRON_INGOT, Amount));
                }
            }
        }
    }
}
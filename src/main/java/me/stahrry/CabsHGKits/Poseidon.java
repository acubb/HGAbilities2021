package me.stahrry.CabsHGKits;

import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class Poseidon implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();
    HGSupport hgSupport = new HGSupport();

    Hashtable<Player, Boolean> poseidonDict = new Hashtable<>();
    double POSEIDON_LS = Double.parseDouble(configFile.getString("POSEIDON_LS"));
    int POSEIDON_REGEN = Integer.parseInt(configFile.getString("POSEIDON_REGEN"));

    @EventHandler(priority = EventPriority.NORMAL)
    private void onPlayerDamageByArrow (EntityDamageByEntityEvent event) {
        Entity entityDmg = event.getDamager();
        if (entityDmg instanceof Arrow) {
            Entity shooter = (Entity) ((Arrow) entityDmg).getShooter();
            if (shooter instanceof Player) {
                Player playerShooter = (Player) shooter;
                if (this.api.getPlayerCanUseAbility(playerShooter, 21, true)) {
                    boolean touchingWater = poseidonDict.get(playerShooter);
                    if (touchingWater) {
                        double arrowDmg = event.getFinalDamage();
                        double healing = arrowDmg * (POSEIDON_LS / 100);
                        double newShooterHealth = playerShooter.getHealth() + healing;
                        playerShooter.sendMessage(ChatColor.LIGHT_PURPLE + "Healing: " + healing);
                        if (newShooterHealth > 20) {
                            newShooterHealth = 20;
                        }
                        playerShooter.setHealth(newShooterHealth);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onPlayerConsume (PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (this.api.getPlayerCanUseAbility(player, 21, true)) {
            if (item.getType() == Material.POTION) {
                PotionType potionType = hgSupport.getPotionType(item);
                if (potionType == PotionType.WATER) {
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (POSEIDON_REGEN * 20), 1));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.api.getPlayerCanUseAbility(player, 21, true)) {
            if (player.getLocation().getBlock().getType() == Material.WATER || player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1000000, 2));
                poseidonDict.putIfAbsent(player, true);
                poseidonDict.put(player, true);
            } else {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.removePotionEffect(PotionEffectType.WATER_BREATHING);
                poseidonDict.putIfAbsent(player, false);
                poseidonDict.put(player, false);
            }
        }
    }
}

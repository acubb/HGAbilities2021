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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Shadow implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();
    Hashtable<Player, Double> shadowDict = new Hashtable<>();
    World world = Bukkit.getWorld("world");

    double SHADOW_DMG_CAP = Double.parseDouble(configFile.getString("SHADOW_DMG_CAP"));
    double SHADOW_LIGHT_DMG = Double.parseDouble(configFile.getString("SHADOW_LIGHT_DMG"));
    double SHADOW_DARK_DMG = Double.parseDouble(configFile.getString("SHADOW_DARK_DMG"));

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.api.getPlayerCanUseAbility(player, 29, true)) {
            if (world.getTime() >= 12750 && world.getTime() <= 23000 || player.getLocation().getBlock().getLightLevel() <= 2) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1, false, false));
            } else {
                player.removePotionEffect(PotionEffectType.SPEED);
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity (EntityDamageByEntityEvent event) {
        Entity entityDamager = event.getDamager();
        Entity entityDefender = event.getEntity();

        if (entityDamager instanceof Player) {
            Player playerDamager = (Player) entityDamager;

            if (entityDefender.getType() == EntityType.PLAYER) {
                Player playerDefender = (Player) entityDefender;
                if (this.api.getPlayerCanUseAbility(playerDamager, 48, true)) {
                    EntityDamageEvent.DamageCause cause = event.getCause();
                    if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                        if (!shadowDict.containsKey(playerDamager)) {
                            shadowDict.put(playerDamager, 0.0);
                        }
                        double percentIncrease = shadowDict.get(playerDamager);
                        double damage = event.getDamage();
                        double newDamage = damage * (1 + (percentIncrease / 100));
                        playerDamager.sendMessage(ChatColor.LIGHT_PURPLE + "Damage dealt: " + newDamage);
                        event.setDamage(newDamage);
                    }
                }
            }
            if (entityDefender.getType() != EntityType.PLAYER) {
                if (this.api.getPlayerCanUseAbility(playerDamager, 48, true)) {
                    double newDamage = event.getDamage() * 2;
                    event.setDamage(newDamage);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerKill (EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        EntityType killedType = event.getEntityType();
        if (killer != null) {
            if (this.api.getPlayerCanUseAbility(killer, 48, true)) {
                if (!shadowDict.containsKey(killer)) {
                    shadowDict.put(killer, 0.0);
                }
                double currDamage = shadowDict.get(killer);
                boolean underCap = currDamage < SHADOW_DMG_CAP;
                if (underCap) {
                    if (world.getTime() >= 12750 && world.getTime() <= 23000 || killer.getLocation().getBlock().getLightLevel() <= 2) {
                        currDamage += SHADOW_DARK_DMG;
                    }
                    else {
                        currDamage += SHADOW_LIGHT_DMG;
                    }
                    if (currDamage > SHADOW_DMG_CAP) {
                        currDamage = SHADOW_DMG_CAP;
                    }
                }
                else {
                    currDamage = SHADOW_DMG_CAP;
                }
                shadowDict.put(killer, currDamage);
                if (underCap) {
                    killer.sendMessage(ChatColor.LIGHT_PURPLE + "You now have " + currDamage + "% increased melee damage.");
                }
                else {
                    killer.sendMessage(ChatColor.LIGHT_PURPLE + "You are capped at " + SHADOW_DMG_CAP + "% increased melee damage.");

                }
            }
        }
    }
}
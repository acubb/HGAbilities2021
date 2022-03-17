package me.stahrry.CabsHGKits;

import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Vampire implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();
    Hashtable<Player, Double> vampDict = new Hashtable<>();

    int VAMP_FIRE_RES = Integer.parseInt(configFile.getString("VAMP_FIRE_RES"));
    double VAMP_START_LS = Double.parseDouble(configFile.getString("VAMP_START_LS"));
    double VAMP_PLAYER_KILL_LS = Double.parseDouble(configFile.getString("VAMP_PLAYER_KILL_LS"));
    double VAMP_MOB_KILL_LS = Double.parseDouble(configFile.getString("VAMP_MOB_KILL_LS"));
    double VAMP_LS_CAP = Double.parseDouble(configFile.getString("VAMP_LS_CAP"));

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity (EntityDamageByEntityEvent event) {
        Entity entityDamager = event.getDamager();
        Entity entityDefender = event.getEntity();

        if (entityDamager instanceof Player) {
            Player playerDamager = (Player) entityDamager;

            if (this.api.getPlayerCanUseAbility(playerDamager, 43, true)) {
                EntityDamageEvent.DamageCause cause = event.getCause();
                if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                    if (!vampDict.containsKey(playerDamager)) {
                        vampDict.put(playerDamager, VAMP_START_LS);
                    }
                    double damage = event.getDamage();
                    double lifeSteal = vampDict.get(playerDamager);
                    double healing = damage * (lifeSteal / 100);
                    playerDamager.sendMessage(ChatColor.LIGHT_PURPLE + "Health gained: " + healing);
                    double health = playerDamager.getHealth();
                    double newHealth = health + healing;
                    if (newHealth > 20) {
                        newHealth = 20.0;
                    }
                    playerDamager.setHealth(newHealth);
                }
            }

        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (this.api.getPlayerCanUseAbility(player, 44, true)) {
                if (player.getEquipment().getItemInMainHand().getType() == Material.SUGAR_CANE) {
                    int amount = player.getEquipment().getItemInMainHand().getAmount();
                    if ((amount - 1) < 0) {
                        amount = 1;
                    }
                    player.getEquipment().getItemInMainHand().setAmount(amount - 1);
                    player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, (VAMP_FIRE_RES * 20), 3));
                    this.api.setPlayerCooldown(player, 44, 1);
                } else if (player.getEquipment().getItemInOffHand().getType() == Material.SUGAR_CANE) {
                    int amount = player.getEquipment().getItemInOffHand().getAmount();
                    if ((amount - 1) < 0) {
                        amount = 1;
                    }
                    player.getEquipment().getItemInOffHand().setAmount(amount - 1);
                    player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, (VAMP_FIRE_RES * 20), 3));
                    this.api.setPlayerCooldown(player, 44, 1);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.api.getPlayerCanUseAbility(player, 44, true)) {
            Location playerLoc = player.getLocation();
            if (playerLoc.getBlock().getLightFromSky() >= 15 && (event.getTo().getWorld().getTime() < 12750 || event.getTo().getWorld().getTime() > 23000)) {
                player.setFireTicks(21);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerKill (EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        EntityType killedType = event.getEntityType();

        if (killer != null && killedType == EntityType.PLAYER) {
            if (this.api.getPlayerCanUseAbility(killer, 43, true)) {
                if (!vampDict.containsKey(killer)) {
                    vampDict.put(killer, VAMP_START_LS);
                }
                double currLifeSteal = vampDict.get(killer) + VAMP_PLAYER_KILL_LS;
                boolean underCap = currLifeSteal < VAMP_LS_CAP;
                if (!underCap) {
                    currLifeSteal = VAMP_LS_CAP;
                }
                vampDict.put(killer, currLifeSteal);
                if (underCap) {
                    killer.sendMessage(ChatColor.LIGHT_PURPLE + "You now have " + currLifeSteal + "% life steal.");
                } else {
                    killer.sendMessage(ChatColor.LIGHT_PURPLE + "You are capped at " + VAMP_LS_CAP + "% life steal.");
                }
            }
        }
        else if (killer != null) {
            if (this.api.getPlayerCanUseAbility(killer, 43, true)) {
                if (!vampDict.containsKey(killer)) {
                    vampDict.put(killer, VAMP_START_LS);
                }
                double currLifeSteal = vampDict.get(killer) + VAMP_MOB_KILL_LS;
                boolean underCap = currLifeSteal < VAMP_LS_CAP;
                if (!underCap) {
                    currLifeSteal = VAMP_LS_CAP;
                }
                vampDict.put(killer, currLifeSteal);
                if (underCap) {
                    killer.sendMessage(ChatColor.LIGHT_PURPLE + "You now have " + currLifeSteal + "% life steal.");
                } else {
                    killer.sendMessage(ChatColor.LIGHT_PURPLE + "You are capped at " + VAMP_LS_CAP + "% life steal.");
                }
            }
        }
    }
}
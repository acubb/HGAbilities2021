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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Pudge implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();
    Hashtable<Player, Integer> pudgeDict = new Hashtable<>();

    int PUDGE_REGEN = Integer.parseInt(configFile.getString("PUDGE_REGEN"));
    int PUDGE_MEAT_FOR_LVL = Integer.parseInt(configFile.getString("PUDGE_MEAT_FOR_LVL"));
    int PUDGE_CAP = Integer.parseInt(configFile.getString("PUDGE_CAP"));

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (this.api.getPlayerCanUseAbility(player, 33, true)) {
                if (player.getEquipment().getItemInOffHand().getType() == Material.COOKIE) {
                    int amount = player.getEquipment().getItemInOffHand().getAmount() - 1;
                    if (amount < 0) {
                        amount = 0;
                    }
                    player.getEquipment().setItemInOffHand(new ItemStack(Material.COOKIE, amount));
                    player.removePotionEffect(PotionEffectType.ABSORPTION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 0));
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (PUDGE_REGEN * 20), 0));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0F, 1.0F);
                    this.api.setPlayerCooldown(player, 33, 1);
                } else if (player.getEquipment().getItemInMainHand().getType() == Material.COOKIE) {
                    int amount = player.getEquipment().getItemInMainHand().getAmount() - 1;
                    if (amount < 0) {
                        amount = 0;
                    }
                    player.getEquipment().setItemInMainHand(new ItemStack(Material.COOKIE, amount));
                    player.removePotionEffect(PotionEffectType.ABSORPTION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 0));
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (PUDGE_REGEN * 20), 0));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0F, 1.0F);
                    this.api.setPlayerCooldown(player, 33, 1);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.api.getPlayerCanUseAbility(player, 33, true)) {
            if (player.getEquipment().getItemInOffHand().getType() == Material.COOKIE) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 0));
            }
            else {
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.removePotionEffect(PotionEffectType.SLOW);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerConsume (PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (this.api.getPlayerCanUseAbility(player, 35, true)) {
            if (item.getType() == Material.GRILLED_PORK || item.getType() == Material.COOKED_BEEF ||
                    item.getType() == Material.COOKED_CHICKEN || item.getType() == Material.COOKED_MUTTON ||
                    item.getType() == Material.COOKED_FISH || item.getType() == Material.COOKED_RABBIT) {
                if (!pudgeDict.containsKey(player)) {
                    pudgeDict.put(player, 0);
                }
                int currentMeat = pudgeDict.get(player) + 1;
                pudgeDict.put(player, currentMeat);
                int level = (currentMeat / PUDGE_MEAT_FOR_LVL) - 1;
                if (currentMeat % PUDGE_MEAT_FOR_LVL == 0 && level < PUDGE_CAP) {
                    double currHealth = player.getHealth();
                    player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 10000000, level));
                    player.setHealth(currHealth);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0F, 1.0F);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "You've eaten " + currentMeat + " meat giving you " + ChatColor.YELLOW + ((level + 1) * 2) +
                            ChatColor.LIGHT_PURPLE + " extra hearts.");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath (EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();

        if (event.getEntity() != null && event.getEntity().getKiller() != null) {
            if (this.api.getPlayerCanUseAbility(player, 65, true)) {
                if (event.getEntityType() == EntityType.ENDERMAN) {
                    event.getDrops().add(new ItemStack(Material.COOKIE, 1));
                }
            }
        }
    }
}
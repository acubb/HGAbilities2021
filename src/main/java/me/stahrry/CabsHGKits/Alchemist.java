package me.stahrry.CabsHGKits;

import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class Alchemist implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();
    HGSupport hgSupport = new HGSupport();
    Hashtable<Player, Queue<PotionEffectType>> alchemistDict = new Hashtable<>();
    Hashtable<Player, Integer> alchemistCounter = new Hashtable<>();

    int ALCHEMIST_POTION_DUR = Integer.parseInt(configFile.getString("ALCHEMIST_POTION_DUR"));
    int ALCHEMIST_MAX_EFFECTS = Integer.parseInt(configFile.getString("ALCHEMIST_MAX_EFFECTS"));

    public PotionEffectType getNextPotionEffectType (Player player, ArrayList<PotionEffectType> potionEffectTypes) {
        if (!alchemistCounter.containsKey(player)) {
            alchemistCounter.put(player, -1);
        }
        int counter = alchemistCounter.get(player) + 1;
        if (counter < 0 || counter > 8) {
            counter = 0;
        }
        alchemistCounter.put(player, counter);
        return (potionEffectTypes.get(counter));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerConsume (PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (this.api.getPlayerCanUseAbility(player, 32, true)) {
            if (item.getType() == Material.POTION) {
                PotionType potionType = hgSupport.getPotionType(item);
                if (potionType == PotionType.WATER) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (this.api.getPlayerCanUseAbility(player, 32, true)) {
                ItemStack item = player.getEquipment().getItemInMainHand();
                if (item.getType() == Material.POTION) {
                    if (player.getEquipment().getItemInOffHand().getType() == Material.BLAZE_POWDER) {
                        PotionType potionType = hgSupport.getPotionType(item);
                        if (potionType == PotionType.WATER) {
                            int duration = (ALCHEMIST_POTION_DUR * 20);
                            int strengthIndex = 0;
                            PotionEffectType effectType = getNextPotionEffectType(player, hgSupport.getPotionEffectTypes());

                            if (!alchemistDict.containsKey(player)) {
                                Queue<PotionEffectType> effectQueue = new ArrayDeque<>();
                                alchemistDict.put(player, effectQueue);
                            }
                            Queue<PotionEffectType> alchQueue = alchemistDict.get(player);
                            if (alchQueue.size() > 0) {
                                PotionEffectType queueBack = (PotionEffectType) alchQueue.toArray()[alchQueue.size() - 1];
                                if (queueBack != effectType) {
                                    alchQueue.add(effectType);
                                }
                            } else {
                                alchQueue.add(effectType);
                            }
                            player.removePotionEffect(effectType);
                            player.addPotionEffect(new PotionEffect(effectType, duration, strengthIndex, false, false));
                            if (alchQueue.size() > ALCHEMIST_MAX_EFFECTS) {
                                PotionEffectType toRemove = alchQueue.remove();
                                if (!alchQueue.contains(toRemove)) {
                                    player.removePotionEffect(toRemove);
                                }
                            }
                            while (alchQueue.size() > player.getActivePotionEffects().size()) {
                                alchQueue.remove();
                            }
                            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1.0F, 1.0F);
                            String filterString = hgSupport.filterEffectTypes(effectType);
                            player.sendMessage(ChatColor.YELLOW + filterString);
                        }
                    }
                }
            }
        }
    }
}
package me.stahrry.CabsHGKits;

import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Dryad implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();
    HGSupport hgSupport = new HGSupport();
    World world = Bukkit.getWorld("world");

    int DRYAD_REGEN = Integer.parseInt(configFile.getString("DRYAD_REGEN"));

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (this.api.getPlayerCanUseAbility(player, 28, true)) {
                ItemStack mainHandItem = player.getEquipment().getItemInMainHand();
                ItemStack offHandItem = player.getEquipment().getItemInOffHand();
                boolean mHHoldingSapling = hgSupport.holdingSapling(player, 0);
                boolean oHHoldingSapling = hgSupport.holdingSapling(player, 1);
                if (mHHoldingSapling) {
                    int amount = mainHandItem.getAmount() - 1;
                    if (amount < 0) {
                        amount = 0;
                    }
                    ItemStack newSapling = hgSupport.getNewSapling(mainHandItem, amount);
                    player.getEquipment().setItemInMainHand(newSapling);
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (DRYAD_REGEN * 20), 1));
                    this.api.setPlayerCooldown(player, 28, 1);
                } else if (oHHoldingSapling) {
                    int amount = offHandItem.getAmount() - 1;
                    if (amount < 0) {
                        amount = 0;
                    }
                    ItemStack newSapling = hgSupport.getNewSapling(offHandItem, amount);
                    player.getEquipment().setItemInOffHand(newSapling);
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (DRYAD_REGEN * 20), 1));
                    this.api.setPlayerCooldown(player, 28, 1);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.api.getPlayerCanUseAbility(player, 28, true)) {
            if (player.getEquipment().getItemInOffHand().getType() == Material.SEEDS) {
                int seedAmount = player.getEquipment().getItemInOffHand().getAmount();
                if (seedAmount >= 16 && seedAmount < 32) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0));
                }
                else if (seedAmount >= 32 && seedAmount < 64) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1));
                }
                else if (seedAmount == 64) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2));
                }
                else if (seedAmount < 16) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                }
            }
            else {
                player.removePotionEffect(PotionEffectType.SPEED);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak (BlockBreakEvent event) {
        Player breaker = event.getPlayer();
        Block block = event.getBlock();
        if (this.api.getPlayerCanUseAbility(breaker, 67, true)) {
            if (hgSupport.wieldingSwordOrAxe(breaker, 0)) {
                if (block.getType() == Material.LOG) {
                    int currY = (world.getHighestBlockYAt(breaker.getLocation())) - 30;
                    int maxHeight = currY + 60;

                    while (currY < maxHeight) {
                        for (int x = block.getX() - 3; x < block.getX() + 4; x++) {
                            for (int z = block.getZ() - 3; z < block.getZ() + 4; z++) {
                                Block currBlock = world.getBlockAt(x, currY, z);
                                if (currBlock.getType() == Material.LOG) {
                                    currBlock.breakNaturally();
                                    world.playSound(currBlock.getLocation(), Sound.BLOCK_WOOD_BREAK, 1.0F, 1.0F);
                                }
                            }
                        }
                        currY++;
                    }
                }
            }
        }
    }
}
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

public class Hunter implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();

    int HUNTER_WOLF_MEAT = Integer.parseInt(configFile.getString("HUNTER_WOLF_MEAT"));

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (this.api.getPlayerCanUseAbility(player, 40, true)) {
                if (player.getEquipment().getItemInOffHand().getType() == Material.BONE) {
                    ItemStack item = player.getEquipment().getItemInMainHand();
                    if (item.getType() == Material.GRILLED_PORK || item.getType() == Material.COOKED_BEEF ||
                            item.getType() == Material.COOKED_CHICKEN || item.getType() == Material.COOKED_MUTTON ||
                            item.getType() == Material.COOKED_RABBIT ||
                            item.getType() == Material.RABBIT ||
                            item.getType() == Material.MUTTON || item.getType() == Material.RAW_CHICKEN ||
                            item.getType() == Material.RAW_BEEF || item.getType() == Material.PORK) {
                        int amount = item.getAmount();
                        int spawnAmount = HUNTER_WOLF_MEAT;
                        Material material = item.getType();
                        boolean spawnEgg = false;
                        if (amount == spawnAmount) {
                            player.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
                            spawnEgg = true;
                        } else if (amount > spawnAmount) {
                            player.getEquipment().setItemInMainHand(new ItemStack(material, amount - spawnAmount));
                            spawnEgg = true;
                        }
                        if (spawnEgg) {
                            player.getInventory().addItem(new ItemStack(Material.MONSTER_EGG, 1, (short) 95));
                            this.api.setPlayerCooldown(player, 40, 1);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entityInteract = event.getRightClicked();

        if (this.api.getPlayerCanUseAbility(player, 39, true)) {
            if (entityInteract.getType() == EntityType.WOLF) {
                Wolf wolfInteract = (Wolf) entityInteract;
                if (wolfInteract.isAdult() && !wolfInteract.isTamed()) {
                    event.setCancelled(true);
                    if (player.getEquipment().getItemInMainHand().getType() == Material.BONE) {
                        wolfInteract.setTamed(true);
                        wolfInteract.setOwner(player);
                        int amount = player.getEquipment().getItemInMainHand().getAmount() - 1;
                        if (amount < 0) {
                            amount = 0;
                        }
                        player.getEquipment().setItemInMainHand(new ItemStack(Material.BONE, amount));
                    } else if (player.getEquipment().getItemInOffHand().getType() == Material.BONE) {
                        wolfInteract.setTamed(true);
                        wolfInteract.setOwner(player);
                        int amount = player.getEquipment().getItemInOffHand().getAmount() - 1;
                        if (amount < 0) {
                            amount = 0;
                        }
                        player.getEquipment().setItemInOffHand(new ItemStack(Material.BONE, amount));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath (EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();

        if (event.getEntity() != null && event.getEntity().getKiller() != null) {
            if (this.api.getPlayerCanUseAbility(player, 39, true)) {
                if (event.getEntityType() == EntityType.SKELETON) {
                    event.getDrops().clear();
                    event.getDrops().add(new ItemStack(Material.BONE, 1));
                }
            }
        }
    }
}

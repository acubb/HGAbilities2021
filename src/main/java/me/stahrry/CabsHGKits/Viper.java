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
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Viper implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();

    int VIPER_POISON_CHANCE = Integer.parseInt(configFile.getString("VIPER_POISON_CHANCE"));
    int VIPER_POISON_DUR = Integer.parseInt(configFile.getString("VIPER_POISON_DUR"));

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit (ProjectileHitEvent event) {
        Projectile proj = event.getEntity();
        if (proj instanceof Arrow) {
            Arrow arrow = (Arrow) proj;
            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();

                if (this.api.getPlayerCanUseAbility(shooter, 26, true)) {
                    Entity entityHit = event.getHitEntity();
                    if (entityHit instanceof Player) {
                        Random rand = new Random();
                        int randInt = rand.nextInt(100) + 1;
                        if (randInt <= VIPER_POISON_CHANCE) {
                            List<Entity> nearbyEntities = entityHit.getNearbyEntities(2.0D, 2.0D, 2.0D);
                            for (Entity target : nearbyEntities) {
                                if (target instanceof Player) {
                                    Player playerTarget = (Player) target;
                                    if (this.api.getPlayerIsSpectator(playerTarget))
                                        continue;
                                    if (playerTarget.getName().equals(shooter.getName()))
                                        continue;
                                    playerTarget.removePotionEffect(PotionEffectType.POISON);
                                    playerTarget.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (VIPER_POISON_DUR * 20), 0));
                                }
                            }
                            Player playerHit = (Player) entityHit;
                            playerHit.removePotionEffect(PotionEffectType.POISON);
                            playerHit.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (VIPER_POISON_DUR * 20), 0));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath (EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();

        if (event.getEntity() != null && event.getEntity().getKiller() != null) {
            if (this.api.getPlayerCanUseAbility(player, 42, true)) {
                if (event.getEntityType() == EntityType.CHICKEN) {
                    event.getDrops().clear();
                    event.getDrops().add(new ItemStack(Material.FEATHER, 2));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak (BlockBreakEvent event) {
        Player breaker = event.getPlayer();
        Block block = event.getBlock();

        if (this.api.getPlayerCanUseAbility(breaker, 42, true)) {
            if (block.getType() == Material.GRAVEL) {
                event.setDropItems(false);
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.FLINT, 1));
            }
        }
    }
}
package me.stahrry.CabsHGKits;

import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Stomper implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage (EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (this.api.getPlayerCanUseAbility(player, 56, true) &&
                    event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                double damage = event.getDamage();

                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                List<Entity> nearbyEntities = event.getEntity().getNearbyEntities(3, 3, 3);
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof Player) {
                        Player playerTarget = (Player) entity;
                        if (this.api.getPlayerIsSpectator(playerTarget))
                            continue;
                        if (playerTarget.getName().equals(player.getName()))
                            continue;
                        if (playerTarget.isSneaking()) {
                            playerTarget.damage(event.getDamage() / 2.0D);
                        } else {
                            playerTarget.damage(damage);
                        }
                    } else {
                        if (!entity.isDead()) {
                            LivingEntity livingEntity = (LivingEntity) entity;
                            livingEntity.damage(damage);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == Action.LEFT_CLICK_BLOCK) {
            if (this.api.getPlayerCanUseAbility(player, 41, true)) {
                if (event.getClickedBlock().getType() == Material.DIRT || event.getClickedBlock().getType() == Material.GRASS) {
                    event.getClickedBlock().setType(Material.AIR);
                    Location blockLoc = event.getClickedBlock().getLocation();
                    blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.DIRT, 1));
                }
            }
        }
    }
}

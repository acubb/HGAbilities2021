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

public class Pyro implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();

    int PYRO_FIRE_DUR = Integer.parseInt(configFile.getString("PYRO_FIRE_DUR"));

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (this.api.getPlayerCanUseAbility(player, 57, true)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1000000, 2));
            if (player.getLocation().getBlock().getType() == Material.LAVA || player.getLocation().getBlock().getType() == Material.STATIONARY_LAVA ||
                    player.getLocation().getBlock().getType() == Material.FIRE) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
            } else {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit (ProjectileHitEvent event) {
        Projectile proj = event.getEntity();

        if (proj instanceof Arrow) {
            Arrow arrow = (Arrow) proj;
            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();

                if (this.api.getPlayerCanUseAbility(shooter, 57, true)) {
                    Entity entityHit = event.getHitEntity();
                    if (entityHit != null) {
                        List<Entity> nearbyEntities = entityHit.getNearbyEntities(2.0D, 2.0D, 2.0D);
                        for (Entity target : nearbyEntities) {
                            if (target instanceof Player) {
                                Player playerTarget = (Player) target;
                                if (this.api.getPlayerIsSpectator(playerTarget))
                                    continue;
                                if (playerTarget.getName().equals(shooter.getName()))
                                    continue;
                                playerTarget.setFireTicks(PYRO_FIRE_DUR * 20);
                            } else if (!target.equals(entityHit)) {
                                target.setFireTicks(PYRO_FIRE_DUR * 20);
                            }
                        }
                        entityHit.setFireTicks(PYRO_FIRE_DUR * 20);
                    }
                }
            }
        }
    }
}

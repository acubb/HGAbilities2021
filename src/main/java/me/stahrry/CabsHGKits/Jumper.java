package me.stahrry.CabsHGKits;

import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class Jumper implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();

    int JUMPER_WEAK_DUR = Integer.parseInt(configFile.getString("JUMPER_WEAK_DUR"));
    int JUMPER_STR_DUR = Integer.parseInt(configFile.getString("JUMPER_STR_DUR"));

    @EventHandler(priority = EventPriority.NORMAL)
    private void onProjectileLaunch (ProjectileLaunchEvent event) {
        Projectile entity = event.getEntity();
        if (entity instanceof Snowball) {
            Snowball snowball = (Snowball) entity;
            ProjectileSource shooter = snowball.getShooter();
            if (shooter instanceof Player) {
                Player playerShooter = (Player) shooter;
                if (this.api.getPlayerCanUseAbility(playerShooter, 25, true)) {
                    snowball.setVelocity(snowball.getVelocity().multiply(2));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit (ProjectileHitEvent event) {
        Projectile proj = event.getEntity();

        if (proj instanceof Snowball) {
            Snowball snow = (Snowball) proj;
            if (snow.getShooter() instanceof Player) {
                Player shooter = (Player) snow.getShooter();

                if (this.api.getPlayerCanUseAbility(shooter, 25, true)) {
                    Block blockHit = event.getHitBlock();
                    Entity entityHit = event.getHitEntity();
                    Location tpLoc;
                    if (blockHit != null) {
                        tpLoc = blockHit.getLocation().add(0.5, 1, 0.5);
                    } else {
                        tpLoc = entityHit.getLocation();
                        Location shooterLoc = shooter.getLocation().add(0.5, 1, 0.5);
                        entityHit.teleport(shooterLoc);
                        if (entityHit.getType() == EntityType.PLAYER) {
                            Player playerHit = (Player) entityHit;
                            shooter.removePotionEffect(PotionEffectType.WEAKNESS);
                            playerHit.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, (JUMPER_WEAK_DUR * 20), 0));
                        }
                    }
                    if (tpLoc != null) {
                        shooter.teleport(tpLoc);
                        shooter.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                        shooter.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (JUMPER_STR_DUR * 20), 0));
                    }
                }
            }
        }
    }
}

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

public class Icarus implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();

    int ICARUS_REGEN = Integer.parseInt(configFile.getString("ICARUS_REGEN"));
    int ICARUS_BONUS_DMG = Integer.parseInt(configFile.getString("ICARUS_BONUS_DMG"));

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if ((this.api.getPlayerCanUseAbility(player, 27, true)) &&
                    (player.getEquipment().getItemInMainHand().getType() == Material.FIREWORK) &&
                    player.isGliding()) {
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (ICARUS_REGEN * 20), 0));
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

                if (this.api.getPlayerCanUseAbility(shooter, 34, true)) {
                    Entity entityHit = event.getHitEntity();
                    if (entityHit instanceof Player && (shooter.isGliding() || shooter.isFlying())) {
                        Player playerEntity = (Player)entityHit;
                        double newHealth = playerEntity.getHealth() - ICARUS_BONUS_DMG;
                        if (newHealth < 0) {
                            newHealth = 0;
                        }
                        playerEntity.setHealth(newHealth);
                        shooter.sendMessage(ChatColor.LIGHT_PURPLE + "Extra damage applied!");
                    }
                }
            }
        }
    }
}
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

public class Assassin implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();
    HGSupport hgSupport = new HGSupport();

    int ASSASSIN_MAX_FALL = Integer.parseInt(configFile.getString("ASSASSIN_MAX_FALL"));
    double ASSASSIN_DMG_TAKEN = Double.parseDouble(configFile.getString("ASSASSIN_DMG_TAKEN"));
    double ASSASSIN_DMG_GIVEN = Double.parseDouble(configFile.getString("ASSASSIN_DMG_GIVEN"));

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerSneakToggle (PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (this.api.getPlayerCanUseAbility(player, 36, true)) {
            if (event.isSneaking()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 10, false, false));
            }
            else {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.api.getPlayerCanUseAbility(player, 37, true)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0, false, false));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage (EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (this.api.getPlayerCanUseAbility(player, 37, true)) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    if (event.getDamage() > ASSASSIN_MAX_FALL) {
                        event.setDamage(ASSASSIN_MAX_FALL);
                    }
                }
            }

            if (this.api.getPlayerCanUseAbility(player, 38, true)) {
                if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
                    double dmgNumber = event.getDamage();
                    double newDmg = dmgNumber * ASSASSIN_DMG_TAKEN;
                    event.setDamage(newDmg);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity (EntityDamageByEntityEvent event) {
        Entity entityDamager = event.getDamager();
        Entity entityDefender = event.getEntity();

        if (entityDamager instanceof Player) {
            Player playerDamager = (Player) entityDamager;

            if (entityDefender.getType() == EntityType.PLAYER) {
                Player playerDefender = (Player) entityDefender;

                if (this.api.getPlayerCanUseAbility(playerDamager, 38, true)) {
                    boolean wieldingSwordOrAxe = hgSupport.wieldingSwordOrAxe(playerDamager, 0);
                    if (wieldingSwordOrAxe) {
                        boolean behindPlayer = hgSupport.isBehind(playerDamager, playerDefender);
                        if (behindPlayer) {
                            double dmg = event.getDamage();
                            double newDmg = dmg * ASSASSIN_DMG_GIVEN;
                            event.setDamage(newDmg);
                            playerDamager.playSound(playerDamager.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }
    }
}
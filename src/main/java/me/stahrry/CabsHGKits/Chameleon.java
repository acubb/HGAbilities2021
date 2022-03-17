package me.stahrry.CabsHGKits;

import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
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

public class Chameleon implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();

    int CHAMELEON_STR_DUR = Integer.parseInt(configFile.getString("CHAMELEON_STR_DUR"));

    @EventHandler
    public void onPlayerInteractEntity (PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (this.api.getPlayerCanUseAbility(player, 30, true)) {
            if (!DisguiseAPI.isDisguised(player)) {
                Entity entity = event.getRightClicked();
                EntityType entityType = entity.getType();

                Disguise entityDisguise = DisguiseAPI.constructDisguise(entity);
                DisguiseAPI.disguiseToAll(player, entityDisguise);
                DisguiseAPI.setViewDisguiseToggled(player, true);

                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100000, 0, false, false));
                player.sendMessage(ChatColor.LIGHT_PURPLE + "You're now disguised as a(n) " + entityType.toString().toLowerCase() + ".");
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage (EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (this.api.getPlayerCanUseAbility(player, 30, true)) {
                if (DisguiseAPI.isDisguised(player)) {
                    DisguiseAPI.undisguiseToAll(player);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Your disguise is gone. You feel empowered!");
                    player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (CHAMELEON_STR_DUR * 20), 1, false, false));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (this.api.getPlayerCanUseAbility(player, 30, true)) {
                if (DisguiseAPI.isDisguised(player)) {
                    DisguiseAPI.undisguiseToAll(player);
                    player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Your disguise is gone.");
                }
            }
        }
    }
}
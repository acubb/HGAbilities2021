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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class Sapper implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();
    HGSupport hgSupport = new HGSupport();
    World world = Bukkit.getWorld("world");
    Hashtable<TNTPrimed, Player> tntSourceDict = new Hashtable<>();
    ArrayList<String> softExplosions = new ArrayList<>();

    int SAPPER_OH_BOMB_FUSE = Integer.parseInt(configFile.getString("SAPPER_OH_BOMB_FUSE"));
    int SAPPER_POISON_CHANCE = Integer.parseInt(configFile.getString("SAPPER_POISON_CHANCE"));
    int SAPPER_FIRE_CHANCE = Integer.parseInt(configFile.getString("SAPPER_FIRE_CHANCE"));
    double SAPPER_MELEE_DMG_NERF = Double.parseDouble(configFile.getString("SAPPER_MELEE_DMG_NERF"));
    int SAPPER_DIRTY_BOMB_DMG_NERF = Integer.parseInt(configFile.getString("SAPPER_DIRTY_BOMB_DMG_NERF"));
    int SAPPER_MAGNITUDE_X_DENOM = Integer.parseInt(configFile.getString("SAPPER_MAGNITUDE_X_DENOM"));
    int SAPPER_MAGNITUDE_Y_DENOM = Integer.parseInt(configFile.getString("SAPPER_MAGNITUDE_Y_DENOM"));
    int SAPPER_MAGNITUDE_Z_DENOM = Integer.parseInt(configFile.getString("SAPPER_MAGNITUDE_Z_DENOM"));

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplosion (EntityExplodeEvent event) {
        Entity explo = event.getEntity();
        Location exploLoc = explo.getLocation();
        String exploID = explo.getUniqueId().toString();

        if (softExplosions.contains(exploID)) {
            event.setCancelled(true);
            world.createExplosion(exploLoc.getX(), exploLoc.getY(), exploLoc.getZ(), 0, false, false);
            softExplosions.remove(exploID);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity (EntityDamageByEntityEvent event) {
        Entity entityDamager = event.getDamager();
        Entity entityDefender = event.getEntity();

        if (entityDamager instanceof Player) {
            Player playerDamager = (Player) entityDamager;

            if (entityDefender.getType() == EntityType.PLAYER) {
                if (this.api.getPlayerCanUseAbility(playerDamager, 62, true)) {
                    EntityDamageEvent.DamageCause cause = event.getCause();
                    if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                        double damage = event.getDamage();
                        double newDamage = damage * SAPPER_MELEE_DMG_NERF;
                        event.setDamage(newDamage);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak (BlockBreakEvent event) {
        Player breaker = event.getPlayer();
        Block block = event.getBlock();

        if (this.api.getPlayerCanUseAbility(breaker, 63, true)) {
            if (block.getType() == Material.SAND) {
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SULPHUR, 1));
            }
            if (block.getType() == Material.STONE) {
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.REDSTONE, 2));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace (BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();

        if (this.api.getPlayerCanUseAbility(player, 64, true)) {
            if (block.getType() == Material.TNT) {
                Location tntLoc = block.getLocation();

                if (hgSupport.wieldingSwordOrAxe(player, 0)) {
                    block.setType(Material.AIR);
                    Location newTntLoc = tntLoc.add(0.5, 0, 0.5);
                    TNTPrimed tntPrimed = (TNTPrimed) world.spawnEntity(newTntLoc, EntityType.PRIMED_TNT);
                    tntPrimed.setFuseTicks(0);

                    tntSourceDict.put(tntPrimed, player);
                    String tntID = tntPrimed.getUniqueId().toString();
                    softExplosions.add(tntID);
                }
                else if (hgSupport.wieldingSwordOrAxe(player, 1)) {
                    block.setType(Material.AIR);
                    Location newTntLoc = tntLoc.add(0.5, 0, 0.5);
                    TNTPrimed tntPrimed = (TNTPrimed) world.spawnEntity(newTntLoc, EntityType.PRIMED_TNT);
                    tntPrimed.setFuseTicks(SAPPER_OH_BOMB_FUSE * 20);

                    tntSourceDict.put(tntPrimed, player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByExplosion (EntityDamageByEntityEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();

        if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            Entity explo = event.getDamager();
            Entity entity = event.getEntity();

            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (this.api.getPlayerCanUseAbility(player, 62, true)) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (explo.getType() == EntityType.PRIMED_TNT) {
                TNTPrimed tntPrimed = (TNTPrimed) explo;
                Player tntSource = tntSourceDict.get(tntPrimed);

                if (tntSource != null) {
                    if (this.api.getPlayerCanUseAbility(tntSource, 64, true)) {
                        Location exploLoc = explo.getLocation();
                        Location entityLoc = entity.getLocation();

                        if (hgSupport.wieldingSwordOrAxe(tntSource, 0)) {
                            double newDamage = 0;
                            event.setDamage(newDamage);

                            Vector entityVel = new Vector(0, 0, 0);
                            entity.setVelocity(entityVel);
                            double distance = Math.sqrt(Math.pow((entityLoc.getX() - exploLoc.getX()), 2) +
                                    Math.pow(entityLoc.getY() - exploLoc.getY(), 2) +
                                    Math.pow(entityLoc.getZ() - exploLoc.getZ(), 2));
                            double magnitude = -0.000344073 * (Math.pow(distance, 9)) + 0.013778 * (Math.pow(distance, 8)) - 0.233271 * (Math.pow(distance, 7)) +
                                    2.17124 * (Math.pow(distance, 6)) - 12.0942 * (Math.pow(distance, 5)) + 41.1105 * (Math.pow(distance, 4)) - 83.2724 * (Math.pow(distance, 3))
                                    + 94.1832 * (Math.pow(distance, 2)) - 56.8784 * distance + 30;
                            if (magnitude < 0.1) {
                                magnitude = 0.1;
                            }
                            else if (magnitude > 30) {
                                magnitude = 30;
                            }
                            //System.out.println("Distance: " + distance);
                            //System.out.println("Magnitude: " + magnitude);
                            entityVel.setX(entityLoc.getX() - exploLoc.getX());
                            entityVel.setY((entityLoc.getY() + 0.2) - exploLoc.getY());
                            entityVel.setZ(entityLoc.getZ() - exploLoc.getZ());
                            entityVel.setX(entityVel.getX() * (magnitude / SAPPER_MAGNITUDE_X_DENOM));
                            entityVel.setY((entityVel.getY() * magnitude) / SAPPER_MAGNITUDE_Y_DENOM);
                            entityVel.setZ(entityVel.getZ() * (magnitude / SAPPER_MAGNITUDE_Z_DENOM));
                            entity.setVelocity(entityVel);
                            //System.out.println("velocity vector: " + entityVel.toString());
                        }
                        else if (hgSupport.wieldingSwordOrAxe(tntSource, 1)) {
                            double newDamage = event.getDamage() / SAPPER_DIRTY_BOMB_DMG_NERF;
                            event.setDamage(newDamage);

                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                Random rand = new Random();

                                int randInt1 = rand.nextInt(100) + 1; // Fire Chance
                                int randInt2 = rand.nextInt(100) + 1; // Poison Chance
                                if (randInt1 <= SAPPER_FIRE_CHANCE && randInt2 <= SAPPER_POISON_CHANCE) {
                                    player.setFireTicks(60);
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
                                }
                                else if (randInt1 <= SAPPER_FIRE_CHANCE) {
                                    player.setFireTicks(60);
                                }
                                else if (randInt2 <= SAPPER_POISON_CHANCE) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
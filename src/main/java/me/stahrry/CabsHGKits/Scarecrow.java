package me.stahrry.CabsHGKits;

import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Scarecrow implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();
    HGSupport hgSupport = new HGSupport();
    Hashtable<Player, ArrayList<Location>> scareCrowDict = new Hashtable<>();

    int SCARECROW_CD = Integer.parseInt(configFile.getString("SCARECROW_CD"));

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.api.getPlayerCanUseAbility(player, 31, true)) {
            boolean hoeWield = hgSupport.wieldingHoe(player, 1);
            boolean inWheat = player.getLocation().getBlock().getRelative(BlockFace.UP, 1).getType() == Material.WHEAT;
            if (hoeWield && inWheat) {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 0));
            } else if (hoeWield) {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
            } else if (inWheat) {
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 0));
            } else {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (this.api.getPlayerCanUseAbility(player, 66, true)) {
            if (action == Action.RIGHT_CLICK_BLOCK) {
                if (hgSupport.wieldingHoe(player, 0) || hgSupport.wieldingHoe(player, 1)) {
                    Block blockClicked = event.getClickedBlock();
                    BlockFace blockFace = event.getBlockFace();
                    Material blockMaterial = blockClicked.getType();
                    Location blockLoc = blockClicked.getLocation();
                    Material handMaterial = event.getMaterial();
                    if ((blockMaterial == Material.GRASS || blockMaterial == Material.DIRT || blockMaterial == Material.SOIL) &&
                            blockFace == BlockFace.UP) {
                        scareCrowDict.putIfAbsent(player, new ArrayList<>());
                        ArrayList<Location> locationArray = scareCrowDict.get(player);
                        if (locationArray.isEmpty()) {
                            for (int i = 0; i < 5; i++) {
                                locationArray.add(blockLoc);
                            }
                        }
                        Location tpLoc = blockLoc.add(0.5, 1, 0.5);
                        if (handMaterial == Material.WOOD_HOE) {
                            locationArray.set(0, tpLoc);
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "Teleport location" + ChatColor.YELLOW +
                                    " (" + blockLoc.getX() + ", " + blockLoc.getY() + ", " + blockLoc.getZ() + ")" + ChatColor.LIGHT_PURPLE +
                                    " saved in the" + ChatColor.YELLOW + " Wooden " + ChatColor.LIGHT_PURPLE + "Hoe slot.");
                        } else if (handMaterial == Material.STONE_HOE) {
                            locationArray.set(1, tpLoc);
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "Teleport location" + ChatColor.YELLOW +
                                    " (" + blockLoc.getX() + ", " + blockLoc.getY() + ", " + blockLoc.getZ() + ")" + ChatColor.LIGHT_PURPLE +
                                    " saved in the" + ChatColor.YELLOW + " Stone " + ChatColor.LIGHT_PURPLE + "Hoe slot.");
                        } else if (handMaterial == Material.IRON_HOE) {
                            locationArray.set(2, tpLoc);
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "Teleport location" + ChatColor.YELLOW +
                                    " (" + blockLoc.getX() + ", " + blockLoc.getY() + ", " + blockLoc.getZ() + ")" + ChatColor.LIGHT_PURPLE +
                                    " saved in the" + ChatColor.YELLOW + " Iron " + ChatColor.LIGHT_PURPLE + "Hoe slot.");
                        } else if (handMaterial == Material.GOLD_HOE) {
                            locationArray.set(3, tpLoc);
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "Teleport location" + ChatColor.YELLOW +
                                    " (" + blockLoc.getX() + ", " + blockLoc.getY() + ", " + blockLoc.getZ() + ")" + ChatColor.LIGHT_PURPLE +
                                    " saved in the" + ChatColor.YELLOW + " Gold " + ChatColor.LIGHT_PURPLE + "Hoe slot.");
                        } else if (handMaterial == Material.DIAMOND_HOE) {
                            locationArray.set(4, tpLoc);
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "Teleport location" + ChatColor.YELLOW +
                                    " (" + blockLoc.getX() + ", " + blockLoc.getY() + ", " + blockLoc.getZ() + ")" + ChatColor.LIGHT_PURPLE +
                                    " saved in the" + ChatColor.YELLOW + " Diamond " + ChatColor.LIGHT_PURPLE + "Hoe slot.");
                        }
                    }
                }
            } else if (action == Action.RIGHT_CLICK_AIR) {
                if ((hgSupport.wieldingHoe(player, 0) || hgSupport.wieldingHoe(player, 1)) &&
                        !(hgSupport.wieldingHoe(player, 0) && hgSupport.wieldingHoe(player, 1))) {
                    if (scareCrowDict.containsKey(player)) {
                        ArrayList<Location> locationArray = scareCrowDict.get(player);
                        Material handMaterial = event.getMaterial();
                        if (!locationArray.isEmpty()) {
                            Location tpLoc = null;
                            boolean toTp = false;
                            int tpId = 0;
                            if (handMaterial == Material.WOOD_HOE) {
                                tpLoc = locationArray.get(0);
                                toTp = true;
                            } else if (handMaterial == Material.STONE_HOE) {
                                tpLoc = locationArray.get(1);
                                toTp = true;
                                tpId = 1;
                            } else if (handMaterial == Material.IRON_HOE) {
                                tpLoc = locationArray.get(2);
                                toTp = true;
                                tpId = 2;
                            } else if (handMaterial == Material.GOLD_HOE) {
                                tpLoc = locationArray.get(3);
                                toTp = true;
                                tpId = 3;
                            } else if (handMaterial == Material.DIAMOND_HOE) {
                                tpLoc = locationArray.get(4);
                                toTp = true;
                                tpId = 4;
                            }

                            if (toTp) {
                                boolean safeTp = ((tpLoc.getBlock().getRelative(BlockFace.UP, 1).getType() == Material.AIR) &&
                                        (tpLoc.getBlock().getRelative(BlockFace.UP, 2).getType() == Material.AIR));
                                if (safeTp) {
                                    Collection<Entity> nearbyEntities = player.getNearbyEntities(3.0, 3.0, 3.0);
                                    player.teleport(tpLoc);
                                    for (Entity entity : nearbyEntities) {
                                        if (entity instanceof Player) {
                                            if (entity.equals(player)) {
                                                continue;
                                            } else {
                                                Player playerTp = (Player) entity;
                                                playerTp.sendMessage(ChatColor.LIGHT_PURPLE + "You've been teleported by a Scarecrow!");
                                            }
                                        }
                                        entity.teleport(tpLoc);
                                    }
                                    String hoeType = "";
                                    switch (tpId) {
                                        case 0:
                                            hoeType = "Wooden";
                                            break;
                                        case 1:
                                            hoeType = "Stone";
                                            break;
                                        case 2:
                                            hoeType = "Iron";
                                            break;
                                        case 3:
                                            hoeType = "Gold";
                                            break;
                                        case 4:
                                            hoeType = "Diamond";
                                            break;
                                    }
                                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Teleported to " + ChatColor.YELLOW + "(" + tpLoc.getX() + ", " + tpLoc.getY() + ", " + tpLoc.getZ() + ")"
                                            + ChatColor.LIGHT_PURPLE + " saved in your " + ChatColor.YELLOW + hoeType + ChatColor.LIGHT_PURPLE + " Hoe slot.");
                                    player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1.0F, 1.0F);
                                    this.api.setPlayerCooldown(player, 66, SCARECROW_CD);
                                } else {
                                    player.sendMessage(ChatColor.LIGHT_PURPLE + "That saved teleport location is blocked!");
                                }
                            }
                        }
                    }
                } else if (hgSupport.wieldingHoe(player, 0) && hgSupport.wieldingHoe(player, 1)) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Cannot attempt to teleport with a hoe in each hand!");
                }
            }
        }
    }
}
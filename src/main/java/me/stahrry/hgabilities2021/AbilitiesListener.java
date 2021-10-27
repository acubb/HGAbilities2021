package me.stahrry.hgabilities2021;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import de.ftbastler.bukkitgames.api.GameStartEvent;
import de.ftbastler.bukkitgames.enums.FeastState;
import de.ftbastler.bukkitgames.enums.GameState;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AbilitiesListener implements Listener {
    BukkitGamesAPI api = HGAbilities2021.getBgAPI();
    Plugin plugin = HGAbilities2021.getInstance();
    FileConfiguration configFile = HGAbilities2021.getConfigFile();

    Hashtable<Player, Queue<PotionEffectType>> alchemistDict = new Hashtable<>();
    Hashtable<Player, Integer> alchemistCounter = new Hashtable<>();
    Hashtable<Player, Integer> pudgeDict = new Hashtable<>();
    Hashtable<Player, Double> vampDict = new Hashtable<>();
    Hashtable<Player, Boolean> poseidonDict = new Hashtable<>();
    Hashtable<TNTPrimed, Player> tntSourceDict = new Hashtable<>();
    Hashtable<Player, ArrayList<Location>> scareCrowDict = new Hashtable<>();
    Hashtable<Player, Double> shadowDict = new Hashtable<>();
    ArrayList<String> softExplosions = new ArrayList<>();
    Hashtable<Integer, ArrayList<ItemStack>> trawlerItems = new Hashtable<>();

    World world = Bukkit.getWorld("world");

    boolean feastEmpty = true;
    boolean trawlerItemsSet = false;

    boolean soupsEnabled = Boolean.parseBoolean(configFile.getString("SOUPS_ENABLED"));
    int soupHealAmount = Integer.parseInt(configFile.getString("SOUP_HEAL_AMOUNT"));
    double strBonus = Double.parseDouble(configFile.getString("STR_BONUS"));
    double regenAmount = Double.parseDouble(configFile.getString("REGEN_AMOUNT"));
    int feastItemsPerChest = Integer.parseInt(configFile.getString("FEAST_ITEMS_PER_CHEST"));

    int ALCHEMIST_POTION_DUR = Integer.parseInt(configFile.getString("ALCHEMIST_POTION_DUR"));
    int ALCHEMIST_MAX_EFFECTS = Integer.parseInt(configFile.getString("ALCHEMIST_MAX_EFFECTS"));
    int ASSASSIN_MAX_FALL = Integer.parseInt(configFile.getString("ASSASSIN_MAX_FALL"));
    double ASSASSIN_DMG_TAKEN = Double.parseDouble(configFile.getString("ASSASSIN_DMG_TAKEN"));
    double ASSASSIN_DMG_GIVEN = Double.parseDouble(configFile.getString("ASSASSIN_DMG_GIVEN"));
    int BUTCHER_DROP_AMOUNT = Integer.parseInt(configFile.getString("BUTCHER_DROP_AMOUNT"));
    int CHAMELEON_STR_DUR = Integer.parseInt(configFile.getString("CHAMELEON_STR_DUR"));
    int DRYAD_REGEN = Integer.parseInt(configFile.getString("DRYAD_REGEN"));
    int HUNTER_WOLF_MEAT = Integer.parseInt(configFile.getString("HUNTER_WOLF_MEAT"));
    int ICARUS_REGEN = Integer.parseInt(configFile.getString("ICARUS_REGEN"));
    int ICARUS_BONUS_DMG = Integer.parseInt(configFile.getString("ICARUS_BONUS_DMG"));
    int JUMPER_WEAK_DUR = Integer.parseInt(configFile.getString("JUMPER_WEAK_DUR"));
    int JUMPER_STR_DUR = Integer.parseInt(configFile.getString("JUMPER_STR_DUR"));
    int MINER_IRON_ORE_CHANCE = Integer.parseInt(configFile.getString("MINER_IRON_ORE_CHANCE"));
    double POSEIDON_LS = Double.parseDouble(configFile.getString("POSEIDON_LS"));
    int POSEIDON_REGEN = Integer.parseInt(configFile.getString("POSEIDON_REGEN"));
    int PUDGE_REGEN = Integer.parseInt(configFile.getString("PUDGE_REGEN"));
    int PUDGE_MEAT_FOR_LVL = Integer.parseInt(configFile.getString("PUDGE_MEAT_FOR_LVL"));
    int PUDGE_CAP = Integer.parseInt(configFile.getString("PUDGE_CAP"));
    int PYRO_FIRE_DUR = Integer.parseInt(configFile.getString("PYRO_FIRE_DUR"));
    int SAPPER_OH_BOMB_FUSE = Integer.parseInt(configFile.getString("SAPPER_OH_BOMB_FUSE"));
    int SAPPER_POISON_CHANCE = Integer.parseInt(configFile.getString("SAPPER_POISON_CHANCE"));
    int SAPPER_FIRE_CHANCE = Integer.parseInt(configFile.getString("SAPPER_FIRE_CHANCE"));
    double SAPPER_MELEE_DMG_NERF = Double.parseDouble(configFile.getString("SAPPER_MELEE_DMG_NERF"));
    int SAPPER_DIRTY_BOMB_DMG_NERF = Integer.parseInt(configFile.getString("SAPPER_DIRTY_BOMB_DMG_NERF"));
    int SAPPER_MAGNITUDE_X_DENOM = Integer.parseInt(configFile.getString("SAPPER_MAGNITUDE_X_DENOM"));
    int SAPPER_MAGNITUDE_Y_DENOM = Integer.parseInt(configFile.getString("SAPPER_MAGNITUDE_Y_DENOM"));
    int SAPPER_MAGNITUDE_Z_DENOM = Integer.parseInt(configFile.getString("SAPPER_MAGNITUDE_Z_DENOM"));
    int SCARECROW_CD = Integer.parseInt(configFile.getString("SCARECROW_CD"));
    double SHADOW_DMG_CAP = Double.parseDouble(configFile.getString("SHADOW_DMG_CAP"));
    double SHADOW_LIGHT_DMG = Double.parseDouble(configFile.getString("SHADOW_LIGHT_DMG"));
    double SHADOW_DARK_DMG = Double.parseDouble(configFile.getString("SHADOW_DARK_DMG"));
    int TRAWLER_RAREST = Integer.parseInt(configFile.getString("TRAWLER_RAREST"));
    int TRAWLER_RARE = Integer.parseInt(configFile.getString("TRAWLER_RARE"));
    int TRAWLER_SEMI_RARE = Integer.parseInt(configFile.getString("TRAWLER_SEMI_RARE"));
    int TRAWLER_NORMAL = Integer.parseInt(configFile.getString("TRAWLER_NORMAL"));
    int TRAWLER_FIRE = Integer.parseInt(configFile.getString("TRAWLER_FIRE"));
    int TRAWLER_POISON = Integer.parseInt(configFile.getString("TRAWLER_POISON"));
    int VAMPIRE_FIRE_RES = Integer.parseInt(configFile.getString("VAMPIRE_FIRE_RES"));
    double VAMP_START_LS = Double.parseDouble(configFile.getString("VAMP_START_LS"));
    double VAMP_PLAYER_KILL_LS = Double.parseDouble(configFile.getString("VAMP_PLAYER_KILL_LS"));
    double VAMP_MOB_KILL_LS = Double.parseDouble(configFile.getString("VAMP_MOB_KILL_LS"));
    double VAMP_LS_CAP = Double.parseDouble(configFile.getString("VAMP_LS_CAP"));
    int VIPER_POISON_CHANCE = Integer.parseInt(configFile.getString("VIPER_POISON_CHANCE"));
    int VIPER_POISON_DUR = Integer.parseInt(configFile.getString("VIPER_POISON_DUR"));

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameStart (GameStartEvent event) {
        if (!trawlerItemsSet) {
            setTrawlerItems();
            trawlerItemsSet = true;
        }

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending (PacketEvent event) {
                PacketContainer packet = event.getPacket();
                String packetString = packet.getChatComponents().read(0).toString();
                if (packetString.contains("Cooldown")) {
                    event.setCancelled(true);
                }
            }});

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (this.api.getPlayerCanUseAbility(player, 66, true)) {
            if (action == Action.RIGHT_CLICK_BLOCK) {
                if (wieldingHoe(player, 0) || wieldingHoe(player, 1)) {
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
                                    " saved in the" + ChatColor.YELLOW +  " Wooden " + ChatColor.LIGHT_PURPLE + "Hoe slot.");
                        }
                        else if (handMaterial == Material.STONE_HOE) {
                            locationArray.set(1, tpLoc);
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "Teleport location" + ChatColor.YELLOW +
                                    " (" + blockLoc.getX() + ", " + blockLoc.getY() + ", " + blockLoc.getZ() + ")" + ChatColor.LIGHT_PURPLE +
                                    " saved in the" + ChatColor.YELLOW +  " Stone " + ChatColor.LIGHT_PURPLE + "Hoe slot.");
                        }
                        else if (handMaterial == Material.IRON_HOE) {
                            locationArray.set(2, tpLoc);
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "Teleport location" + ChatColor.YELLOW +
                                    " (" + blockLoc.getX() + ", " + blockLoc.getY() + ", " + blockLoc.getZ() + ")" + ChatColor.LIGHT_PURPLE +
                                    " saved in the" + ChatColor.YELLOW +  " Iron " + ChatColor.LIGHT_PURPLE + "Hoe slot.");
                        }
                        else if (handMaterial == Material.GOLD_HOE) {
                            locationArray.set(3, tpLoc);
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "Teleport location" + ChatColor.YELLOW +
                                    " (" + blockLoc.getX() + ", " + blockLoc.getY() + ", " + blockLoc.getZ() + ")" + ChatColor.LIGHT_PURPLE +
                                    " saved in the" + ChatColor.YELLOW +  " Gold " + ChatColor.LIGHT_PURPLE + "Hoe slot.");
                        }
                        else if (handMaterial == Material.DIAMOND_HOE) {
                            locationArray.set(4, tpLoc);
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "Teleport location" + ChatColor.YELLOW +
                                    " (" + blockLoc.getX() + ", " + blockLoc.getY() + ", " + blockLoc.getZ() + ")" + ChatColor.LIGHT_PURPLE +
                                    " saved in the" + ChatColor.YELLOW +  " Diamond " + ChatColor.LIGHT_PURPLE + "Hoe slot.");
                        }
                    }
                }
            }
            else if (action == Action.RIGHT_CLICK_AIR) {
                if ((wieldingHoe(player, 0) || wieldingHoe(player, 1)) && !(wieldingHoe(player, 0) && wieldingHoe(player, 1))) {
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
                            }
                            else if (handMaterial == Material.STONE_HOE) {
                                tpLoc = locationArray.get(1);
                                toTp = true;
                                tpId = 1;
                            }
                            else if (handMaterial == Material.IRON_HOE) {
                                tpLoc = locationArray.get(2);
                                toTp = true;
                                tpId = 2;
                            }
                            else if (handMaterial == Material.GOLD_HOE) {
                                tpLoc = locationArray.get(3);
                                toTp = true;
                                tpId = 3;
                            }
                            else if (handMaterial == Material.DIAMOND_HOE) {
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
                                            }
                                            else {
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
                                }
                                else {
                                    player.sendMessage(ChatColor.LIGHT_PURPLE + "That saved teleport location is blocked!");
                                }
                            }
                        }
                    }
                }
                else if (wieldingHoe(player, 0) && wieldingHoe(player, 1)) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Cannot attempt to teleport with a hoe in each hand!");
                }
            }
        }

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (this.api.getPlayerCanUseAbility(player, 30, true)) {
                if (DisguiseAPI.isDisguised(player)) {
                    DisguiseAPI.undisguiseToAll(player);
                    player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Your disguise is gone.");
                }
            }
        }

        if (action == Action.LEFT_CLICK_BLOCK) {
            if (this.api.getPlayerCanUseAbility(player, 41, true)) {
                if (event.getClickedBlock().getType() == Material.DIRT || event.getClickedBlock().getType() == Material.GRASS) {
                    event.getClickedBlock().setType(Material.AIR);
                    Location blockLoc = event.getClickedBlock().getLocation();
                    blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.DIRT, 1));
                }
            }
        }

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (this.api.getPlayerCanUseAbility(player, 28, true)) {
                ItemStack mainHandItem = player.getEquipment().getItemInMainHand();
                ItemStack offHandItem = player.getEquipment().getItemInOffHand();
                boolean mHHoldingSapling = holdingSapling(player, 0);
                boolean oHHoldingSapling = holdingSapling(player, 1);
                if (mHHoldingSapling) {
                    int amount = mainHandItem.getAmount() - 1;
                    if (amount < 0) {
                        amount = 0;
                    }
                    ItemStack newSapling = getNewSapling(mainHandItem, amount);
                    player.getEquipment().setItemInMainHand(newSapling);
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (DRYAD_REGEN * 20), 1));
                    this.api.setPlayerCooldown(player, 28, 1);
                } else if (oHHoldingSapling) {
                    int amount = offHandItem.getAmount() - 1;
                    if (amount < 0) {
                        amount = 0;
                    }
                    ItemStack newSapling = getNewSapling(offHandItem, amount);
                    player.getEquipment().setItemInOffHand(newSapling);
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (DRYAD_REGEN * 20), 1));
                    this.api.setPlayerCooldown(player, 28, 1);
                }
            }

            if ((this.api.getPlayerCanUseAbility(player, 27, true)) &&
                    (player.getEquipment().getItemInMainHand().getType() == Material.FIREWORK) &&
                    player.isGliding()) {
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (ICARUS_REGEN * 20), 0));
            }

            if (this.api.getPlayerCanUseAbility(player, 33, true)) {
                if (player.getEquipment().getItemInOffHand().getType() == Material.COOKIE) {
                    int amount = player.getEquipment().getItemInOffHand().getAmount() - 1;
                    if (amount < 0) {
                        amount = 0;
                    }
                    player.getEquipment().setItemInOffHand(new ItemStack(Material.COOKIE, amount));
                    player.removePotionEffect(PotionEffectType.ABSORPTION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 0));
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (PUDGE_REGEN * 20), 0));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0F, 1.0F);
                    this.api.setPlayerCooldown(player, 33, 1);
                } else if (player.getEquipment().getItemInMainHand().getType() == Material.COOKIE) {
                    int amount = player.getEquipment().getItemInMainHand().getAmount() - 1;
                    if (amount < 0) {
                        amount = 0;
                    }
                    player.getEquipment().setItemInMainHand(new ItemStack(Material.COOKIE, amount));
                    player.removePotionEffect(PotionEffectType.ABSORPTION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 0));
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (PUDGE_REGEN * 20), 0));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0F, 1.0F);
                    this.api.setPlayerCooldown(player, 33, 1);
                }
            }

            if (this.api.getPlayerCanUseAbility(player, 44, true)) {
                if (player.getEquipment().getItemInMainHand().getType() == Material.SUGAR_CANE) {
                    int amount = player.getEquipment().getItemInMainHand().getAmount();
                    if ((amount - 1) < 0) {
                        amount = 1;
                    }
                    player.getEquipment().getItemInMainHand().setAmount(amount - 1);
                    player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, (VAMPIRE_FIRE_RES * 20), 3));
                    this.api.setPlayerCooldown(player, 44, 1);
                } else if (player.getEquipment().getItemInOffHand().getType() == Material.SUGAR_CANE) {
                    int amount = player.getEquipment().getItemInOffHand().getAmount();
                    if ((amount - 1) < 0) {
                        amount = 1;
                    }
                    player.getEquipment().getItemInOffHand().setAmount(amount - 1);
                    player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, (VAMPIRE_FIRE_RES * 20), 3));
                    this.api.setPlayerCooldown(player, 44, 1);
                }
            }

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

            if (this.api.getPlayerCanUseAbility(player, 51, true)) {
                if (player.getEquipment().getItemInMainHand().getType() == Material.FLINT_AND_STEEL) {
                    player.getEquipment().setItemInMainHand(new ItemStack(Material.FLINT_AND_STEEL, 1));
                } else if (player.getEquipment().getItemInOffHand().getType() == Material.FLINT_AND_STEEL) {
                    player.getEquipment().setItemInOffHand(new ItemStack(Material.FLINT_AND_STEEL, 1));
                }
            }

            if (this.api.getPlayerCanUseAbility(player, 32, true)) {
                ItemStack item = player.getEquipment().getItemInMainHand();
                if (item.getType() == Material.POTION) {
                    if (player.getEquipment().getItemInOffHand().getType() == Material.BLAZE_POWDER) {
                        PotionType potionType = getPotionType(item);
                        if (potionType == PotionType.WATER) {
                            int duration = (ALCHEMIST_POTION_DUR * 20);
                            int strengthIndex = 0;
                            PotionEffectType effectType = getNextPotionEffectType(player, getPotionEffectTypes());

                            if (!alchemistDict.containsKey(player)) {
                                Queue<PotionEffectType> effectQueue = new ArrayDeque<>();
                                alchemistDict.put(player, effectQueue);
                            }
                            Queue<PotionEffectType> alchQueue = alchemistDict.get(player);
                            if (alchQueue.size() > 0) {
                                PotionEffectType queueBack = (PotionEffectType) alchQueue.toArray()[alchQueue.size() - 1];
                                if (queueBack != effectType) {
                                    alchQueue.add(effectType);
                                }
                            } else {
                                alchQueue.add(effectType);
                            }
                            player.removePotionEffect(effectType);
                            player.addPotionEffect(new PotionEffect(effectType, duration, strengthIndex, false, false));
                            if (alchQueue.size() > ALCHEMIST_MAX_EFFECTS) {
                                PotionEffectType toRemove = alchQueue.remove();
                                if (!alchQueue.contains(toRemove)) {
                                    player.removePotionEffect(toRemove);
                                }
                            }
                            while (alchQueue.size() > player.getActivePotionEffects().size()) {
                                alchQueue.remove();
                            }
                            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1.0F, 1.0F);
                            String filterString = filterEffectTypes(effectType);
                            player.sendMessage(ChatColor.YELLOW + filterString);
                        }
                    }
                }
            }
        }

        // Feast chest filling on player open feast chest code
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.CHEST &&
                    feastEmpty &&
                    api.getCurrentFeastState() == FeastState.SPAWNED) {
                Block chestClicked = event.getClickedBlock();
                int feastChest = isFeastChest(chestClicked);
                if (feastChest != 0) {
                    findFeastChests(chestClicked, feastChest);
                    feastEmpty = false;
                }
            }
        }

        // Mushroom soups heal on right click code
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK && soupsEnabled) {
            boolean soup = false;
            if (player.getEquipment().getItemInMainHand().getType() == Material.MUSHROOM_SOUP) {
                player.getEquipment().setItemInMainHand(new ItemStack(Material.BOWL));
                soup = true;
            }
            else if (player.getEquipment().getItemInOffHand().getType() == Material.MUSHROOM_SOUP) {
                player.getEquipment().setItemInOffHand(new ItemStack(Material.BOWL));
                soup = true;
            }
            if (soup) {
                double difference = 20 - player.getHealth();
                double amount = soupHealAmount;
                if (difference < amount) {
                    amount = difference;
                }
                player.setHealth(player.getHealth() + amount);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.api.getPlayerCanUseAbility(player, 21, true)) {
            if (player.getLocation().getBlock().getType() == Material.WATER || player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1000000, 2));
                poseidonDict.putIfAbsent(player, true);
                poseidonDict.put(player, true);
            }
            else {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.removePotionEffect(PotionEffectType.WATER_BREATHING);
                poseidonDict.putIfAbsent(player, false);
                poseidonDict.put(player, false);
            }
        }

        if (this.api.getPlayerCanUseAbility(player, 57, true)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1000000, 2));
            if (player.getLocation().getBlock().getType() == Material.LAVA || player.getLocation().getBlock().getType() == Material.STATIONARY_LAVA ||
                    player.getLocation().getBlock().getType() == Material.FIRE) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
            }
            else {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }
        }

        if (this.api.getPlayerCanUseAbility(player, 28, true)) {
            if (player.getEquipment().getItemInOffHand().getType() == Material.SEEDS) {
                int seedAmount = player.getEquipment().getItemInOffHand().getAmount();
                if (seedAmount >= 16 && seedAmount < 32) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0));
                }
                else if (seedAmount >= 32 && seedAmount < 64) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1));
                }
                else if (seedAmount == 64) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2));
                }
                else if (seedAmount < 16){
                    player.removePotionEffect(PotionEffectType.SPEED);
                }
            }
            else {
                player.removePotionEffect(PotionEffectType.SPEED);
            }
        }

        if (this.api.getPlayerCanUseAbility(player, 29, true)) {
            if (world.getTime() >= 12750 && world.getTime() <= 23000 || player.getLocation().getBlock().getLightLevel() <= 2) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1, false, false));
            }
            else {
                player.removePotionEffect(PotionEffectType.SPEED);
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        }

        if (this.api.getPlayerCanUseAbility(player, 31, true)) {
            boolean hoeWield = wieldingHoe(player, 1);
            boolean inWheat = player.getLocation().getBlock().getRelative(BlockFace.UP, 1).getType() == Material.WHEAT;
            if (hoeWield && inWheat) {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 0));
            }
            else if (hoeWield) {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
            }
            else if (inWheat) {
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 0));
            }
            else {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            }
        }

        if (this.api.getPlayerCanUseAbility(player, 33, true)) {
            if (player.getEquipment().getItemInOffHand().getType() == Material.COOKIE) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 0));
            }
            else {
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.removePotionEffect(PotionEffectType.SLOW);
            }
        }

        if (this.api.getPlayerCanUseAbility(player, 44, true)) {
            Location playerLoc = player.getLocation();
            if (playerLoc.getBlock().getLightFromSky() >= 15 && (event.getTo().getWorld().getTime() < 12750 || event.getTo().getWorld().getTime() > 23000)) {
                player.setFireTicks(21);
            }
        }

        if (this.api.getPlayerCanUseAbility(player, 37, true)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0, false, false));
        }
    }

    @EventHandler
    public void onPlayerInteractEntity (PlayerInteractEntityEvent event) {
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
                    }
                    else if (player.getEquipment().getItemInOffHand().getType() == Material.BONE) {
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
    public void onEntityDeath (EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();

        if (event.getEntity() != null && event.getEntity().getKiller() != null) {
            if (this.api.getPlayerCanUseAbility(player, 55, true)) {
                if (event.getEntityType() == EntityType.PIG) {
                    event.getDrops().clear();
                    event.getDrops().add(new ItemStack(Material.PORK, BUTCHER_DROP_AMOUNT));
                }
                else if (event.getEntityType() == EntityType.COW) {
                    event.getDrops().clear();
                    event.getDrops().add(new ItemStack(Material.RAW_BEEF, BUTCHER_DROP_AMOUNT));
                }
                else if (event.getEntityType() == EntityType.CHICKEN) {
                    event.getDrops().clear();
                    event.getDrops().add(new ItemStack(Material.RAW_CHICKEN, BUTCHER_DROP_AMOUNT));
                }
                else if (event.getEntityType() == EntityType.SHEEP) {
                    event.getDrops().clear();
                    event.getDrops().add(new ItemStack(Material.MUTTON, BUTCHER_DROP_AMOUNT));
                }
                else if (event.getEntityType() == EntityType.RABBIT) {
                    event.getDrops().clear();
                    event.getDrops().add(new ItemStack(Material.RABBIT, BUTCHER_DROP_AMOUNT));
                }
            }

            if (this.api.getPlayerCanUseAbility(player, 39, true)) {
                if (event.getEntityType() == EntityType.SKELETON) {
                    event.getDrops().clear();
                    event.getDrops().add(new ItemStack(Material.BONE, 1));
                }
            }

            if (this.api.getPlayerCanUseAbility(player, 42, true)) {
                if (event.getEntityType() == EntityType.CHICKEN) {
                    event.getDrops().clear();
                    event.getDrops().add(new ItemStack(Material.FEATHER, 2));
                }
            }

            if (this.api.getPlayerCanUseAbility(player, 47, true)) {
                if (event.getEntityType() == EntityType.SPIDER) {
                    event.getDrops().clear();
                    event.getDrops().add(new ItemStack(Material.STRING, 1));
                }
            }

            if (this.api.getPlayerCanUseAbility(player, 65, true)) {
                if (event.getEntityType() == EntityType.ENDERMAN) {
                    event.getDrops().add(new ItemStack(Material.COOKIE, 1));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage (EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (this.api.getPlayerIsSpectator(player)) {
                event.setCancelled(true);
                return;
            }

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
                        }
                        else {
                            playerTarget.damage(damage);
                        }
                    }
                    else {
                        if (!entity.isDead()) {
                            LivingEntity livingEntity = (LivingEntity) entity;
                            livingEntity.damage(damage);
                        }
                    }
                }
            }

            if (this.api.getPlayerCanUseAbility(player, 30, true)) {
                if (DisguiseAPI.isDisguised(player)) {
                    DisguiseAPI.undisguiseToAll(player);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Your disguise is gone. You feel empowered!");
                    player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (CHAMELEON_STR_DUR * 20), 1, false, false));
                }
            }

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
    public void onBlockPlace (BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();
        //System.out.println("Block: " + block);

        if (this.api.getPlayerCanUseAbility(player, 58, true)) {
            if (block.getType() == Material.CROPS || block.getType() == Material.SEEDS) {
                block.setData(CropState.RIPE.getData());
            }
            else if (block.getType() == Material.MELON_SEEDS) {
                block.setData(CropState.RIPE.getData());
            }
            else if (block.getType() == Material.PUMPKIN_SEEDS) {
                block.setData(CropState.RIPE.getData());
            }
            else if (block.getType() == Material.SAPLING) {
                TreeType t = getTree(block.getData());
                Location blockLoc = block.getLocation();
                world.getBlockAt(blockLoc).setType(Material.AIR);
                world.generateTree(blockLoc, t);
            }
        }

        if (this.api.getPlayerCanUseAbility(player, 64, true)) {
            if (block.getType() == Material.TNT) {
                Location tntLoc = block.getLocation();

                if (wieldingSwordOrAxe(player, 0)) {
                    block.setType(Material.AIR);
                    Location newTntLoc = tntLoc.add(0.5, 0, 0.5);
                    TNTPrimed tntPrimed = (TNTPrimed) world.spawnEntity(newTntLoc, EntityType.PRIMED_TNT);
                    tntPrimed.setFuseTicks(0);

                    tntSourceDict.put(tntPrimed, player);
                    String tntID = tntPrimed.getUniqueId().toString();
                    softExplosions.add(tntID);
                }
                else if (wieldingSwordOrAxe(player, 1)) {
                    block.setType(Material.AIR);
                    Location newTntLoc = tntLoc.add(0.5, 0, 0.5);
                    TNTPrimed tntPrimed = (TNTPrimed) world.spawnEntity(newTntLoc, EntityType.PRIMED_TNT);
                    tntPrimed.setFuseTicks(SAPPER_OH_BOMB_FUSE * 20);

                    tntSourceDict.put(tntPrimed, player);
                }
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak (BlockBreakEvent event) {
        Player breaker = event.getPlayer();
        Block block = event.getBlock();

        if (this.api.getPlayerCanUseAbility(breaker, 20, true)) {
            if (block.getType() == Material.IRON_ORE) {
                Location centerOfBlock = block.getLocation();

                Random r = new Random();
                int Chance = MINER_IRON_ORE_CHANCE;
                int Amount = 1;
                int randInt = r.nextInt(200) + 1;
                event.setDropItems(false);

                if (randInt == 1) {
                    block.getWorld().dropItemNaturally(centerOfBlock, new ItemStack(Material.DIAMOND, 2));
                }
                else {
                    if (randInt > Chance) {
                        Amount = 3;
                    }
                    block.getWorld().dropItemNaturally(centerOfBlock, new ItemStack(Material.IRON_INGOT, Amount));
                }
            }
        }

        if (this.api.getPlayerCanUseAbility(breaker, 42, true)) {
            if (block.getType() == Material.GRAVEL) {
                event.setDropItems(false);
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.FLINT, 1));
            }
        }

        if (this.api.getPlayerCanUseAbility(breaker, 63, true)) {
            if (block.getType() == Material.SAND) {
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SULPHUR, 1));
            }
            if (block.getType() == Material.STONE) {
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.REDSTONE, 2));
            }
        }

        if (this.api.getPlayerCanUseAbility(breaker, 67, true)) {
            if (wieldingSwordOrAxe(breaker, 0)) {
                if (block.getType() == Material.LOG) {
                    int currY = (world.getHighestBlockYAt(breaker.getLocation())) - 30;
                    int maxHeight = currY + 60;

                    while (currY < maxHeight) {
                        for (int x = block.getX() - 3; x < block.getX() + 4; x ++) {
                            for (int z = block.getZ() - 3; z < block.getZ() + 4; z ++) {
                                Block currBlock = world.getBlockAt(x, currY, z);
                                if (currBlock.getType() == Material.LOG) {
                                    currBlock.breakNaturally();
                                    world.playSound(currBlock.getLocation(), Sound.BLOCK_WOOD_BREAK, 1.0F, 1.0F);
                                }
                            }
                        }
                        currY ++;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityRegainHealth (EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            Collection<PotionEffect> activeEffects = player.getActivePotionEffects();
            for (PotionEffect effect : activeEffects) {
                if (effect.getType().getName().equals("REGENERATION")) {
                    int amplifier = effect.getAmplifier() + 1;
                    double newAmount = regenAmount * amplifier;
                    event.setAmount(newAmount);
                }
            }
        }
    }

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

                        if (wieldingSwordOrAxe(tntSource, 0)) {
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
                        else if (wieldingSwordOrAxe(tntSource, 1)) {
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity (EntityDamageByEntityEvent event) {
        Entity entityDamager = event.getDamager();
        Entity entityDefender = event.getEntity();

        if (this.api.getCurrentGameState() == GameState.PREGAME && !(event.getEntity() instanceof Player)) {
            return;
        }

        if (this.api.getCurrentGameState() != GameState.RUNNING && event.getEntity() instanceof Player) {
            return;
        }

        if (event.getEntity().isDead()) {
            return;
        }

        if (entityDamager instanceof Player) {
            Player playerDamager = (Player) entityDamager;

            if (entityDefender.getType() == EntityType.PLAYER) {
                Player playerDefender = (Player) entityDefender;
                //System.out.println("Damage: " + event.getDamage());
                // Strength fix here
                int strFix = strFix(playerDamager, event);
                if (strFix != -1) {
                    double newStrDmg = getNewStrDmg(strFix, event.getDamage());
                    event.setDamage(newStrDmg);
                }

                if (this.api.getPlayerCanUseAbility(playerDamager, 62, true)) {
                    EntityDamageEvent.DamageCause cause = event.getCause();
                    if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                        double damage = event.getDamage();
                        double newDamage = damage * SAPPER_MELEE_DMG_NERF;
                        event.setDamage(newDamage);
                    }
                }

                if (this.api.getPlayerCanUseAbility(playerDamager, 38, true)) {
                    boolean wieldingSwordOrAxe = wieldingSwordOrAxe(playerDamager, 0);
                    if (wieldingSwordOrAxe) {
                        boolean behindPlayer = isBehind(playerDamager, playerDefender);
                        if (behindPlayer) {
                            double dmg = event.getDamage();
                            double newDmg = dmg * ASSASSIN_DMG_GIVEN;
                            event.setDamage(newDmg);
                            playerDamager.playSound(playerDamager.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                        }
                    }
                }

                if (this.api.getPlayerCanUseAbility(playerDamager, 46, true)) {
                    EntityDamageEvent.DamageCause cause = event.getCause();
                    if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                        MaterialData offhandData = playerDamager.getEquipment().getItemInOffHand().getData();
                        if (offhandData.toString().equals("RAW_FISH(2)")) {
                            Random rand = new Random();
                            int roll = rand.nextInt(100) + 1;
                            if (roll <= TRAWLER_FIRE) {
                                playerDefender.setFireTicks(60);
                            }
                        }
                        else if (offhandData.toString().equals("RAW_FISH(3)")) {
                            Random rand = new Random();
                            int roll = rand.nextInt(100) + 1;
                            if (roll <= TRAWLER_POISON) {
                                playerDefender.removePotionEffect(PotionEffectType.POISON);
                                playerDefender.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 0));
                            }
                        }
                    }
                }

                if (this.api.getPlayerCanUseAbility(playerDamager, 48, true)) {
                    EntityDamageEvent.DamageCause cause = event.getCause();
                    if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                        if (!shadowDict.containsKey(playerDamager)) {
                            shadowDict.put(playerDamager, 0.0);
                        }
                        double percentIncrease = shadowDict.get(playerDamager);
                        double damage = event.getDamage();
                        double newDamage = damage * (1 + (percentIncrease / 100));
                        playerDamager.sendMessage(ChatColor.LIGHT_PURPLE + "Damage dealt: " + newDamage);
                        event.setDamage(newDamage);
                    }
                }
            }
            if (this.api.getPlayerCanUseAbility(playerDamager, 43, true)) {
                EntityDamageEvent.DamageCause cause = event.getCause();
                if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                    if (!vampDict.containsKey(playerDamager)) {
                        vampDict.put(playerDamager, VAMP_START_LS);
                    }
                    double damage = event.getDamage();
                    double lifeSteal = vampDict.get(playerDamager);
                    double healing = damage * (lifeSteal / 100);
                    playerDamager.sendMessage(ChatColor.LIGHT_PURPLE + "Health gained: " + healing);
                    double health = playerDamager.getHealth();
                    double newHealth = health + healing;
                    if (newHealth > 20) {
                        newHealth = 20.0;
                    }
                    playerDamager.setHealth(newHealth);
                }
            }
            if (entityDefender.getType() != EntityType.PLAYER) {
                if (this.api.getPlayerCanUseAbility(playerDamager, 48, true)) {
                    double newDamage = event.getDamage() * 2;
                    event.setDamage(newDamage);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerKill (EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        EntityType killedType = event.getEntityType();
        if (killer != null && killedType == EntityType.PLAYER) {
            if (this.api.getPlayerCanUseAbility(killer, 24, true)) {
                killer.sendMessage(ChatColor.LIGHT_PURPLE + "You taste blood... you feel empowered.");
                killer.playSound(killer.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1.0F, 1.0F);
                killer.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                killer.removePotionEffect(PotionEffectType.SPEED);
                killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 420, 1));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 420, 1));
            }

            if (this.api.getPlayerCanUseAbility(killer, 43, true)) {
                if (!vampDict.containsKey(killer)) {
                    vampDict.put(killer, VAMP_START_LS);
                }
                double currLifeSteal = vampDict.get(killer) + VAMP_PLAYER_KILL_LS;
                boolean underCap = currLifeSteal < VAMP_LS_CAP;
                if (!underCap) {
                    currLifeSteal = VAMP_LS_CAP;
                }
                vampDict.put(killer, currLifeSteal);
                if (underCap) {
                    killer.sendMessage(ChatColor.LIGHT_PURPLE + "You now have " + currLifeSteal + "% life steal.");
                }
                else {
                    killer.sendMessage(ChatColor.LIGHT_PURPLE + "You are capped at " + VAMP_LS_CAP + "% life steal.");
                }
            }
        }
        else if (killer != null) {
            if (this.api.getPlayerCanUseAbility(killer, 43, true)) {
                if (!vampDict.containsKey(killer)) {
                    vampDict.put(killer, VAMP_START_LS);
                }
                double currLifeSteal = vampDict.get(killer) + VAMP_MOB_KILL_LS;
                boolean underCap = currLifeSteal < VAMP_LS_CAP;
                if (!underCap) {
                    currLifeSteal = VAMP_LS_CAP;
                }
                vampDict.put(killer, currLifeSteal);
                if (underCap) {
                    killer.sendMessage(ChatColor.LIGHT_PURPLE + "You now have " + currLifeSteal + "% life steal.");
                }
                else {
                    killer.sendMessage(ChatColor.LIGHT_PURPLE + "You are capped at " + VAMP_LS_CAP + "% life steal.");
                }
            }

            if (this.api.getPlayerCanUseAbility(killer, 48, true)) {
                if (!shadowDict.containsKey(killer)) {
                    shadowDict.put(killer, 0.0);
                }
                double currDamage = shadowDict.get(killer);
                boolean underCap = currDamage < SHADOW_DMG_CAP;
                if (underCap) {
                    if (world.getTime() >= 12750 && world.getTime() <= 23000 || killer.getLocation().getBlock().getLightLevel() <= 2) {
                        currDamage += SHADOW_DARK_DMG;
                    }
                    else {
                        currDamage += SHADOW_LIGHT_DMG;
                    }
                    if (currDamage > SHADOW_DMG_CAP) {
                        currDamage = SHADOW_DMG_CAP;
                    }
                }
                else {
                    currDamage = SHADOW_DMG_CAP;
                }
                shadowDict.put(killer, currDamage);
                if (underCap) {
                    killer.sendMessage(ChatColor.LIGHT_PURPLE + "You now have " + currDamage + "% increased melee damage.");
                }
                else {
                    killer.sendMessage(ChatColor.LIGHT_PURPLE + "You are capped at " + SHADOW_DMG_CAP + "% increased melee damage.");

                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityTarget (EntityTargetEvent event) {
        Entity entity = event.getTarget();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (this.api.getPlayerCanUseAbility(player, 61, true) &&
                    event.getReason() == EntityTargetEvent.TargetReason.CLOSEST_PLAYER)
                event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit (ProjectileHitEvent event) {
        Projectile proj = event.getEntity();

        if (proj instanceof Snowball) {
            Snowball snow = (Snowball)proj;
            if (snow.getShooter() instanceof Player) {
                Player shooter = (Player)snow.getShooter();

                if (this.api.getPlayerCanUseAbility(shooter, 25, true)) {
                    Block blockHit = event.getHitBlock();
                    Entity entityHit = event.getHitEntity();
                    Location tpLoc;
                    if (blockHit != null) {
                        tpLoc = blockHit.getLocation().add(0.5, 1, 0.5);
                    }
                    else {
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
                            }
                            else if (!target.equals(entityHit)) {
                                target.setFireTicks(PYRO_FIRE_DUR * 20);
                            }
                        }
                        entityHit.setFireTicks(PYRO_FIRE_DUR * 20);
                    }
                }

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

    @EventHandler(priority = EventPriority.NORMAL)
    private void onPlayerDamageByArrow (EntityDamageByEntityEvent event) {
        Entity entityDmg = event.getDamager();
        if (entityDmg instanceof Arrow) {
            Entity shooter = (Entity) ((Arrow) entityDmg).getShooter();
            if (shooter instanceof Player) {
                Player playerShooter = (Player) shooter;
                if (this.api.getPlayerCanUseAbility(playerShooter, 21, true)) {
                    boolean touchingWater = poseidonDict.get(playerShooter);
                    if (touchingWater) {
                        double arrowDmg = event.getFinalDamage();
                        double healing = arrowDmg * (POSEIDON_LS / 100);
                        double newShooterHealth = playerShooter.getHealth() + healing;
                        playerShooter.sendMessage(ChatColor.LIGHT_PURPLE + "Healing: " + healing);
                        if (newShooterHealth > 20) {
                            newShooterHealth = 20;
                        }
                        playerShooter.setHealth(newShooterHealth);
                    }
                }
            }
        }
    }

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
    private void onPlayerConsume (PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (this.api.getPlayerCanUseAbility(player, 21, true)) {
            if (item.getType() == Material.POTION) {
                PotionType potionType = getPotionType(item);
                if (potionType == PotionType.WATER) {
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (POSEIDON_REGEN * 20), 1));
                }
            }
        }
        if (this.api.getPlayerCanUseAbility(player, 32, true)) {
            if (item.getType() == Material.POTION) {
                PotionType potionType = getPotionType(item);
                if (potionType == PotionType.WATER) {
                    event.setCancelled(true);
                }
            }
        }
        if (this.api.getPlayerCanUseAbility(player, 35, true)) {
            if (item.getType() == Material.GRILLED_PORK || item.getType() == Material.COOKED_BEEF ||
                    item.getType() == Material.COOKED_CHICKEN || item.getType() == Material.COOKED_MUTTON ||
                    item.getType() == Material.COOKED_FISH || item.getType() == Material.COOKED_RABBIT) {
                if (!pudgeDict.containsKey(player)) {
                    pudgeDict.put(player, 0);
                }
                int currentMeat = pudgeDict.get(player) + 1;
                pudgeDict.put(player, currentMeat);
                int level = (currentMeat / PUDGE_MEAT_FOR_LVL) - 1;
                if (currentMeat % PUDGE_MEAT_FOR_LVL == 0 && level < PUDGE_CAP) {
                    double currHealth = player.getHealth();
                    player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 10000000, level));
                    player.setHealth(currHealth);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0F, 1.0F);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "You've eaten " + currentMeat + " meat giving you " + ChatColor.YELLOW + ((level + 1) * 2) +
                            ChatColor.LIGHT_PURPLE + " extra hearts.");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerSneakToggle (PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (this.api.getPlayerCanUseAbility(player, 36, true)) {
            if (event.isSneaking()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 10, false, false));
            } else {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerFish (PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (this.api.getPlayerCanUseAbility(player, 45, true)) {
            event.setExpToDrop(0);
            if (event.getState() == PlayerFishEvent.State.BITE) {
                event.setCancelled(true);
            }
            else if (event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT) {
                Random rand = new Random();
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                if (player.getEquipment().getItemInMainHand().getType() == Material.FISHING_ROD) {
                    ItemStack rod = player.getEquipment().getItemInMainHand();
                    short durability = rod.getDurability();
                    rod.setDurability((short)(durability + 1));
                }
                else  if (player.getEquipment().getItemInOffHand().getType() == Material.FISHING_ROD) {
                    ItemStack rod = player.getEquipment().getItemInOffHand();
                    short durability = rod.getDurability();
                    rod.setDurability((short)(durability + 1));
                }
                int roll = rand.nextInt(1000) + 1;
                if (roll <= TRAWLER_RAREST) { // rarest
                    ArrayList<ItemStack> rarestItems = trawlerItems.get(0);
                    int index = rand.nextInt(rarestItems.size());
                    ItemStack item = rarestItems.get(index);
                    player.getInventory().addItem(item);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1.0F, 1.0F);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Extremely rare item found! " + ChatColor.YELLOW + item.getType());
                }
                else if (roll <= TRAWLER_RARE) { // rare
                    ArrayList<ItemStack> rareItems = trawlerItems.get(1);
                    int index = rand.nextInt(rareItems.size());
                    ItemStack item = rareItems.get(index);
                    player.getInventory().addItem(item);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Rare item found! " + ChatColor.YELLOW + item.getType());
                }
                else if (roll <= TRAWLER_SEMI_RARE) { // semi-rare
                    ArrayList<ItemStack> semiRareItems = trawlerItems.get(2);
                    int index = rand.nextInt(semiRareItems.size());
                    ItemStack item = semiRareItems.get(index);
                    player.getInventory().addItem(item);
                }
                else if (roll <= TRAWLER_NORMAL) { // normal
                    ArrayList<ItemStack> normalItems = trawlerItems.get(3);
                    int index = rand.nextInt(normalItems.size());
                    ItemStack item = normalItems.get(index);
                    player.getInventory().addItem(item);
                }
                else { // trash
                    ArrayList<ItemStack> trashItems = trawlerItems.get(4);
                    int index = rand.nextInt(trashItems.size());
                    ItemStack item = trashItems.get(index);
                    player.getInventory().addItem(item);
                }
            }
        }
    }

    // Helper methods

    private boolean wieldingSwordOrAxe (Player player, int hand) {
        boolean wielding = false;
        if (hand == 0) {
            wielding = (player.getEquipment().getItemInMainHand().getType() == Material.WOOD_SWORD ||
                    player.getEquipment().getItemInMainHand().getType() == Material.STONE_SWORD ||
                    player.getEquipment().getItemInMainHand().getType() == Material.IRON_SWORD ||
                    player.getEquipment().getItemInMainHand().getType() == Material.GOLD_SWORD ||
                    player.getEquipment().getItemInMainHand().getType() == Material.DIAMOND_SWORD) ||
                    (player.getEquipment().getItemInMainHand().getType() == Material.WOOD_AXE ||
                            player.getEquipment().getItemInMainHand().getType() == Material.STONE_AXE ||
                            player.getEquipment().getItemInMainHand().getType() == Material.IRON_AXE ||
                            player.getEquipment().getItemInMainHand().getType() == Material.GOLD_AXE ||
                            player.getEquipment().getItemInMainHand().getType() == Material.DIAMOND_AXE);
        }
        else if (hand == 1) {
            wielding = (player.getEquipment().getItemInOffHand().getType() == Material.WOOD_SWORD ||
                    player.getEquipment().getItemInOffHand().getType() == Material.STONE_SWORD ||
                    player.getEquipment().getItemInOffHand().getType() == Material.IRON_SWORD ||
                    player.getEquipment().getItemInOffHand().getType() == Material.GOLD_SWORD ||
                    player.getEquipment().getItemInOffHand().getType() == Material.DIAMOND_SWORD) ||
                    (player.getEquipment().getItemInOffHand().getType() == Material.WOOD_AXE ||
                            player.getEquipment().getItemInOffHand().getType() == Material.STONE_AXE ||
                            player.getEquipment().getItemInOffHand().getType() == Material.IRON_AXE ||
                            player.getEquipment().getItemInOffHand().getType() == Material.GOLD_AXE ||
                            player.getEquipment().getItemInOffHand().getType() == Material.DIAMOND_AXE);
        }
        return (wielding);
    }

    private boolean wieldingHoe (Player player, int hand) {
        boolean wielding = false;
        if (hand == 0) {
            wielding = (player.getEquipment().getItemInMainHand().getType() == Material.WOOD_HOE ||
                    player.getEquipment().getItemInMainHand().getType() == Material.STONE_HOE ||
                    player.getEquipment().getItemInMainHand().getType() == Material.IRON_HOE ||
                    player.getEquipment().getItemInMainHand().getType() == Material.GOLD_HOE ||
                    player.getEquipment().getItemInMainHand().getType() == Material.DIAMOND_HOE);
        }
        else if (hand == 1) {
            wielding = (player.getEquipment().getItemInOffHand().getType() == Material.WOOD_HOE ||
                    player.getEquipment().getItemInOffHand().getType() == Material.STONE_HOE ||
                    player.getEquipment().getItemInOffHand().getType() == Material.IRON_HOE ||
                    player.getEquipment().getItemInOffHand().getType() == Material.GOLD_HOE ||
                    player.getEquipment().getItemInOffHand().getType() == Material.DIAMOND_HOE);
        }
        return (wielding);
    }

    public int strFix (Player player, EntityDamageByEntityEvent event) {
        int strFix = -1;
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            Collection<PotionEffect> active_effects = player.getActivePotionEffects();
            for (PotionEffect effect : active_effects) {
                PotionEffectType effectType = effect.getType();
                if (effectType.getName().equals("INCREASE_DAMAGE")) {
                    strFix = effect.getAmplifier();
                }
            }
        }
        return (strFix);
    }

    public double getNewStrDmg (int amplifier, double damage) {
        return ((damage - (3 * (amplifier + 1))) + ((amplifier + 1) * strBonus));
    }

    public boolean isBehind (Player player1, Player player2) {
        double angle = (player1.getLocation().getDirection().angle(player2.getLocation().getDirection()) / 180 * Math.PI) * 1000;
        return (angle <= 12.7);
    }

    public boolean holdingSapling (Player player, int hand) {
        boolean holdingSapling = false;
        Material mHType = player.getEquipment().getItemInMainHand().getType();
        Material oHType = player.getEquipment().getItemInOffHand().getType();
        if (hand == 0 ) {
            holdingSapling = mHType == Material.SAPLING;
        }
        else if (hand == 1) {
            holdingSapling = oHType == Material.SAPLING;
        }
        return (holdingSapling);
    }

    private ItemStack getNewSapling (ItemStack currentSapling, int amount) {
        ItemStack newSapling = null;
        if (amount >= 1) {
            String saplingString = currentSapling.getData().toString().split(" ")[3];
            //System.out.println(saplingString);

            switch (saplingString) {
                case "SAPLING(1)":
                    newSapling = new ItemStack(Material.SAPLING, amount, (short)1);
                    break;
                case "SAPLING(2)":
                    newSapling = new ItemStack(Material.SAPLING, amount, (short)2);
                    break;
                case "SAPLING(3)":
                    newSapling = new ItemStack(Material.SAPLING, amount, (short)3);
                    break;
                case "SAPLING(4)":
                    newSapling = new ItemStack(Material.SAPLING, amount, (short)4);
                    break;
                case "SAPLING(5)":
                    newSapling = new ItemStack(Material.SAPLING, amount, (short)5);
                    break;
                default:
                    newSapling = new ItemStack(Material.SAPLING, amount);
            }
        }
        return (newSapling);
    }

    public TreeType getTree (int data) {
        TreeType treeType = TreeType.TREE;

        switch (data) {
            case 1:
                treeType = TreeType.REDWOOD;
                break;
            case 2:
                treeType = TreeType.BIRCH;
                break;
            case 3:
                treeType = TreeType.JUNGLE;
                break;
            case 4:
                treeType = TreeType.ACACIA;
                break;
            case 5:
                treeType = TreeType.DARK_OAK;
                break;
        }
        return (treeType);
    }

    private ArrayList<PotionEffectType> getPotionEffectTypes () {
        ArrayList<PotionEffectType> potionEffectTypes = new ArrayList<>();
        potionEffectTypes.add(PotionEffectType.SPEED); // 0
        potionEffectTypes.add(PotionEffectType.INCREASE_DAMAGE); // 1
        potionEffectTypes.add(PotionEffectType.REGENERATION); // 2
        potionEffectTypes.add(PotionEffectType.FIRE_RESISTANCE); // 3
        potionEffectTypes.add(PotionEffectType.DAMAGE_RESISTANCE); // 4
        potionEffectTypes.add(PotionEffectType.ABSORPTION); // 5
        potionEffectTypes.add(PotionEffectType.FAST_DIGGING); // 6
        potionEffectTypes.add(PotionEffectType.INVISIBILITY); // 7
        potionEffectTypes.add(PotionEffectType.LEVITATION); // 8
        return (potionEffectTypes);
    }

    private PotionType getPotionType (ItemStack item) {
        String metaString = item.getItemMeta().toString().split(" ")[1].split(":")[1];

        PotionType potionType = null;
        if (metaString.contains("water")) {
            potionType = PotionType.WATER;
        } else if (metaString.contains("night")) {
            potionType = PotionType.NIGHT_VISION;
        } else if (metaString.contains("fire")) {
            potionType = PotionType.FIRE_RESISTANCE;
        } else if (metaString.contains("leaping")) {
            potionType = PotionType.JUMP;
        } else if (metaString.contains("regen")) {
            potionType = PotionType.REGEN;
        } else if (metaString.contains("strength")) {
            potionType = PotionType.STRENGTH;
        } else if (metaString.contains("water_breathing")) {
            potionType = PotionType.WATER_BREATHING;
        } else if (metaString.contains("invis")) {
            potionType = PotionType.INVISIBILITY;
        } else if (metaString.contains("poison")) {
            potionType = PotionType.POISON;
        } else if (metaString.contains("slowness")) {
            potionType = PotionType.SLOWNESS;
        } else if (metaString.contains("weakness")) {
            potionType = PotionType.WEAKNESS;
        } else if (metaString.contains("swiftness")) {
            potionType = PotionType.SPEED;
        }

        return (potionType);
    }

    private PotionEffectType getNextPotionEffectType (Player player, ArrayList<PotionEffectType> potionEffectTypes) {
        if (!alchemistCounter.containsKey(player)) {
            alchemistCounter.put(player, -1);
        }
        int counter = alchemistCounter.get(player) + 1;
        if (counter < 0 || counter > 8) {
            counter = 0;
        }
        alchemistCounter.put(player, counter);
        return (potionEffectTypes.get(counter));
    }

    private String filterEffectTypes (PotionEffectType toFilter) {
        String filterString = "";
        if (toFilter == PotionEffectType.SPEED)
            filterString = "Speed";
        else if (toFilter == PotionEffectType.FIRE_RESISTANCE)
            filterString = "Fire Resist";
        else if (toFilter == PotionEffectType.REGENERATION)
            filterString = "Regen";
        else if (toFilter == PotionEffectType.INCREASE_DAMAGE)
            filterString = "Strength";
        else if (toFilter == PotionEffectType.INVISIBILITY)
            filterString = "Invisibility";
        else if (toFilter == PotionEffectType.DAMAGE_RESISTANCE)
            filterString = "Damage Resist";
        else if (toFilter == PotionEffectType.HEALTH_BOOST)
            filterString = "Health Boost";
        else if (toFilter == PotionEffectType.LEVITATION)
            filterString = "Levitation";
        else if (toFilter == PotionEffectType.NIGHT_VISION)
            filterString = "Night Vision";
        else if (toFilter == PotionEffectType.ABSORPTION)
            filterString = "Absorption";
        else if (toFilter == PotionEffectType.FAST_DIGGING)
            filterString = "Haste";

        return (filterString);
    }

    public void setTrawlerItems () {
        ArrayList<ItemStack> rarestItems = new ArrayList<>();
        rarestItems.add(new ItemStack(Material.DIAMOND_SWORD, 1));  // 3 / 1000
        rarestItems.add(new ItemStack(Material.ELYTRA, 1));
        rarestItems.add(new ItemStack(Material.TOTEM, 1));

        ArrayList<ItemStack> rareItems = new ArrayList<>();       // 13 / 1000
        rareItems.add(new ItemStack(Material.IRON_SWORD, 1));
        rareItems.add(new ItemStack(Material.IRON_HELMET, 1));
        rareItems.add(new ItemStack(Material.RAW_FISH, 1, (short)3));
        rareItems.add(new ItemStack(Material.RAW_FISH, 1, (short)2));

        ArrayList<ItemStack> semiRareItems = new ArrayList<>();   // 70 / 1000
        semiRareItems.add(new ItemStack(Material.IRON_INGOT, 1));
        semiRareItems.add(new ItemStack(Material.STRING, 1));
        semiRareItems.add(new ItemStack(Material.ARROW, 2));
        semiRareItems.add(new ItemStack(Material.MUSHROOM_SOUP, 1));

        ArrayList<ItemStack> normalItems = new ArrayList<>();     // 400 / 1000
        normalItems.add(new ItemStack(Material.BROWN_MUSHROOM, 1));
        normalItems.add(new ItemStack(Material.RED_MUSHROOM, 1));
        normalItems.add(new ItemStack(Material.PORK, 1));
        normalItems.add(new ItemStack(Material.RAW_BEEF, 1));
        normalItems.add(new ItemStack(Material.RAW_CHICKEN, 1));
        normalItems.add(new ItemStack(Material.LOG, 1));
        normalItems.add(new ItemStack(Material.BREAD, 1));
        normalItems.add(new ItemStack(Material.FLINT, 1));
        normalItems.add(new ItemStack(Material.FEATHER, 1));
        normalItems.add(new ItemStack(Material.SULPHUR, 1));
        normalItems.add(new ItemStack(Material.SUGAR_CANE, 1));
        normalItems.add(new ItemStack(Material.FURNACE, 1));

        ArrayList<ItemStack> trashItems = new ArrayList<>();     // 400 / 1000
        trashItems.add(new ItemStack(Material.ROTTEN_FLESH, 1));
        trashItems.add(new ItemStack(Material.WATER_LILY, 1));
        trashItems.add(new ItemStack(Material.DIRT, 1));
        trashItems.add(new ItemStack(Material.YELLOW_FLOWER, 1));
        trashItems.add(new ItemStack(Material.SAPLING, 1));
        trashItems.add(new ItemStack(Material.SEEDS, 1));
        trashItems.add(new ItemStack(Material.COBBLESTONE, 1));
        trashItems.add(new ItemStack(Material.STICK, 2));
        trashItems.add(new ItemStack(Material.COAL, 1));
        trashItems.add(new ItemStack(Material.EGG, 1));
        trashItems.add(new ItemStack(Material.BOWL, 1));

        trawlerItems.put(0, rarestItems);
        trawlerItems.put(1, rareItems);
        trawlerItems.put(2, semiRareItems);
        trawlerItems.put(3, normalItems);
        trawlerItems.put(4, trashItems);
    }

    public int isFeastChest (Block chest) {
        int feastChest = 0;
        Block possibility1 = chest.getRelative(0, 1, 1);
        Block possibility2 = chest.getRelative(0, 1, -1);
        Block possibility3 = chest.getRelative(1, 1, 0);
        Block possibility4 = chest.getRelative(-1, 1, 0);
        if (possibility1.getType() == Material.ENCHANTMENT_TABLE) {
            feastChest = 1;
        }
        else if (possibility2.getType() == Material.ENCHANTMENT_TABLE) {
            feastChest = 2;
        }
        else if (possibility3.getType() == Material.ENCHANTMENT_TABLE) {
            feastChest = 3;
        }
        else if (possibility4.getType() == Material.ENCHANTMENT_TABLE) {
            feastChest = 4;
        }
        return (feastChest);
    }

    public void findFeastChests (Block chest, int feastChest) {
        ArrayList<Chest> chests = new ArrayList<>();
        Chest chest2 = null;
        Chest chest3 = null;
        Chest chest4 = null;
        if (feastChest == 1) {
            chest2 = (Chest) chest.getRelative(1, 0, 1).getState();
            chest3 = (Chest) chest.getRelative(-1, 0, 1).getState();
            chest4 = (Chest) chest.getRelative(0, 0, 2).getState();
        }
        else if (feastChest == 2) {
            chest2 = (Chest) chest.getRelative(1, 0, -1).getState();
            chest3 = (Chest) chest.getRelative(-1, 0, -1).getState();
            chest4 = (Chest) chest.getRelative(0, 0, -2).getState();
        }
        else if (feastChest == 3) {
            chest2 = (Chest) chest.getRelative(1, 0, 1).getState();
            chest3 = (Chest) chest.getRelative(1, 0, -1).getState();
            chest4 = (Chest) chest.getRelative(2, 0, 0).getState();
        }
        else if (feastChest == 4) {
            chest2 = (Chest) chest.getRelative(-1, 0, 1).getState();
            chest3 = (Chest) chest.getRelative(-1, 0, -1).getState();
            chest4 = (Chest) chest.getRelative(-2, 0, 0).getState();
        }
        Chest chest1 = (Chest) chest.getState();
        chests.add(chest1);
        chests.add(chest2);
        chests.add(chest3);
        chests.add(chest4);
        fillFeastChests(chests);
    }

    public void fillFeastChests (ArrayList<Chest> chests) {
        ArrayList<ItemStack> fillItems = new ArrayList<>();
        BufferedReader br = null;
        Random rand = new Random();
        try {
            br = new BufferedReader(new FileReader("feast-items.txt"));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (br != null) {
                String currLine = br.readLine();
                while (currLine != null) {
                    String[] parts = currLine.split(" ");

                    Material itemType = Material.matchMaterial(parts[0]);
                    int amount = Integer.parseInt(parts[1]);
                    ItemStack currItem = new ItemStack(itemType, amount);
                    if (parts.length > 2) {
                        for (int i = 2; i < parts.length; i++) {
                            String[] enchantParts = parts[i].split(";");
                            org.bukkit.enchantments.Enchantment enchant = org.bukkit.enchantments.Enchantment.getByName(enchantParts[0]);
                            int level = Integer.parseInt(enchantParts[1]);
                            currItem.addEnchantment(enchant, level);
                        }
                    }
                    fillItems.add(currItem);
                    currLine = br.readLine();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        int itemsPerChest = feastItemsPerChest;
        for (Chest chest : chests) {
            for (int i = 0; i < itemsPerChest; i ++) {
                int itemIndex = rand.nextInt(fillItems.size());
                chest.getInventory().addItem(fillItems.remove(itemIndex));
            }
        }
    }
}

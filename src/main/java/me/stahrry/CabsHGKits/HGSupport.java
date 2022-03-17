package me.stahrry.CabsHGKits;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import de.ftbastler.bukkitgames.api.GameStartEvent;
import de.ftbastler.bukkitgames.enums.FeastState;
import de.ftbastler.bukkitgames.enums.GameState;
import org.bukkit.*;
import org.bukkit.block.Block;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class HGSupport implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    Plugin plugin = CabsHGKits.getInstance();
    FileConfiguration configFile = CabsHGKits.getConfigFile();
    World world = Bukkit.getWorld("world");

    boolean feastEmpty = true;
    boolean soupsEnabled = Boolean.parseBoolean(configFile.getString("SOUPS_ENABLED"));
    int soupHealAmount = Integer.parseInt(configFile.getString("SOUP_HEAL_AMOUNT"));
    double strBonus = Double.parseDouble(configFile.getString("STR_BONUS"));
    double regenAmount = Double.parseDouble(configFile.getString("REGEN_AMOUNT"));
    int feastItemsPerChest = Integer.parseInt(configFile.getString("FEAST_ITEMS_PER_CHEST"));

    int BUTCHER_DROP_AMOUNT = Integer.parseInt(configFile.getString("BUTCHER_DROP_AMOUNT"));

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameStart (GameStartEvent event) {
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

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (this.api.getPlayerCanUseAbility(player, 51, true)) {
                if (player.getEquipment().getItemInMainHand().getType() == Material.FLINT_AND_STEEL) {
                    player.getEquipment().setItemInMainHand(new ItemStack(Material.FLINT_AND_STEEL, 1));
                } else if (player.getEquipment().getItemInOffHand().getType() == Material.FLINT_AND_STEEL) {
                    player.getEquipment().setItemInOffHand(new ItemStack(Material.FLINT_AND_STEEL, 1));
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
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace (BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();

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
                // Strength fix here
                int strFix = strFix(playerDamager, event);
                if (strFix != -1) {
                    double newStrDmg = getNewStrDmg(strFix, event.getDamage());
                    event.setDamage(newStrDmg);
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

    // Helper methods

    public boolean wieldingSwordOrAxe (Player player, int hand) {
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

    public boolean wieldingHoe (Player player, int hand) {
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

    public ItemStack getNewSapling (ItemStack currentSapling, int amount) {
        ItemStack newSapling = null;
        if (amount >= 1) {
            String saplingString = currentSapling.getData().toString().split(" ")[3];

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

    public ArrayList<PotionEffectType> getPotionEffectTypes () {
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

    public PotionType getPotionType (ItemStack item) {
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

    public String filterEffectTypes (PotionEffectType toFilter) {
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

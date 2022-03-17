package me.stahrry.CabsHGKits;

import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import de.ftbastler.bukkitgames.api.GameStartEvent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Trawler implements Listener {
    BukkitGamesAPI api = CabsHGKits.getBgAPI();
    FileConfiguration configFile = CabsHGKits.getConfigFile();
    Hashtable<Integer, ArrayList<ItemStack>> trawlerItems = new Hashtable<>();
    boolean trawlerItemsSet = false;

    int TRAWLER_RAREST = Integer.parseInt(configFile.getString("TRAWLER_RAREST"));
    int TRAWLER_RARE = Integer.parseInt(configFile.getString("TRAWLER_RARE"));
    int TRAWLER_SEMI_RARE = Integer.parseInt(configFile.getString("TRAWLER_SEMI_RARE"));
    int TRAWLER_NORMAL = Integer.parseInt(configFile.getString("TRAWLER_NORMAL"));
    int TRAWLER_FIRE = Integer.parseInt(configFile.getString("TRAWLER_FIRE"));
    int TRAWLER_POISON = Integer.parseInt(configFile.getString("TRAWLER_POISON"));

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameStart (GameStartEvent event) {
        if (!trawlerItemsSet) {
            setTrawlerItems();
            trawlerItemsSet = true;
        }
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity (EntityDamageByEntityEvent event) {
        Entity entityDamager = event.getDamager();
        Entity entityDefender = event.getEntity();

        if (entityDamager instanceof Player) {
            Player playerDamager = (Player) entityDamager;

            if (entityDefender.getType() == EntityType.PLAYER) {
                Player playerDefender = (Player) entityDefender;

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
                        } else if (offhandData.toString().equals("RAW_FISH(3)")) {
                            Random rand = new Random();
                            int roll = rand.nextInt(100) + 1;
                            if (roll <= TRAWLER_POISON) {
                                playerDefender.removePotionEffect(PotionEffectType.POISON);
                                playerDefender.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 0));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath (EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();

        if (event.getEntity() != null && event.getEntity().getKiller() != null) {
            if (this.api.getPlayerCanUseAbility(player, 47, true)) {
                if (event.getEntityType() == EntityType.SPIDER) {
                    event.getDrops().clear();
                    event.getDrops().add(new ItemStack(Material.STRING, 1));
                }
            }
        }
    }
}
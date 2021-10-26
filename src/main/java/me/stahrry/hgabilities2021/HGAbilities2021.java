package me.stahrry.hgabilities2021;

import de.ftbastler.bukkitgames.api.AbilityExistsException;
import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class HGAbilities2021 extends JavaPlugin {
    private static Logger logger;
    private static BukkitGamesAPI bgAPI;
    private static HGAbilities2021 instance;
    private static FileConfiguration configFile;

    public HGAbilities2021 () {
        HGAbilities2021.instance = this;
    }

    public static Logger getPluginLogger() {
        return logger;
    }

    public static BukkitGamesAPI getBgAPI () {
        return bgAPI;
    }

    public static HGAbilities2021 getInstance() {
        return (HGAbilities2021.instance);
    }

    public static FileConfiguration getConfigFile () {
        return configFile;
    }

    public void onEnable () {
        logger = getLogger();
        bgAPI = BukkitGamesAPI.getApi();
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        configFile = getConfig();

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
        int SAPPER_POISON_CHANCE = Integer.parseInt(configFile.getString("SAPPER_POISON_CHANCE"));
        int SAPPER_FIRE_CHANCE = Integer.parseInt(configFile.getString("SAPPER_FIRE_CHANCE"));
        double SAPPER_MELEE_DMG_NERF = Double.parseDouble(configFile.getString("SAPPER_MELEE_DMG_NERF"));
        int SCARECROW_CD = Integer.parseInt(configFile.getString("SCARECROW_CD"));
        double SHADOW_DMG_CAP = Double.parseDouble(configFile.getString("SHADOW_DMG_CAP"));
        double SHADOW_LIGHT_DMG = Double.parseDouble(configFile.getString("SHADOW_LIGHT_DMG"));
        double SHADOW_DARK_DMG = Double.parseDouble(configFile.getString("SHADOW_DARK_DMG"));
        int TRAWLER_FIRE = Integer.parseInt(configFile.getString("TRAWLER_FIRE"));
        int TRAWLER_POISON = Integer.parseInt(configFile.getString("TRAWLER_POISON"));
        int VAMPIRE_FIRE_RES = Integer.parseInt(configFile.getString("VAMPIRE_FIRE_RES"));
        double VAMP_START_LS = Double.parseDouble(configFile.getString("VAMP_START_LS"));
        double VAMP_PLAYER_KILL_LS = Double.parseDouble(configFile.getString("VAMP_PLAYER_KILL_LS"));
        double VAMP_MOB_KILL_LS = Double.parseDouble(configFile.getString("VAMP_MOB_KILL_LS"));
        double VAMP_LS_CAP = Double.parseDouble(configFile.getString("VAMP_LS_CAP"));
        int VIPER_POISON_CHANCE = Integer.parseInt(configFile.getString("VIPER_POISON_CHANCE"));

        try {
            getBgAPI().registerNewAbility(55, "Butcher", "Animals you kill always drop " + BUTCHER_DROP_AMOUNT + " of their meat.");
            getBgAPI().registerNewAbility(56, "Shockwave", "You take no fall damage. Damage you would have taken is transferred to players within a 3 block radius.");
            getBgAPI().registerNewAbility(57, "Avatar of Fire", "You have fire / lava immunity always. Touching fire or lava will give you a strength buff. Arrows you fire will always light your target, and any surrounding targets on fire.");
            getBgAPI().registerNewAbility(58, "Horticulturist", "Seeds and trees you plant will grow instantly.");
            getBgAPI().registerNewAbility(61, "Monster-Kin", "Monsters won't attack you unless you attack them first.");
            getBgAPI().registerNewAbility(20, "Mining Affinity", "You receive iron ingots when you mine iron ore. You also have a " + MINER_IRON_ORE_CHANCE + "% chance to receive 3 iron ingots from mining iron instead of 1.");
            getBgAPI().registerNewAbility(21, "Sea God", "You can breathe underwater, and you gain a strength buff while touching water. Arrows you fire while touching water have " + POSEIDON_LS + " life steal. After drinking water you gain a health regen buff for " + POSEIDON_REGEN + " seconds.");
            getBgAPI().registerNewAbility(24, "Bloodlust", "After killing another player you become bloodthirsty. You gain a large speed buff and a large strength buff for 20 seconds.");
            getBgAPI().registerNewAbility(25, "Jump", "Throwing a snowball instantly teleports you to where it lands. You then gain a strength buff for " + JUMPER_STR_DUR + " seconds.");
            getBgAPI().registerNewAbility(26, "Mortal Arrows", "Your arrows are poisoned, giving you an " + VIPER_POISON_CHANCE + "% chance per hit to poison your target. Also, the poison spreads on hit to any targets in a 2 block radius.");
            getBgAPI().registerNewAbility(27, "Healing Flight", "Using a firework to power Elytra grants you health regeneration for " + ICARUS_REGEN + " seconds.");
            getBgAPI().registerNewAbility(28, "Nymph's Blessing", "Consuming a sapling (right click) grants you a large amount of health regeneration for " + DRYAD_REGEN + " seconds. Holding at least 16 seeds in your off hand grants you a speed buff. Hold at least 32, and then 64 seeds to further increase the level of the speed buff.");
            getBgAPI().registerNewAbility(29, "Dark Resonance", "The shroud of darkness grants you a constant speed buff, as well as night vision. Both night, and light levels 2 or less trigger this effect.");
            getBgAPI().registerNewAbility(30, "Shape-Shifter", "Right clicking a mob transforms you into that mob until you left click. Taking damage also reverts the transformation, but grants you a large strength buff for " + CHAMELEON_STR_DUR + " seconds. You have damage resistance while transformed.");
            getBgAPI().registerNewAbility(31, "Cain's Progeny", "Holding a hoe of any kind in your off hand grants you a strength buff. Also, while standing in wheat you gain a 20% damage resistance buff.");
            getBgAPI().registerNewAbility(32, "Equivalent Exchange", "If you're holding blaze powder in your offhand you can drink a water bottle instantly (right click) to receive a new potion effect for " + ALCHEMIST_POTION_DUR + " seconds. The new potion effects loop in a cycle. You may only have " + ALCHEMIST_MAX_EFFECTS + " active potion effects.");
            getBgAPI().registerNewAbility(33, "Greed", "Holding a cookie in your offhand grants you a constant 20% damage resistance buff, but slows you. Whenever you eat a cookie, you over-heal by 2 hearts and gain a " + PUDGE_REGEN + " second health regen buff.");
            getBgAPI().registerNewAbility(34, "Death From Above", "If you're gliding with Elytra, your arrows deal " + ICARUS_BONUS_DMG + " additional damage to your target.");
            getBgAPI().registerNewAbility(35, "Gluttony", "Every " + PUDGE_MEAT_FOR_LVL + "th piece of cooked meat you consume permanently grants you 2 additional hearts of HP. Maximum is " + (PUDGE_CAP * 2) + " extra hearts.");
            getBgAPI().registerNewAbility(36, "Perfect Stealth", "You are completely invisible while crouching.");
            getBgAPI().registerNewAbility(37, "Dynamic Movement", "Fall damage is limited to at most " + ASSASSIN_MAX_FALL + " damage. You always move faster than normal.");
            getBgAPI().registerNewAbility(38, "A Quick Death", "You deal " + ((ASSASSIN_DMG_GIVEN - 1) * 100) + "% increased damage with swords (and axes) while you're behind your target. All damage dealt to you is increased by " + ((ASSASSIN_DMG_TAKEN - 1) * 100) + "%.");
            getBgAPI().registerNewAbility(39, "Bone Affinity", "Wolves are tamed with only 1 bone. Skeletons you kill will always drop 1 bone.");
            getBgAPI().registerNewAbility(40, "Lure Wolf", "Right click while holding at least " + HUNTER_WOLF_MEAT + " meat of any one kind in your main hand, and a bone in your offhand to consume the meat instantly and gain a Wolf spawn egg.");
            getBgAPI().registerNewAbility(41, "Dirty Tower Player", "You break dirt blocks instantly.");
            getBgAPI().registerNewAbility(42, "Fletcher", "Chickens will always drop 2 feathers, and gravel will always drop 1 flint.");
            getBgAPI().registerNewAbility(43, "Sanguine Power", "You have " + VAMP_START_LS + "% life steal. Every time you kill a player you drink their blood and gain an additional " + VAMP_PLAYER_KILL_LS + "% life steal. Killing any other mob grants an additional " + VAMP_MOB_KILL_LS + "% life steal. Life steal is capped at " + VAMP_LS_CAP + "%.");
            getBgAPI().registerNewAbility(44, "Solar Weakness", "When exposed directly to sunlight you will catch on fire. You can consume sugarcane (right click) to grant yourself immunity to fire for " + VAMPIRE_FIRE_RES + " seconds.");
            getBgAPI().registerNewAbility(45, "Turn the Tides", "Fishing is now instant for you (normal fishing is disabled). You can fish for an array of items from rotten flesh to arrows, to an iron sword. The more valuable an item is, the rarer it is to catch.");
            getBgAPI().registerNewAbility(46, "Seaman's Curse", "Holding a Tropical Fish in your offhand gives your melee hits a " + TRAWLER_FIRE + "% chance to ignite the target for 3 seconds. Holding a Pufferfish in your offhand gives your melee hits a " + TRAWLER_POISON + "% chance to poison the target for 4 seconds.");
            getBgAPI().registerNewAbility(47, "Resupply at Port", "Spiders you kill will always drop 1 string.");
            getBgAPI().registerNewAbility(48, "Soul Transfusion", "You deal double damage to mobs. Killing a mob absorbs its power, granting you a permanent " + SHADOW_LIGHT_DMG + "% increase in melee damage to players. If it's night, or light level 2 or lower, you gain " + SHADOW_DARK_DMG + "% damage per kill instead. Additional damage is capped at " + SHADOW_DMG_CAP + "%.");
            getBgAPI().registerNewAbility(49, "Tele-Swap", "Hitting another entity with a snowball swaps your positions instantly. The target also receives a " + JUMPER_WEAK_DUR + " second weakness debuff.");
            getBgAPI().registerNewAbility(50, "Master Delver", "You have a 0.5% chance when you mine iron to receive 2 diamonds instead.");
            getBgAPI().registerNewAbility(51, "Hardened Steel", "Your flint and steel doesn't ever deteriorate.");
            getBgAPI().registerNewAbility(62, "Flak Jacket", "You're immune to explosions. You deal " + ((1 - SAPPER_MELEE_DMG_NERF) * 100) + "% less melee damage.");
            getBgAPI().registerNewAbility(63, "Improvised Explosives", "You receive 1 gunpowder for every sand block you mine, and 2 redstone for every stone block.");
            getBgAPI().registerNewAbility(64, "Seismic Bombs, Dirty Bombs", "TNT you place with a weapon in your MAIN hand explodes, pushing away everything. No damage." +
                    " TNT you place with a weapon in your OFF hand is auto primed. Anything hit has a " + SAPPER_FIRE_CHANCE + "% chance to be set on fire AND a " + SAPPER_POISON_CHANCE + "% chance to be poisoned. Deals reduced damage.");
            getBgAPI().registerNewAbility(65, "Wrath", "Endermen always drop 1 cookie when killed.");
            getBgAPI().registerNewAbility(66, "Crop Rotation", "The most recent block you tilled with any one type of hoe saves that block. Using that same type of hoe (right click) teleports you, and any entities in a 3 block radius to that saved block. Has a " + SCARECROW_CD + " second cool down.");
            getBgAPI().registerNewAbility(67, "Forester", "You chop down trees instantly. Must be wielding an axe, or a sword.");

            Bukkit.getPluginManager().registerEvents(new AbilitiesListener(), this);
        }
        catch (AbilityExistsException e) {
            e.printStackTrace();
        }

    }
}

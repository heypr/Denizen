package com.denizenscript.denizen.scripts.containers.core;

import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.tags.BukkitTagContext;
import com.denizenscript.denizen.utilities.FormattedTextHelper;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.scripts.queues.ContextSource;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.AsciiMatcher;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class EnchantmentScriptContainer extends ScriptContainer {

    // <--[language]
    // @name Enchantment Script Containers
    // @group Script Container System
    // @description
    // Enchantment script containers allow you to register custom item enchantments.
    // For the most part, they work similarly to vanilla enchantments, albeit with some limitations.
    //
    // In current implementation, custom enchantments do not appear in lore on their own, and will need fake lore added in their place. This may be fixed in the future.
    //
    // It may be beneficial in some cases to restart your server after making changes to enchantments, rather than just reloading scripts.
    //
    // Using these may cause unpredictable compatibility issues with external plugins.
    //
    // <code>
    // Enchantment_Script_Name:
    //
    //   type: enchantment
    //
    //   # The ID is used as the internal registration key, and for lookups with things like the 'enchantments' mechanism.
    //   # If unspecified, will use the script name.
    //   # Limited to A-Z or _.
    //   # | Most enchantment scripts should have this key!
    //   id: my_id
    //
    //   # A list of slots this enchantment is valid in.
    //   # | ALL enchantment scripts MUST have this key!
    //   slots:
    //   # Can be any of: mainhand, offhand, feet, legs, chest, head
    //   - mainhand
    //
    //   # The rarity level of this enchantment. Can be any of: COMMON, UNCOMMON, RARE, VERY_RARE
    //   # If unspecified, will use COMMON.
    //   # | Most enchantment scripts should have this key!
    //   rarity: common
    //
    //   # The rarity level of this enchantment. Can be any of: ARMOR, ARMOR_FEET, ARMOR_LEGS, ARMOR_CHEST, ARMOR_HEAD, WEAPON, DIGGER, FISHING_ROD, TRIDENT, BREAKABLE, BOW, WEARABLE, CROSSBOW, VANISHABLE
    //   # If unspecified, will use WEAPON.
    //   # | Most enchantment scripts should have this key!
    //   category: weapon
    //
    //   # The per-level full name of this enchantment. Does not appear in lore automatically (but might in the future?).
    //   # Can make use of "<context.level>" for the applicable level.
    //   # | Most enchantment scripts should have this key!
    //   full_name: My Enchantment <context.level>
    //
    //   # The minimum level of this enchantment.
    //   # If unspecified, will use 1.
    //   # | Most enchantment scripts can exclude this key.
    //   min_level: 1
    //
    //   # The maximum level of this enchantment.
    //   # If unspecified, will use 1.
    //   # | Some enchantment scripts should have this key.
    //   max_level: 1
    //
    //   # The per-level minimum XP cost of enchanting.
    //   # Can make use of "<context.level>" for the applicable level.
    //   # | Most enchantment scripts should have this key!
    //   min_cost: <context.level.mul[1]>
    //
    //   # The per-level maximum XP cost of enchanting.
    //   # Can make use of "<context.level>" for the applicable level.
    //   # | Most enchantment scripts should have this key!
    //   max_cost: <context.level.mul[1]>
    //
    //   # Whether this enchantment is only considered to be treasure. (TODO: What difference does this make?)
    //   # If unspecified, will be false.
    //   # | Most enchantment scripts can exclude this key.
    //   treasure_only: false
    //
    //   # Whether this enchantment is only considered to be a curse. (TODO: What difference does this make?)
    //   # If unspecified, will be false.
    //   # | Most enchantment scripts can exclude this key.
    //   is_curse: false
    //
    //   # Whether this enchantment is only considered to be tradable. Villagers won't trade this enchantment if set to false.
    //   # If unspecified, will be true.
    //   # | Most enchantment scripts can exclude this key.
    //   is_tradable: true
    //
    //   # Whether this enchantment is only considered to be discoverable. (TODO: What difference does this make?)
    //   # If unspecified, will be true.
    //   # | Most enchantment scripts can exclude this key.
    //   is_discoverable: true
    //
    //   # A tag that returns a boolean indicating whether this enchantment is compatible with another.
    //   # Can make use of "<context.enchantment_key>" for the applicable enchantment's key, like "minecraft:sharpness".
    //   # If unspecified, will default to always true.
    //   # | Most enchantment scripts can exclude this key.
    //   is_compatible: <context.enchantment_key.advanced_matches[minecraft:lure|minecraft:luck*]>
    //
    //   # A tag that returns a boolean indicating whether this enchantment can enchant a specific item.
    //   # Can make use of "<context.item>" for the applicable ItemTag.
    //   # If unspecified, will default to always true.
    //   # | Most enchantment scripts can exclude this key.
    //   can_enchant: <context.item.advanced_matches[*_sword|*_axe]>
    //
    //   # A tag that returns an integer number indicating how much extra damage this item should deal.
    //   # Can make use of "<context.level>" for the enchantment level, and "<context.type>" for the type of monster being fought: ARTHROPOD, ILLAGER, WATER, UNDEAD, or UNDEFINED
    //   # If unspecified, will default to 0.
    //   # | Most enchantment scripts can exclude this key.
    //   damage_bonus: 0
    //
    //   # A tag that returns a decimal number indicating how much this item should protection against damage.
    //   # Can make use of "<context.level>" for the enchantment level, and "<context.cause>" for the applicable damage cause, using internal cause names.
    //   # Internal cause names: inFire, lightningBolt, onFire, lava, hotFloor, inWall, cramming, drown, starve, cactus, fall, flyIntoWall, outOfWorld, generic, magic, wither, anvil, fallingBlock, dragonBreath, dryout, sweetBerryBush, freeze, fallingStalactite, stalagmite
    //   # If unspecified, will default to 0.
    //   # | Most enchantment scripts can exclude this key.
    //   damage_protection: 0.0
    //
    // </code>
    //
    // -->
    public static AsciiMatcher descriptionCharsAllowed = new AsciiMatcher("abcdefghijklmnopqrstuvwxyz" + "_");

    public static HashMap<String, EnchantmentReference> registeredEnchantmentContainers = new HashMap<>();

    public static class EnchantmentReference {
        public EnchantmentScriptContainer script;
    }

    public EnchantmentScriptContainer(YamlConfiguration configurationSection, String scriptContainerName) {
        super(configurationSection, scriptContainerName);
        canRunScripts = false;
        id = descriptionCharsAllowed.trimToMatches(CoreUtilities.toLowerCase(getString("id", scriptContainerName)));
        descriptionId = "enchantment.denizen." + id;
        minLevel = Integer.parseInt(getString("min_level", "1"));
        maxLevel = Integer.parseInt(getString("max_level", "1"));
        isTreasureOnly = CoreUtilities.toLowerCase(getString("treasure_only", "false")).equals("true");
        isCurse = CoreUtilities.toLowerCase(getString("is_curse", "false")).equals("true");
        isTradable = CoreUtilities.toLowerCase(getString("is_tradable", "true")).equals("true");
        isDiscoverable = CoreUtilities.toLowerCase(getString("is_discoverable", "true")).equals("true");
        rarity = getString("rarity", "COMMON").toUpperCase();
        category = getString("category", "WEAPON").toUpperCase();
        slots = getStringList("slots");
        fullNameTaggable = getString("full_name", "");
        canEnchantTaggable = getString("can_enchant", "true");
        isCompatibleTaggable = getString("is_compatible", "true");
        minCostTaggle = getString("min_cost", "1");
        maxCostTaggable = getString("max_cost", "1");
        damageBonusTaggable = getString("damage_bonus", "0.0");
        damageProtectionTaggable = getString("damage_protection", "0");
        EnchantmentReference ref = registeredEnchantmentContainers.get(id);
        boolean isNew = ref == null;
        if (isNew) {
            ref = new EnchantmentReference();
        }
        ref.script = this;
        registeredEnchantmentContainers.put(id, ref);
        if (isNew) {
            NMSHandler.getItemHelper().registerFakeEnchantment(ref);
        }
    }

    public int minLevel, maxLevel;

    public String id, rarity, category, descriptionId, fullNameTaggable, canEnchantTaggable, isCompatibleTaggable, minCostTaggle, maxCostTaggable, damageBonusTaggable, damageProtectionTaggable;

    public boolean isTreasureOnly, isCurse, isTradable, isDiscoverable;

    public List<String> slots;

    public HashMap<Integer, BaseComponent[]> fullNamePerLevel = new HashMap<>();

    public void validateThread() {
        if (!Bukkit.isPrimaryThread()) {
            try {
                throw new RuntimeException("Stack reference");
            }
            catch (RuntimeException ex) {
                Debug.echoError("Warning: enchantment access from wrong thread, errors will result");
                Debug.echoError(ex);
            }
        }
    }

    public String autoTag(String value, ContextSource src) {
        if (value == null) {
            return null;
        }
        validateThread();
        TagContext context = new BukkitTagContext(null, new ScriptTag(this));
        context.contextSource = src;
        return TagManager.tag(value, context);
    }

    public String autoTagForLevel(String value, int level) {
        ContextSource.SimpleMap src = new ContextSource.SimpleMap();
        src.contexts = new HashMap<>();
        src.contexts.put("level", new ElementTag(level));
        return autoTag(value, src);
    }

    public boolean canEnchant(ItemStack item) {
        ContextSource.SimpleMap src = new ContextSource.SimpleMap();
        src.contexts = new HashMap<>();
        src.contexts.put("item", new ItemTag(item));
        String res = autoTag(canEnchantTaggable, src);
        return CoreUtilities.toLowerCase(res).equals("true");
    }

    public boolean isCompatible(Enchantment enchantment) {
        ContextSource.SimpleMap src = new ContextSource.SimpleMap();
        src.contexts = new HashMap<>();
        src.contexts.put("enchantment_key", new ElementTag(enchantment.getKey().toString()));
        String res = autoTag(isCompatibleTaggable, src);
        return CoreUtilities.toLowerCase(res).equals("true");
    }

    public BaseComponent[] getFullName(int level) {
        BaseComponent[] result = fullNamePerLevel.get(level);
        if (result != null) {
            return result;
        }
        String tagged = autoTagForLevel(fullNameTaggable, level);
        result = FormattedTextHelper.parse(tagged, ChatColor.WHITE);
        fullNamePerLevel.put(level, result);
        return result;
    }

    public int getDamageProtection(int level, String causeName) {
        ContextSource.SimpleMap src = new ContextSource.SimpleMap();
        src.contexts = new HashMap<>();
        src.contexts.put("level", new ElementTag(level));
        src.contexts.put("cause", new ElementTag(causeName));
        return Integer.parseInt(autoTag(damageProtectionTaggable, src));
    }

    public float getDamageBonus(int level, String type) {
        ContextSource.SimpleMap src = new ContextSource.SimpleMap();
        src.contexts = new HashMap<>();
        src.contexts.put("level", new ElementTag(level));
        src.contexts.put("type", new ElementTag(type));
        return Float.parseFloat(autoTag(damageBonusTaggable, src));
    }
}

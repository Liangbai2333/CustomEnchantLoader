package site.liangbai.golddigger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import site.liangbai.customenchantloader.CustomEnchantLoader;
import site.liangbai.customenchantloader.api.CustomEnchantConfig;
import site.liangbai.customenchantloader.api.CustomEnchantProcessor;
import site.liangbai.customenchantloader.api.CustomEnchantType;
import site.liangbai.customenchantloader.util.ItemUtil;

import java.util.Random;

public class GoldDigger implements CustomEnchantProcessor, Listener {
    private static final int MAX_FOOD_LEVEL = 20;

    private static double originChance;
    private static double addChancePerLevel;

    private static int originHeal;
    private static int addHealPerLevel;

    private static int originAddFoodLevel;
    private static int addFoodLevelPerLevel;

    public GoldDigger() {
        if (!CustomEnchantLoader.INSTANCE.isAvailable(CustomEnchantType.GOLD_DIGGER)) {
            return;
        }
        System.out.println("Loading enchantment: GoldDigger");
        Bukkit.getPluginManager().registerEvents(this, CustomEnchantLoader.INSTANCE.getPlugin());
        ConfigurationSection beheadConfig = CustomEnchantConfig.INSTANCE.getEnchantSection("gold_digger");
        originChance = beheadConfig.getDouble("origin_chance", 0.1);
        addChancePerLevel = beheadConfig.getDouble("add_chance_per_level", 0.1);
        originHeal = beheadConfig.getInt("origin_heal", 1);
        addHealPerLevel = beheadConfig.getInt("add_heal_per_level", 0);
        originAddFoodLevel = beheadConfig.getInt("origin_add_food_level", 1);
        addFoodLevelPerLevel = beheadConfig.getInt("add_food_level_per_level", 0);
    }

    

    @EventHandler
    public void onDig(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.GOLD_ORE) {
            return;
        }
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if (ItemUtil.isEmpty(itemStack) || !isPickaxe(itemStack)) {
            return;
        }
        if (!ItemUtil.hasCustomEnchant(itemStack, CustomEnchantType.GOLD_DIGGER)) {
            return;
        }
        int level = ItemUtil.getCustomEnchantLevel(itemStack, CustomEnchantType.GOLD_DIGGER);
        double chance = originChance + (addChancePerLevel * (level - 1));
        if (!hasChance(chance)) {
            return;
        }
        double heal = originHeal + (addHealPerLevel * (level - 1));
        int foodLevel = originAddFoodLevel + (addFoodLevelPerLevel * (level - 1));
        Player player = event.getPlayer();
        player.setHealth(player.getHealth() + heal);
        player.setFoodLevel(Math.min(player.getFoodLevel() + foodLevel, MAX_FOOD_LEVEL));
    }

    public static boolean hasChance(double chance) {
        Random random = new Random(System.nanoTime());
        return random.nextDouble() <= chance;
    }

    public static boolean isPickaxe(ItemStack itemStack) {
        return itemStack.getType().name().endsWith("_PICKAXE");
    }
}

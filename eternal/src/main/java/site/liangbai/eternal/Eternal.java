package site.liangbai.eternal;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import site.liangbai.customenchantloader.CustomEnchantLoader;
import site.liangbai.customenchantloader.api.CustomEnchantConfig;
import site.liangbai.customenchantloader.api.CustomEnchantProcessor;
import site.liangbai.customenchantloader.api.CustomEnchantType;
import site.liangbai.customenchantloader.util.ItemUtil;

import java.util.Random;

public class Eternal implements CustomEnchantProcessor, Listener {
    private static double originChance;
    private static double addChancePerLevel;

    public Eternal() {
        if (!CustomEnchantLoader.INSTANCE.isAvailable(CustomEnchantType.ETERNAL)) {
            return;
        }
        System.out.println("Loading enchantment: Eternal");
        Bukkit.getPluginManager().registerEvents(this, CustomEnchantLoader.INSTANCE.getPlugin());
        ConfigurationSection beheadConfig = CustomEnchantConfig.INSTANCE.getEnchantSection("eternal");
        originChance = beheadConfig.getDouble("origin_chance", 0.6);
        addChancePerLevel = beheadConfig.getDouble("add_chance_per_level", 0.1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        ItemStack itemStack = event.getItem();
        if (ItemUtil.isEmpty(itemStack)) {
            return;
        }
        if (!ItemUtil.hasCustomEnchant(itemStack, CustomEnchantType.ETERNAL)) {
            return;
        }
        int level = ItemUtil.getCustomEnchantLevel(itemStack, CustomEnchantType.ETERNAL);
        double chance = originChance + (addChancePerLevel * (level - 1));
        if (!hasChance(chance)) {
            return;
        }
        short durability = itemStack.getDurability();
        itemStack.setDurability((short) (durability + 1));
        event.getPlayer().getInventory().setItemInMainHand(itemStack);
    }

    public static boolean hasChance(double chance) {
        Random random = new Random(System.nanoTime());
        return random.nextDouble() <= chance;
    }
}

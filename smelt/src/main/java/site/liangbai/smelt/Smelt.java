package site.liangbai.smelt;

import net.minecraft.server.v1_12_R1.RecipesFurnace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import site.liangbai.customenchantloader.CustomEnchantLoader;
import site.liangbai.customenchantloader.api.CustomEnchantConfig;
import site.liangbai.customenchantloader.api.CustomEnchantProcessor;
import site.liangbai.customenchantloader.api.CustomEnchantType;
import site.liangbai.customenchantloader.util.ItemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Smelt implements CustomEnchantProcessor, Listener {
    private static double originChance;
    private static double addChancePerLevel;

    public Smelt() {
        if (!CustomEnchantLoader.INSTANCE.isAvailable(CustomEnchantType.SMELT)) {
            return;
        }
        System.out.println("Loading enchantment: Smelt");
        Bukkit.getPluginManager().registerEvents(this, CustomEnchantLoader.INSTANCE.getPlugin());
        ConfigurationSection beheadConfig = CustomEnchantConfig.INSTANCE.getEnchantSection("smelt");
        originChance = beheadConfig.getDouble("origin_chance", 0.1);
        addChancePerLevel = beheadConfig.getDouble("add_chance_per_level", 0.1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getBlock().getType() == Material.AIR) {
            return;
        }
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if (ItemUtil.isEmpty(itemStack)) {
            return;
        }
        if (!ItemUtil.hasCustomEnchant(itemStack, CustomEnchantType.SMELT)) {
            return;
        }
        int level = ItemUtil.getCustomEnchantLevel(itemStack, CustomEnchantType.SMELT);
        double chance = originChance + (addChancePerLevel * (level - 1));
        if (!hasChance(chance)) {
            return;
        }
        List<ItemStack> drops = new ArrayList<>(event.getBlock().getDrops(itemStack));
        if (drops.isEmpty()) {
            return;
        }
        List<ItemStack> smeltResults = drops.stream()
                .filter(it -> it.getType() != Material.AIR)
                .map(Smelt::getSmeltItemStack)
                .filter(it -> it.getType() != Material.AIR).collect(Collectors.toList());
        if (smeltResults.isEmpty()) {
            return;
        }
        event.setDropItems(false);
        Location loc = event.getBlock().getLocation();
        Bukkit.getScheduler().runTask(CustomEnchantLoader.INSTANCE.getPlugin(), () -> {
            smeltResults.forEach(it -> {
                loc.getWorld().dropItem(loc, it);
            });
        });
    }

    @NotNull
    public static ItemStack getSmeltItemStack(ItemStack itemStack) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.server.v1_12_R1.ItemStack result = RecipesFurnace.getInstance().getResult(nmsItemStack);
        return CraftItemStack.asBukkitCopy(result);
    }

    public static boolean hasChance(double chance) {
        Random random = new Random(System.nanoTime());
        return random.nextDouble() <= chance;
    }
}

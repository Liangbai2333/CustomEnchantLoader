package site.liangbai.farmer;

import net.minecraft.server.v1_12_R1.BlockCrops;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.ItemSeeds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.customenchantloader.CustomEnchantLoader;
import site.liangbai.customenchantloader.api.CustomEnchantConfig;
import site.liangbai.customenchantloader.api.CustomEnchantProcessor;
import site.liangbai.customenchantloader.api.CustomEnchantType;
import site.liangbai.customenchantloader.util.ItemUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class Farmer implements CustomEnchantProcessor, Listener {
    private static double originChance;
    private static double addChancePerLevel;

    private static Method seedGetter;
    private static Field blockGetter;
    private static Method ageGetter;

    public Farmer() {
        if (!CustomEnchantLoader.INSTANCE.isAvailable(CustomEnchantType.FARMER)) {
            return;
        }
        System.out.println("Loading enchantment: Farmer");
        Bukkit.getPluginManager().registerEvents(this, CustomEnchantLoader.INSTANCE.getPlugin());
        ConfigurationSection beheadConfig = CustomEnchantConfig.INSTANCE.getEnchantSection("farmer");
        originChance = beheadConfig.getDouble("origin_chance", 0.1);
        addChancePerLevel = beheadConfig.getDouble("add_chance_per_level", 0.1);

        try {
            seedGetter = BlockCrops.class.getDeclaredMethod("h");
            seedGetter.setAccessible(true);
            blockGetter = ItemSeeds.class.getDeclaredField("a");
            blockGetter.setAccessible(true);
            ageGetter = BlockCrops.class.getDeclaredMethod("y", IBlockData.class);
            ageGetter.setAccessible(true);
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHarvest(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getBlock().getType() == Material.AIR) {
            return;
        }
        Material seedBlockMaterial = getRipeSeedBlockItem(event.getBlock());
        if (seedBlockMaterial == null) {
            return;
        }
        Location loc = event.getBlock().getLocation();
        Block soilBlock = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()).add(0.0, -1.0, 0.0).getBlock();
        if (soilBlock.getType() != Material.SOIL) {
            return;
        }
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if (ItemUtil.isEmpty(itemStack)) {
            return;
        }
        if (!ItemUtil.hasCustomEnchant(itemStack, CustomEnchantType.FARMER)) {
            return;
        }
        int level = ItemUtil.getCustomEnchantLevel(itemStack, CustomEnchantType.FARMER);
        double chance = originChance + (addChancePerLevel * (level - 1));
        if (!hasChance(chance)) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (loc.getBlock().getType() == Material.AIR) {
                    loc.getBlock().setType(seedBlockMaterial);
                    cancel();
                }
            }
        }.runTaskTimer(CustomEnchantLoader.INSTANCE.getPlugin(), 1, 1);
    }

    public static Material getRipeSeedBlockItem(Block block) {
        net.minecraft.server.v1_12_R1.Block nmsBlock = CraftMagicNumbers.getBlock(block);
        Location loc = block.getLocation();
        if (nmsBlock instanceof BlockCrops) {
            try {
                IBlockData data = ((CraftWorld) loc.getWorld()).getHandle().getType(new BlockPosition(loc.getX(), loc.getY(), loc.getZ()));
                ItemSeeds nmsItem = (ItemSeeds) seedGetter.invoke(nmsBlock);
                net.minecraft.server.v1_12_R1.BlockCrops seedBlock = (net.minecraft.server.v1_12_R1.BlockCrops) blockGetter.get(nmsItem);
                int maxAge = seedBlock.g();
                int age = (int) ageGetter.invoke(seedBlock, data);
                if (age >= maxAge) {
                    return CraftMagicNumbers.getMaterial(seedBlock);
                } else {
                    return null;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static boolean hasChance(double chance) {
        Random random = new Random(System.nanoTime());
        return random.nextDouble() <= chance;
    }
}

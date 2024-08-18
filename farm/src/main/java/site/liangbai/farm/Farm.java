package site.liangbai.farm;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import site.liangbai.customenchantloader.CustomEnchantLoader;
import site.liangbai.customenchantloader.api.CustomEnchantConfig;
import site.liangbai.customenchantloader.api.CustomEnchantProcessor;
import site.liangbai.customenchantloader.api.CustomEnchantType;
import site.liangbai.customenchantloader.util.ItemUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Farm implements CustomEnchantProcessor, Listener {
    private static int originFarmNumber;
    private static int addNumberPerLevel;

    public Farm() {
        if (!CustomEnchantLoader.INSTANCE.isAvailable(CustomEnchantType.FARM)) {
            return;
        }
        System.out.println("Loading enchantment: Farm");
        Bukkit.getPluginManager().registerEvents(this, CustomEnchantLoader.INSTANCE.getPlugin());
        ConfigurationSection chainCollectionConfig = CustomEnchantConfig.INSTANCE.getEnchantSection("farm");
        originFarmNumber = chainCollectionConfig.getInt("origin_farm_number");
        addNumberPerLevel = chainCollectionConfig.getInt("add_number_per_level");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFarm(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (!isDirt(event.getClickedBlock())) {
            return;
        }
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if (ItemUtil.isEmpty(itemStack) || !isHoe(itemStack)) {
            return;
        }
        if (!ItemUtil.hasCustomEnchant(itemStack, CustomEnchantType.FARM)) {
            return;
        }
        int level = ItemUtil.getCustomEnchantLevel(itemStack, CustomEnchantType.FARM);
        int extraFarmNumber = originFarmNumber + (addNumberPerLevel * (level - 1));

        List<Block> blocks = getCollectionFarmBlocks(event.getClickedBlock(), extraFarmNumber);

        Bukkit.getScheduler().runTask(CustomEnchantLoader.INSTANCE.getPlugin(), () -> {
            blocks.forEach(it ->
                    it.setType(Material.SOIL)
            );
        });
    }

    public static List<Block> getCollectionFarmBlocks(Block block, int deep) {
        return getCollectionFarmBlocks(block, deep, new ArrayList<>());
    }

    // 除了block本身外的deep个方块
    public static List<Block> getCollectionFarmBlocks(Block block, int deep, List<Block> originalBlocks) {
        List<Block> blocks = new ArrayList<>();

        double blockX = block.getX();
        double blockY = block.getY();
        double blockZ = block.getZ();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                // 去除本身
                if (x == 0 && z == 0) continue;
                if (blocks.size() >= deep) break;
                Location loc = new Location(block.getWorld(), blockX + x, blockY, blockZ + z);

                if (isDirt(loc.getBlock()) && !originalBlocks.contains(loc.getBlock())) {
                    blocks.add(loc.getBlock());
                }
            }
        }
        // 乱序
        Collections.shuffle(blocks);

        if (blocks.size() < deep) {
            List<Block> copyBlocks = new ArrayList<>(blocks);
            for (Block collectedBlock : copyBlocks) {
                if (blocks.size() >= deep) break;
                int surplus = deep - blocks.size();
                List<Block> original = new ArrayList<>(blocks);
                original.add(block);
                original.addAll(originalBlocks);
                blocks.addAll(getCollectionFarmBlocks(collectedBlock, surplus, original));
            }
        }

        return blocks;
    }

    public static boolean isHoe(ItemStack itemStack) {
        return itemStack.getType().name().endsWith("_HOE");
    }

    public static boolean isDirt(Block block) {
        return block.getType() == Material.GRASS || block.getType() == Material.DIRT || block.getType() == Material.GRASS_PATH;
    }
}

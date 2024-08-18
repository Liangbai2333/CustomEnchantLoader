package site.liangbai.chaincollection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import site.liangbai.customenchantloader.CustomEnchantLoader;
import site.liangbai.customenchantloader.api.CustomEnchantConfig;
import site.liangbai.customenchantloader.api.CustomEnchantProcessor;
import site.liangbai.customenchantloader.api.CustomEnchantType;
import site.liangbai.customenchantloader.util.ItemUtil;

import java.util.*;
import java.util.stream.Collectors;

public class ChainCollection implements CustomEnchantProcessor, Listener {
    private static int originCollectionNumber;
    private static int addNumberPerLevel;

    private static final Map<Material, CustomSetting> customSettingMap = new HashMap<>();


    public ChainCollection() {
        if (!CustomEnchantLoader.INSTANCE.isAvailable(CustomEnchantType.CHAIN_COLLECTION)) {
            return;
        }
        System.out.println("Loading enchantment: ChainCollection");
        Bukkit.getPluginManager().registerEvents(this, CustomEnchantLoader.INSTANCE.getPlugin());
        ConfigurationSection chainCollectionConfig = CustomEnchantConfig.INSTANCE.getEnchantSection("chain_collection");
        originCollectionNumber = chainCollectionConfig.getInt("origin_collection_number");
        addNumberPerLevel = chainCollectionConfig.getInt("add_number_per_level");
        ConfigurationSection section = chainCollectionConfig.getConfigurationSection("custom");

        section.getKeys(false).forEach(key -> {
            ConfigurationSection customSection = section.getConfigurationSection(key);
            Material material = Material.getMaterial(key.toUpperCase());
            if (material != null) {
                List<Material> materialList = customSection.getStringList("extra_collection").stream()
                        .map(String::toUpperCase)
                        .filter(it -> Material.getMaterial(it) != null)
                        .map(Material::getMaterial).collect(Collectors.toList());
                CustomSetting setting = new CustomSetting(customSection.getBoolean("use_builtin", true), materialList);
                customSettingMap.put(material, setting);
            }
        });
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
        if (!ItemUtil.hasCustomEnchant(itemStack, CustomEnchantType.CHAIN_COLLECTION)) {
            return;
        }
        boolean forceDrop = false;
        boolean isEmptyDrop = event.getBlock().getDrops(itemStack).isEmpty();
        Material material = itemStack.getType();
        if (customSettingMap.containsKey(material)) {
            CustomSetting setting = customSettingMap.get(material);
            boolean b = setting.getExtraCollection().contains(event.getBlock().getType());
            if (setting.isUseBuiltIn() && isEmptyDrop && !b) {
                return;
            } else if (isEmptyDrop) {
                forceDrop = b;
            }
        }
        if (isEmptyDrop && !forceDrop) {
            return;
        }
        int level = ItemUtil.getCustomEnchantLevel(itemStack, CustomEnchantType.CHAIN_COLLECTION);
        int collectionNumber = originCollectionNumber + (addNumberPerLevel * (level - 1));

        List<Block> blocks = getCollectionBlocks(event.getBlock(), collectionNumber);
        if (forceDrop) {
            event.getBlock().getDrops().forEach(it -> event.getBlock().getLocation().getWorld().dropItem(event.getBlock().getLocation(), it));
        }
        blocks.forEach(Block::breakNaturally);
    }

    public static List<Block> getCollectionBlocks(Block block, int deep) {
        return getCollectionBlocks(block, deep, new ArrayList<>());
    }

    // 除了block本身外的deep个方块
    public static List<Block> getCollectionBlocks(Block block, int deep, List<Block> originalBlocks) {
        List<Block> blocks = new ArrayList<>();

        double blockX = block.getX();
        double blockY = block.getY();
        double blockZ = block.getZ();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    // 去除本身
                    if (x == 0 && y == 0 && z == 0) continue;
                    if (blocks.size() >= deep) break;
                    Location loc = new Location(block.getWorld(), blockX + x, blockY + y, blockZ + z);

                    if (block.getType() == loc.getBlock().getType() && !originalBlocks.contains(loc.getBlock())) {
                        blocks.add(loc.getBlock());
                    }
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
                blocks.addAll(getCollectionBlocks(collectedBlock, surplus, original));
            }
        }

        return blocks;
    }

    public static boolean isPickaxe(ItemStack item) {
        return item.getType().name().endsWith("_PICKAXE");
    }

    public static boolean isAxe(ItemStack item) { return item.getType().name().endsWith("_AXE"); }

    public static boolean isSpade(ItemStack item) { return item.getType().name().endsWith("_SPADE"); }
}

//    public static boolean canHarvestBlockByBuiltin(ItemStack itemStack, Block block) {
//        if (isPickaxe(itemStack)) {
//            if (itemStack.getType() == Material.WOOD_PICKAXE) {
//                switch (block.getType()) {
//                    case STONE: return true;
//                    case ANVIL: return true;
//                }
//            }
//            switch (block.getType()) {
//                case ACTIVATOR_RAIL:
//                case COAL_ORE:
//                case COAL_BLOCK:
//                case COBBLESTONE:
//                case DETECTOR_RAIL:
//                case IRON_BLOCK:
//                case DIAMOND_BLOCK:
//                case STONE_PLATE:
//                case STONE_SLAB2:
//                case LAPIS_BLOCK:
//                case DIAMOND_ORE:
//                case DOUBLE_STONE_SLAB2:
//                case RAILS:
//                case POWERED_RAIL:
//                case GOLD_BLOCK:
//                case GLOWING_REDSTONE_ORE:
//                case REDSTONE_ORE:
//                case MOSSY_COBBLESTONE:
//                case NETHERRACK:
//                case PACKED_ICE:
//                case SANDSTONE:
//                case LAPIS_ORE:
//                case RED_SANDSTONE:
//                case STONE:
//                case IRON_ORE:
//                case ICE:
//                case GOLD_ORE:
//                case EMERALD_BLOCK:
//                case EMERALD_ORE:
//                    return true;
//            }
//        }
//        if (isAxe(itemStack)) {
//            switch (block.getType()) {
//                case WOOD_PLATE:
//                case WOOD_STEP:
//                case WOOD_DOUBLE_STEP:
//                case WOOD_STAIRS:
//                case LOG:
//                case LOG_2:
//                case LADDER:
//                case MELON:
//                case PUMPKIN:
//                case CHEST:
//                case BOOKSHELF:
//                    return true;
//            }
//        }
//        if (isSpade(itemStack)) {
//            switch (block.getType()) {
//                case CLAY:
//                case CONCRETE_POWDER:
//                case GRASS_PATH:
//                case SOUL_SAND:
//                case SNOW_BLOCK:
//                case SNOW:
//                case SAND:
//                case GRAVEL:
//                case MYCEL:
//                case SOIL:
//                case GRASS:
//                case DIRT:
//                    return true;
//            }
//        }
//        return false;
//    }

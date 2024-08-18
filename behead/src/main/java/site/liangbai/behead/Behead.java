package site.liangbai.behead;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import site.liangbai.customenchantloader.CustomEnchantLoader;
import site.liangbai.customenchantloader.api.CustomEnchantConfig;
import site.liangbai.customenchantloader.api.CustomEnchantProcessor;
import site.liangbai.customenchantloader.api.CustomEnchantType;
import site.liangbai.customenchantloader.util.ItemUtil;

import java.util.Random;

public final class Behead implements CustomEnchantProcessor, Listener {
    private static double originChance;
    private static double addChancePerLevel;

    public Behead() {
        if (!CustomEnchantLoader.INSTANCE.isAvailable(CustomEnchantType.BEHEAD)) {
            return;
        }
        System.out.println("Loading enchantment: Behead");
        Bukkit.getPluginManager().registerEvents(this, CustomEnchantLoader.INSTANCE.getPlugin());
        ConfigurationSection beheadConfig = CustomEnchantConfig.INSTANCE.getEnchantSection("behead");
        originChance = beheadConfig.getDouble("origin_chance", 0.1);
        addChancePerLevel = beheadConfig.getDouble("add_chance_per_level", 0.1);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer != null) {
            ItemStack itemStack = killer.getInventory().getItemInMainHand();
            if (ItemUtil.isEmpty(itemStack)) {
                return;
            }
            if (!ItemUtil.hasCustomEnchant(itemStack, CustomEnchantType.BEHEAD)) {
                return;
            }
            int level = ItemUtil.getCustomEnchantLevel(itemStack, CustomEnchantType.BEHEAD);
            double chance = originChance + (addChancePerLevel * (level - 1));
            if (!hasChance(chance)) {
                return;
            }
            SkullType type = getLivingEntitySkullType(entity);
            ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) type.ordinal());
            if (type == SkullType.PLAYER) {
                String skullOwner;
                if (entity instanceof Player) {
                    skullOwner = entity.getName();
                } else {
                    skullOwner = getLivingEntitySkullOwner(entity);
                }
                SkullMeta meta = (SkullMeta) skullItem.getItemMeta();
                meta.setOwner(skullOwner);
                meta.setDisplayName(entity.getName() + "的头");
                skullItem.setItemMeta(meta);
            }

            entity.getWorld().dropItem(entity.getEyeLocation(), skullItem);
        }
    }

    public static boolean hasChance(double chance) {
        Random random = new Random(System.nanoTime());
        return random.nextDouble() <= chance;
    }

    public static SkullType getLivingEntitySkullType(LivingEntity entity) {
        switch (entity.getType()) {
            case ZOMBIE: return SkullType.ZOMBIE;
            case SKELETON: return SkullType.SKELETON;
            case CREEPER: return SkullType.CREEPER;
            case WITHER: return SkullType.WITHER;
            case ENDER_DRAGON: return SkullType.DRAGON;
        }
        return SkullType.PLAYER;
    }

    public static String getLivingEntitySkullOwner(LivingEntity entity) {
        switch (entity.getType()) {
            case SPIDER: return "MHF_Spider";
            case ENDERMAN: return "MHF_Enderman";
            case BLAZE: return "MHF_Blaze";
            case HORSE: return "gavertoso";
            case SQUID: return "MHF_Squid";
            case SILVERFISH: return "Xzomag";
            case ENDER_DRAGON: return "KingEndermen";
            case SLIME: return "HappyHappyMan";
            case IRON_GOLEM: return "MHF_Golem";
            case MUSHROOM_COW: return "MHF_MushroomCow";
            case BAT: return "bozzobrain";
            case PIG_ZOMBIE: return "MHF_PigZombie";
            case SNOWMAN: return "Koebasti";
            case GHAST: return "MHF_Ghast";
            case PIG: return "MHF_Pig";
            case VILLAGER: return "MHF_Villager";
            case SHEEP: return "MHF_Sheep";
            case COW: return "MHF_Cow";
            case CHICKEN: return "MHF_Chicken";
            case OCELOT: return "MHF_Ocelot";
            case WITCH: return "scrafbrothers4";
            case MAGMA_CUBE: return "MHF_LavaSlime";
            case WOLF: return "Pablo_Penguin";
            case CAVE_SPIDER: return "MHF_CaveSpider";
            case RABBIT: return "rabbit2077";
            case GUARDIAN: return "Guardian";
            case POLAR_BEAR: return "Polar_Bears";
        }

        return entity.getName();
    }
}
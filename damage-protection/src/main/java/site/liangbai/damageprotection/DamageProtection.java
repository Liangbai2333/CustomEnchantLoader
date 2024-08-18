package site.liangbai.damageprotection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import site.liangbai.customenchantloader.CustomEnchantLoader;
import site.liangbai.customenchantloader.api.CustomEnchantProcessor;
import site.liangbai.customenchantloader.api.CustomEnchantType;
import site.liangbai.customenchantloader.util.ItemUtil;

public class DamageProtection implements CustomEnchantProcessor, Listener {
    public DamageProtection() {
        if (!CustomEnchantLoader.INSTANCE.isAvailable(CustomEnchantType.DAMAGE_PROTECTION)) {
            return;
        }
        System.out.println("Loading enchantment: DamageProtection");
        Bukkit.getPluginManager().registerEvents(this, CustomEnchantLoader.INSTANCE.getPlugin());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        ItemStack itemStack = event.getItem();
        if (ItemUtil.isEmpty(itemStack)) {
            return;
        }
        if (!ItemUtil.hasCustomEnchant(itemStack, CustomEnchantType.DAMAGE_PROTECTION)) {
            return;
        }
        int durability = itemStack.getDurability();
        if (durability == 1) {
            Player player = event.getPlayer();
            PlayerInventory inventory = player.getInventory();

            int slot = inventory.getHeldItemSlot();
            int empty = inventory.firstEmpty();
            if (empty != -1) {
                inventory.setItem(slot, null);
                inventory.setItem(empty, itemStack);
            } else {
                int index = -1;
                for (int i = 0; i < inventory.getSize(); i++) {
                    if (inventory.getItem(i).isSimilar(itemStack) && inventory.getItem(i).getDurability() > 1) {
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (!inventory.getItem(i).isSimilar(itemStack)) {
                            index = i;
                            break;
                        }
                    }
                }

                ItemStack cacheItem = inventory.getItem(index);
                if (index != -1) {
                    inventory.setItem(slot, cacheItem);
                    inventory.setItem(index, itemStack);
                }
            }
            event.setCancelled(true);
        }
    }
}

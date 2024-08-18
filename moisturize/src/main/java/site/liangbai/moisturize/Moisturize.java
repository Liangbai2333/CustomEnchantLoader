package site.liangbai.moisturize;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import site.liangbai.customenchantloader.CustomEnchantLoader;
import site.liangbai.customenchantloader.api.CustomEnchantProcessor;

public class Moisturize implements CustomEnchantProcessor, Listener {
    public Moisturize() {

        System.out.println("Loading enchantment: Moisturize");
        Bukkit.getPluginManager().registerEvents(this, CustomEnchantLoader.INSTANCE.getPlugin());
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
            if (event.getClickedBlock().getType() == Material.DIRT) {
                event.getClickedBlock().setType(Material.SOIL);

            }
        }
    }
}

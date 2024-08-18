package site.liangbai.elytra;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import site.liangbai.customenchantloader.CustomEnchantLoader;
import site.liangbai.customenchantloader.api.CustomEnchantProcessor;
import site.liangbai.customenchantloader.api.CustomEnchantType;

public class Elytra implements CustomEnchantProcessor, Listener {
    public Elytra() {
        if (!CustomEnchantLoader.INSTANCE.isAvailable(CustomEnchantType.ELYTRA)) {
            return;
        }


        System.out.println("Loading enchantment: Elytra");
        Bukkit.getPluginManager().registerEvents(this, CustomEnchantLoader.INSTANCE.getPlugin());
    }
}

package site.liangbai.customenchantloader.listener

import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack
import site.liangbai.customenchantloader.CustomEnchantLoader
import site.liangbai.customenchantloader.api.CustomEnchantType
import site.liangbai.customenchantloader.util.hasCustomEnchant
import site.liangbai.customenchantloader.util.isEmpty
import taboolib.common.platform.event.SubscribeEvent

object EnchantListener {
    @SubscribeEvent
    fun onEnchantItem(event: EnchantItemEvent) {
        if (event.enchantsToAdd.keys.any { it in CustomEnchantLoader.REGISTERED_CUSTOM_ENCHANTMENTS.values }) {
            event.isCancelled = true
        }
    }

    @SubscribeEvent
    fun onAnvilUse(event: PrepareAnvilEvent) {
        val result = event.result
        if (!result.isEmpty()) {
            if (result.hasCustomEnchant(CustomEnchantType.BEHEAD) && !result.isAxe()) {
                event.result = null
            }
            if (result.hasCustomEnchant(CustomEnchantType.FARM) && !result.isHoe()) {
                event.result = null
            }
            if (result.hasCustomEnchant(CustomEnchantType.FARMER) && !result.isHoe()) {
                event.result = null
            }
            if (result.hasCustomEnchant(CustomEnchantType.SMELT) && result.isHoe()) {
                event.result = null
            }
            if (result.hasCustomEnchant(CustomEnchantType.GOLD_DIGGER) && !result.isPickaxe()) {
                event.result = null
            }
            if (result.hasCustomEnchant(CustomEnchantType.ETERNAL) && result.isHoe()) {
                event.result = null
            }
        }
    }

    fun ItemStack.isHoe() = type.name.endsWith("_HOE")

    fun ItemStack.isAxe() = type.name.endsWith("_AXE")

    fun ItemStack.isPickaxe() = type.name.endsWith("_PICKAXE")
}
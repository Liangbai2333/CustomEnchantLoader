@file:JvmName("ItemUtil")

package site.liangbai.customenchantloader.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import site.liangbai.customenchantloader.CustomEnchantLoader
import site.liangbai.customenchantloader.api.CustomEnchantType

fun ItemStack.hasCustomEnchant(enchantType: CustomEnchantType): Boolean {
    return this.containsEnchantment(CustomEnchantLoader.findCustomEnchant(enchantType))
}

fun ItemStack.getCustomEnchantLevel(enchantType: CustomEnchantType): Int {
    return this.getEnchantmentLevel(CustomEnchantLoader.findCustomEnchant(enchantType))
}

fun ItemStack?.isEmpty(): Boolean {
    return this == null || this.type == Material.AIR || this.type.name.endsWith("_AIR");
}
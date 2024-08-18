package site.liangbai.customenchantloader

import org.bukkit.craftbukkit.v1_12_R1.enchantments.CraftEnchantment
import org.bukkit.enchantments.Enchantment
import site.liangbai.customenchant.api.CustomEnchantAPI
import site.liangbai.customenchantloader.api.CustomEnchantProcessor
import site.liangbai.customenchantloader.api.CustomEnchantType
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
import taboolib.platform.BukkitPlugin
import java.io.File
import java.net.URLClassLoader

object CustomEnchantLoader : Plugin() {
    val REGISTERED_CUSTOM_ENCHANTMENTS = mutableMapOf<CustomEnchantType, Enchantment>()

    val plugin by lazy { BukkitPlugin.getInstance() }

    val customEnchantProcessors = mutableMapOf<String, CustomEnchantProcessor>()

    override fun onActive() {
        REGISTERED_CUSTOM_ENCHANTMENTS.putAll(
            CustomEnchantAPI.getCustomEnchants()
                .mapKeys { CustomEnchantType.of(it.key) }
                .mapValues { CraftEnchantment(it.value as net.minecraft.server.v1_12_R1.Enchantment) }
        )

        // 堆屎山
        val processors = File(getDataFolder(), "enchantments").also { if (!it.exists()) it.mkdirs() }
        val processorFiles = mutableListOf<File>()
        processors.listFiles()?.forEach {
            if (!it.isDirectory && it.extension == "jar") {
                processorFiles.add(it)
            }
        }
        processorFiles.forEach {
            val mainClassName = it.nameWithoutExtension
            try {
                val classLoader = URLClassLoader(arrayOf(it.toURI().toURL()), this.javaClass.classLoader)
                val fullClassName = "site.liangbai.${mainClassName.lowercase()}.$mainClassName"
                customEnchantProcessors[mainClassName] = classLoader.loadClass(fullClassName).newInstance() as CustomEnchantProcessor
            } catch (e: Throwable) {
                e.printStackTrace()
                warning("Could not load enchant processor: $mainClassName")
            }
        }

        info("Loaded custom enchants: ${customEnchantProcessors.keys.joinToString(" ")}")
    }

    fun findCustomEnchant(ench: CustomEnchantType): Enchantment? = REGISTERED_CUSTOM_ENCHANTMENTS[ench]

    fun isAvailable(ench: CustomEnchantType): Boolean {
        return true
    }
}
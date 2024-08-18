package site.liangbai.customenchantloader.api

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.function.releaseResourceFile

object CustomEnchantConfig {
    private val config by lazy {
        val file = releaseResourceFile("config.yml")
        YamlConfiguration.loadConfiguration(file)
    }

    fun getEnchantSection(name: String): ConfigurationSection {
        return config.getConfigurationSection(name)
    }
}
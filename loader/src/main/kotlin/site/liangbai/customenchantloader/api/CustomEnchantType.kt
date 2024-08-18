package site.liangbai.customenchantloader.api

enum class CustomEnchantType {
    BEHEAD,
    CHAIN_COLLECTION,
    MOISTURIZE,
    FARM,
    FARMER,
    DAMAGE_PROTECTION,
    SMELT,
    ELYTRA,
    MOVING_LIGHT_SOURCE;

    companion object {
        fun of(name: String): CustomEnchantType {
            return when (name.lowercase()) {
                "behead" -> BEHEAD
                "chain_collection" -> CHAIN_COLLECTION
                "moisturize" -> MOISTURIZE
                "farm" -> FARM
                "farmer" -> FARMER
                "damage_protection" -> DAMAGE_PROTECTION
                "smelt" -> SMELT
                "elytra" -> ELYTRA
                "moving_light_source" -> MOVING_LIGHT_SOURCE
                else -> throw IllegalArgumentException("Unknown EnchantType $name")
            }
        }
    }
}
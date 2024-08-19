package site.liangbai.customenchantloader.api

enum class CustomEnchantType {
    BEHEAD,
    CHAIN_COLLECTION,
    FARM,
    FARMER,
    DAMAGE_PROTECTION,
    SMELT,
    MOVING_LIGHT_SOURCE,
    GOLD_DIGGER,
    ETERNAL;

    companion object {
        fun of(name: String): CustomEnchantType {
            return when (name.lowercase()) {
                "behead" -> BEHEAD
                "chain_collection" -> CHAIN_COLLECTION
                "farm" -> FARM
                "farmer" -> FARMER
                "damage_protection" -> DAMAGE_PROTECTION
                "smelt" -> SMELT
                "moving_light_source" -> MOVING_LIGHT_SOURCE
                "gold_digger" -> GOLD_DIGGER
                "eternal" -> ETERNAL
                else -> throw IllegalArgumentException("Unknown EnchantType $name")
            }
        }
    }
}
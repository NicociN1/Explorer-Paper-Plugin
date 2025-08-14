package com.nicon.explorerPaper.definitions

class MiscConstants {
    companion object {
        /**
         * ドロップ不可なアイテムIDリスト
         */
        val DISABLED_DROP_ITEMS = arrayOf(
            "minecraft:compass",
            "minecraft:wooden_pickaxe",
            "minecraft:wooden_axe",
            "minecraft:wooden_shovel"
        )

        /**
         * 耐久ダメージ無効化アイテム
         */
        val DISABLED_DURABILITY_ITEMS = arrayOf(
            "minecraft:wooden_pickaxe",
            "minecraft:wooden_axe",
            "minecraft:wooden_shovel"
        )

        /**
         * 設置可能なブロックリスト
         */
        val ENABLED_PLACE_BLOCKS = arrayOf(
            "minecraft:torch",
            "minecraft:oak_planks"
        )
    }
}
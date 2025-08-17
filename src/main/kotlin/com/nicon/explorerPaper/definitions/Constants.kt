package com.nicon.explorerPaper.definitions

import org.bukkit.Material

class Constants {
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

        /**
         * 右クリックを無効化するブロックリスト
         */
        val DISABLED_INTERACT_BLOCKS = arrayOf(
            Material.OAK_LOG
        )

        /**
         * 上位鉱石リスト
         */
        val HIGH_GRADE_ORES = arrayOf(
            Material.GOLD_INGOT,
            Material.LAPIS_ORE,
            Material.REDSTONE_ORE,
            Material.DIAMOND_ORE
        )
    }
}
package com.nicon.explorerPaper.definitions

import org.bukkit.Material
import org.bukkit.block.BlockFace

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
            Material.GOLD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.LAPIS_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.REDSTONE_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE
        )

        /**
         * ストレージの最大数
         */
        const val MAX_STORAGE_COUNT = 9

        /**
         * ブロックの6面
         */
        val faces = listOf(
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST
        )

        /**
         * 未解放状態のレベルキャップ
         */
        val initialLevelCap = 19
    }
}
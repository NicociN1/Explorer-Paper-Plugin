package com.nicon.explorerPaper.blocks

import com.nicon.explorerPaper.utils.PlayerUtils

class BlockData {
    var blocks: MutableMap<String, BlockDetail> = mutableMapOf()

    constructor()

    class BlockDetail {
        var lootTable: String? = null
        var smeltedLootTable: String? = null
        var gold: Drop? = null
        var amethyst: Drop? = null
        var appropriateTools: String? = null
        var xp: Int? = null
        var levelType: PlayerUtils.LevelType? = null

        constructor()

        class Drop {
            var chance: Float = 0f
            var min: Int = 0
            var max: Int = 0
        }
    }
}
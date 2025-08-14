package com.nicon.explorerPaper.blocks

class BlockData {
    var blocks: MutableMap<String, BlockDetail> = mutableMapOf()

    constructor() {}

    class BlockDetail {
        var lootTable: String? = null
        var xp: Int = 0
        var gold: Drop? = null
        var amethyst: Drop? = null
        var appropriateTools: String? = null

        constructor() {}

        class Drop {
            var chance: Float = 0f
            var min: Int = 0
            var max: Int = 0
        }
    }
}
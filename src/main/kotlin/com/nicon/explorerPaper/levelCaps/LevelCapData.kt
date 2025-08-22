package com.nicon.explorerPaper.levelCaps

import com.nicon.explorerPaper.utils.PlayerUtils

class LevelCapData {
    var levelCaps: MutableMap<PlayerUtils.LevelType, Array<LevelCapDetail>> = mutableMapOf()

    constructor()

    class LevelCapDetail {
        var level: Int = 0
        var requireGold: Int = 0
        var requireItems: Array<InputItem> = arrayOf()

        class InputItem {
            var id: String = ""
            var label: String = ""
            var amount: Int = 1

            constructor()
        }
    }
}
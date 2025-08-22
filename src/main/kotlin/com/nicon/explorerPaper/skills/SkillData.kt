package com.nicon.explorerPaper.skills

import com.nicon.explorerPaper.utils.PlayerUtils

class SkillData {
    var skills: MutableMap<PlayerUtils.LevelType, Array<SkillDetail>> = mutableMapOf()

    constructor()

    class SkillDetail {
        var id: String = ""
        var name: String = ""
        var skillLevels: Array<SkillLevel> = emptyArray()
        var iconItemId: String = ""

        constructor()

        class SkillLevel {
            var unlockLevel: Int = 0
            var description: String = ""
            var states: Array<String>? = null

            constructor()
        }
    }
}
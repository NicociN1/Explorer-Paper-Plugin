package com.nicon.explorerPaper.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "players")
data class Players(
    @DatabaseField(id = true)
    var uuid: String = "",

    @DatabaseField(columnName = "user_name")
    var userName: String = "",

    @DatabaseField
    var gold: Int = 0,

    @DatabaseField
    var amethyst: Int = 0,

    @DatabaseField(columnName = "blocks_mined")
    var blocksMined: Int = 0,

    @DatabaseField(columnName = "unlocked_recipe_tags", defaultValue = "[]")
    var unlockedRecipeTags: String = "[]",

    @DatabaseField(columnName = "land_level")
    var landLevel: Int = 1,

    @DatabaseField(columnName = "land_xp")
    var landXp: Int = 0,

    @DatabaseField(columnName = "land_level_cap_count")
    var landLevelCapCount: Int = 0,

    @DatabaseField(columnName = "wood_level")
    var woodLevel: Int = 1,

    @DatabaseField(columnName = "wood_xp")
    var woodXp: Int = 0,

    @DatabaseField(columnName = "wood_level_cap_count")
    var woodLevelCapCount: Int = 0,

    @DatabaseField(columnName = "stone_level")
    var stoneLevel: Int = 1,

    @DatabaseField(columnName = "stone_xp")
    var stoneXp: Int = 0,

    @DatabaseField(columnName = "stone_level_cap_count")
    var stoneLevelCapCount: Int = 0,

    @DatabaseField(columnName = "ore_level")
    var oreLevel: Int = 1,

    @DatabaseField(columnName = "ore_xp")
    var oreXp: Int = 0,

    @DatabaseField(columnName = "ore_level_cap_count")
    var oreLevelCapCount: Int = 0,

    @DatabaseField(columnName = "skill_states", defaultValue = "{}")
    var skillStates: String = "{}",

    @DatabaseField
    var mana: Int = 0,

    @DatabaseField(columnName = "max_mana")
    var maxMana: Int = 100
)
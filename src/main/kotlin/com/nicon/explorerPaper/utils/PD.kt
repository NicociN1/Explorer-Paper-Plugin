package com.nicon.explorerPaper.utils

import com.nicon.explorerPaper.Database
import com.nicon.explorerPaper.model.Players
import kotlinx.serialization.json.Json
import org.bukkit.entity.Player

object PD {
    fun createIfNotExists(player: Player) {
        Database.playerDao.createIfNotExists(
            Players(
                player.uniqueId.toString(),
                player.name
            )
        )
    }

    fun getGold(player: Player): Int {
        val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
        return playerData?.gold ?: 0
    }

    fun setGold(player: Player, gold: Int) {
        createIfNotExists(player)

        val updateBuilder = Database.playerDao.updateBuilder()
        updateBuilder.updateColumnValue("gold", gold)
        updateBuilder.where().eq("uuid", player.uniqueId.toString())
        updateBuilder.update()
    }

    fun getBlocksMined(player: Player): Int {
        val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
        return playerData?.blocksMined ?: 0
    }

    fun setBlocksMined(player: Player, blocksMined: Int) {
        createIfNotExists(player)

        val updateBuilder = Database.playerDao.updateBuilder()
        updateBuilder.updateColumnValue("blocks_mined", blocksMined)
        updateBuilder.where().eq("uuid", player.uniqueId.toString())
        updateBuilder.update()
    }

    fun getUnlockedRecipeTags(player: Player): MutableList<String> {
        val playerData = Database.playerDao.queryForId(player.uniqueId.toString()) ?: return mutableListOf()
        val unlockedRecipes = Json.decodeFromString<MutableList<String>>(playerData.unlockedRecipeTags)

        return unlockedRecipes
    }

    fun setUnlockedRecipeTags(player: Player, unlockedRecipeTags: MutableList<String>) {
        createIfNotExists(player)

        val unlockedRecipeTagsTxt = Json.encodeToString(unlockedRecipeTags)

        val updateBuilder = Database.playerDao.updateBuilder()
        updateBuilder.updateColumnValue("unlocked_recipe_tags", unlockedRecipeTagsTxt)
        updateBuilder.where().eq("uuid", player.uniqueId.toString())
        updateBuilder.update()
    }

    fun getSkillStates(player: Player): MutableMap<String, String> {
        val playerData = Database.playerDao.queryForId(player.uniqueId.toString()) ?: return mutableMapOf()
        val unlockedRecipes = Json.decodeFromString<MutableMap<String, String>>(playerData.skillStates)

        return unlockedRecipes
    }

    fun setSkillStates(player: Player, skillStates: MutableMap<String, String>) {
        createIfNotExists(player)

        val skillStatesTxt = Json.encodeToString(skillStates)

        val updateBuilder = Database.playerDao.updateBuilder()
        updateBuilder.updateColumnValue("skill_states", skillStatesTxt)
        updateBuilder.where().eq("uuid", player.uniqueId.toString())
        updateBuilder.update()
    }

    object Level {
        fun getLandLevel(player: Player): Int {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.landLevel ?: 0
        }

        fun setLandLevel(player: Player, level: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("land_level", level)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getLandXp(player: Player): Int {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.landXp ?: 0
        }

        fun setLandXp(player: Player, xp: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("land_xp", xp)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getLandLevelCapCount(player: Player): Int {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.landLevelCapCount ?: 0
        }

        fun setLandLevelCapCount(player: Player, levelCap: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("land_level_cap_count", levelCap)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getWoodLevel(player: Player): Int {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.woodLevel ?: 0
        }

        fun setWoodLevel(player: Player, level: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("wood_level", level)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getWoodXp(player: Player): Int {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.woodXp ?: 0
        }

        fun setWoodXp(player: Player, xp: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("wood_xp", xp)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getWoodLevelCapCount(player: Player): Int {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.woodLevelCapCount ?: 0
        }

        fun setWoodLevelCapCount(player: Player, levelCap: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("wood_level_cap_count", levelCap)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getStoneLevel(player: Player): Int {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.stoneLevel ?: 0
        }

        fun setStoneLevel(player: Player, level: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("stone_level", level)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getStoneXp(player: Player): Int {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.stoneXp ?: 0
        }

        fun setStoneXp(player: Player, xp: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("stone_xp", xp)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getStoneLevelCapCount(player: Player): Int {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.stoneLevelCapCount ?: 0
        }

        fun setStoneLevelCapCount(player: Player, levelCap: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("stone_level_cap_count", levelCap)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getOreLevel(player: Player): Int {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.oreLevel ?: 0
        }

        fun setOreLevel(player: Player, level: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("ore_level", level)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getOreXp(player: Player): Int {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.oreXp ?: 0
        }

        fun setOreXp(player: Player, xp: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("ore_xp", xp)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getOreLevelCapCount(player: Player): Int {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.oreLevelCapCount ?: 0
        }

        fun setOreLevelCapCount(player: Player, levelCap: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("ore_level_cap_count", levelCap)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }
    }

    fun getAmethyst(player: Player): Int {
        val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
        return playerData?.amethyst ?: 0
    }

    fun setAmethyst(player: Player, amethyst: Int) {
        createIfNotExists(player)

        val updateBuilder = Database.playerDao.updateBuilder()
        updateBuilder.updateColumnValue("amethyst", amethyst)
        updateBuilder.where().eq("uuid", player.uniqueId.toString())
        updateBuilder.update()
    }

    fun getMana(player: Player): Int {
        val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
        return playerData?.mana ?: 0
    }

    fun setMana(player: Player, mana: Int) {
        createIfNotExists(player)

        val updateBuilder = Database.playerDao.updateBuilder()
        updateBuilder.updateColumnValue("mana", mana)
        updateBuilder.where().eq("uuid", player.uniqueId.toString())
        updateBuilder.update()
    }

    fun getMaxMana(player: Player): Int {
        val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
        return playerData?.maxMana ?: 100
    }

    fun setMaxMana(player: Player, mana: Int) {
        createIfNotExists(player)

        val updateBuilder = Database.playerDao.updateBuilder()
        updateBuilder.updateColumnValue("max_mana", mana)
        updateBuilder.where().eq("uuid", player.uniqueId.toString())
        updateBuilder.update()
    }
}
package com.nicon.explorerPaper.utils

import Players
import com.nicon.explorerPaper.Database
import com.nicon.explorerPaper.Main
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.LootContext
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import java.lang.Math.round
import java.util.Random
import java.util.UUID
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

object PlayerUtils {
    /**
     * アイテムをインベントリから削除
     */
    fun clearItems(player: Player, id: String, amount: Int? = null) {
        var amountCount: Int? = amount
        for (slot in 0..<player.inventory.size) {
            val itemStack = player.inventory.getItem(slot) ?: continue
            if (itemStack.type.key.toString() != id) continue

            var itemAmount: Int = itemStack.amount

            if (amountCount != null) {
                val minusAmount = min(64, amountCount)
                amountCount -= minusAmount
                itemAmount -= minusAmount
            } else {
                itemAmount = 0
            }

            if (itemAmount <= 0) {
                player.inventory.setItem(slot, ItemStack.empty())
            } else {
                itemStack.amount = itemAmount
                player.inventory.setItem(slot, itemStack)
            }

            if (amountCount != null && amountCount <= 0) return
        }
    }

    /**
     * アイテムを持っている量を取得
     */
    fun getItemAmount(player: Player, id: String): Int {
        var amountCount = 0
        for (slot in 0..<player.inventory.size) {
            val itemStack = player.inventory.getItem(slot) ?: continue
            if (itemStack.type.key.toString() != id) continue

            amountCount += itemStack.amount
        }

        return amountCount
    }

    object PlayerDatabase {
        fun createIfNotExists(player: Player) {
            Database.playerDao.createIfNotExists(Players(
                player.uniqueId.toString(),
                player.name
            ))
        }

        fun getGold(player: Player): Int? {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.gold
        }

        fun setGold(player: Player, gold: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("gold", gold)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getBlocksMined(player: Player): Int? {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.blocksMined
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
        }

        fun getAmethyst(player: Player): Int? {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.amethyst
        }

        fun setAmethyst(player: Player, amethyst: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("amethyst", amethyst)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getMana(player: Player): Int? {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.mana
        }

        fun setMana(player: Player, mana: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("mana", mana)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }
    }

    /**
     * サイドバーを更新
     */
    fun refreshSidebar(player: Player) {
        val board = player.scoreboard

        val objective: Objective = board.getObjective("sidebar") ?:
            board.registerNewObjective(
                "sidebar",
                Criteria.DUMMY,
                Component
                    .text()
                    .decoration(TextDecoration.BOLD, true)
                    .content("<Explorer Server>")
                    .build(),
            )

        if (objective.displaySlot != DisplaySlot.SIDEBAR) {
            objective.displaySlot = DisplaySlot.SIDEBAR
        }

        val goldScore = objective.getScore("gold")
        goldScore.score = -1
        goldScore.customName(
            Component
                .text()
                .color(NamedTextColor.GOLD)
                .content("Gold: ${Utils.addCommaToNumber(PlayerDatabase.getGold(player) ?: 0)}")
                .build()
        )

        val amethystScore = objective.getScore("amethyst")
        amethystScore.score = -2
        amethystScore.customName(
            Component
                .text()
                .color(NamedTextColor.LIGHT_PURPLE)
                .content("Amethyst: ${Utils.addCommaToNumber(PlayerDatabase.getAmethyst(player) ?: 0)}")
                .build()
        )

        val blocksMinedScore = objective.getScore("blocksMined")
        blocksMinedScore.score = -3
        blocksMinedScore.customName(
            Component
                .text()
                .color(NamedTextColor.WHITE)
                .content("累計採掘回数: ${Utils.addCommaToNumber(PlayerDatabase.getBlocksMined(player) ?: 0)}")
                .build()
        )

        player.scoreboard = board
    }

    /**
     * レシピを解放させる
     */
    fun unlockRecipe(player: Player, recipeTag: String) {
        val unlockedRecipes = PlayerDatabase.getUnlockedRecipeTags(player)
        if (unlockedRecipes.contains(recipeTag)) return
        unlockedRecipes.add(recipeTag)
        PlayerDatabase.setUnlockedRecipeTags(player, unlockedRecipes)

        for (recipe in Main.recipeDetails) {
            if (recipe.tag != recipeTag) continue
            player.sendMessage(
                Component
                    .text()
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .content("[${recipe.name}] レシピを開放しました！")
                    .build()
            )
        }

        player.playSound(player.location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f)
    }

    enum class LevelType {
        LAND,
        WOOD,
        STONE,
        ORE
    }

    val expBossBars = mutableMapOf<UUID, BossBar>()
    /**
     * ボスバーを更新
     */
    fun refreshExpBossBar(player: Player, levelType: LevelType) {
        val expBossBar = expBossBars[player.uniqueId] ?: run {
            val newBar = player.server.createBossBar(
                "",
                BarColor.GREEN,
                BarStyle.SOLID
            )
            newBar.addPlayer(player)
            expBossBars[player.uniqueId] = newBar
            newBar
        }

        val levelName = getLevelTypeName(levelType)

        val level = getTypeLevel(player, levelType)
        val xp = getTypeXp(player, levelType)
        val requireXp = getExpToNextLevel(level)
        val xpProgress = (xp.toDouble() / requireXp).coerceIn(0.0, 1.0)

        expBossBar.setTitle("${levelName}Lv.${level} ${Utils.addCommaToNumber(xp)}/${Utils.addCommaToNumber(requireXp)}")
        expBossBar.progress = xpProgress
    }

    val manaBossBars = mutableMapOf<UUID, BossBar>()
    /**
     * ボスバーを更新
     */
    fun refreshManaBossBar(player: Player) {
        val manaBossBar = manaBossBars[player.uniqueId] ?: run {
            val newBar = player.server.createBossBar(
                "",
                BarColor.BLUE,
                BarStyle.SOLID
            )
            newBar.addPlayer(player)
            manaBossBars[player.uniqueId] = newBar
            newBar
        }
        val maxMana = 1000
        val currentMana = PlayerDatabase.getMana(player) ?: 0
        val progress = (currentMana.toDouble() / maxMana).coerceIn(0.0, 1.0)

        manaBossBar.setTitle("マナ: ${currentMana}/${maxMana}")
        manaBossBar.progress = progress
    }

    fun getExpToNextLevel(level: Int): Int {
        val base = 1.45
        val initial = 20.0
        return (initial * base.pow((level - 1).toDouble())).roundToInt()
//        return 4
    }

    fun addExp(player: Player, levelType: LevelType, exp: Int) {
        val currentExp = getTypeXp(player, levelType)
        setTypeXp(player, levelType, currentExp + exp)
        refreshLevel(player, levelType)
        refreshExpBossBar(player, levelType)
    }

    fun getTypeLevel(player: Player, levelType: LevelType): Int {
        return when (levelType) {
            LevelType.LAND -> {
                PlayerDatabase.Level.getLandLevel(player)
            }
            LevelType.WOOD -> {
                PlayerDatabase.Level.getWoodLevel(player)
            }
            LevelType.STONE -> {
                PlayerDatabase.Level.getStoneLevel(player)
            }
            LevelType.ORE -> {
                PlayerDatabase.Level.getOreLevel(player)
            }
        }
    }

    fun getTypeXp(player: Player, levelType: LevelType): Int {
        return when (levelType) {
            LevelType.LAND -> {
                PlayerDatabase.Level.getLandXp(player)
            }
            LevelType.WOOD -> {
                PlayerDatabase.Level.getWoodXp(player)
            }
            LevelType.STONE -> {
                PlayerDatabase.Level.getStoneXp(player)
            }
            LevelType.ORE -> {
                PlayerDatabase.Level.getOreXp(player)
            }
        }
    }

    fun setTypeLevel(player: Player, levelType: LevelType, level: Int) {
        when (levelType) {
            LevelType.LAND -> {
                PlayerDatabase.Level.setLandLevel(player, level)
            }
            LevelType.WOOD -> {
                PlayerDatabase.Level.setWoodLevel(player, level)
            }
            LevelType.STONE -> {
                PlayerDatabase.Level.setStoneLevel(player, level)
            }
            LevelType.ORE -> {
                PlayerDatabase.Level.setOreLevel(player, level)
            }
        }
    }

    fun setTypeXp(player: Player, levelType: LevelType, xp: Int) {
        when (levelType) {
            LevelType.LAND -> {
                PlayerDatabase.Level.setLandXp(player, xp)
            }
            LevelType.WOOD -> {
                PlayerDatabase.Level.setWoodXp(player, xp)
            }
            LevelType.STONE -> {
                PlayerDatabase.Level.setStoneXp(player, xp)
            }
            LevelType.ORE -> {
                PlayerDatabase.Level.setOreXp(player, xp)
            }
        }
    }

    fun getLevelTypeName(levelType: LevelType): String {
        return when (levelType) {
            LevelType.LAND -> {
            "整地"
        }
            LevelType.WOOD -> {
            "伐採"
        }
            LevelType.STONE -> {
            "採石"
        }
            LevelType.ORE -> {
            "鉱石採掘"
        }
        }
    }

    fun refreshLevel(player: Player, levelType: LevelType) {
        val xp = getTypeXp(player, levelType)
        val level = getTypeLevel(player, levelType)
        val requireExp = getExpToNextLevel(level)

        if (xp >= requireExp) {
            setTypeLevel(player, levelType, level + 1)
            setTypeXp(player, levelType, xp - requireExp)

            val levelName = getLevelTypeName(levelType)
            player.sendMessage(
                Component
                    .text()
                    .content("${levelName}レベルアップ！ $level > ${level + 1}")
                    .color(NamedTextColor.GREEN)
                    .build()
            )
            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)

            player.server.broadcast(
                Component
                    .text()
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .content("${player.name} さんの${levelName}レベルが${level + 1}になりました！")
                    .build()
            )

            refreshLevelUnlockRecipe(player)

            refreshLevel(player, levelType)

            val skills = Main.skillDetails[levelType]
            if (skills != null) {
                for (skillDetail in skills) {
                    for (skillLevel in skillDetail.skillLevels) {
                        if (level + 1 == skillLevel.unlockLevel) {
                            unlockSkillMessage(player, skillDetail.name)
                        }
                    }
                }
            }
        }
    }

    fun refreshLevelUnlockRecipe(player: Player) {
        val landLevel = getTypeLevel(player, LevelType.LAND)
        val woodLevel = getTypeLevel(player, LevelType.WOOD)
        val stoneLevel = getTypeLevel(player, LevelType.STONE)
        val oreLevel = getTypeLevel(player, LevelType.ORE)

        if (landLevel >= 5) {
            unlockRecipe(player, "land_level_5")
        }
        if (landLevel >= 15) {
            unlockRecipe(player, "land_level_15")
        }
        if (landLevel >= 25) {
            unlockRecipe(player, "land_level_25")
        }

        if (woodLevel >= 5) {
            unlockRecipe(player, "wood_level_5")
        }
        if (woodLevel >= 15) {
            unlockRecipe(player, "wood_level_15")
        }
        if (woodLevel >= 25) {
            unlockRecipe(player, "wood_level_25")
        }

        if (stoneLevel >= 5) {
            unlockRecipe(player, "stone_level_5")
        }
        if (stoneLevel >= 15) {
            unlockRecipe(player, "stone_level_15")
        }
        if (stoneLevel >= 25) {
            unlockRecipe(player, "stone_level_25")
        }

        if (oreLevel >= 5) {
            unlockRecipe(player, "ore_level_5")
        }
        if (oreLevel >= 15) {
            unlockRecipe(player, "ore_level_15")
        }
        if (oreLevel >= 25) {
            unlockRecipe(player, "ore_level_25")
        }
    }

    private fun unlockSkillMessage(player: Player, skillName: String) {
        player.sendMessage(
            Component
                .text()
                .decoration(TextDecoration.BOLD, true)
                .color(NamedTextColor.AQUA)
                .content("スキル [$skillName] が解放されました！")
        )
    }

    fun safeAddItem(player: Player, itemStack: ItemStack) {
        val leftover = player.inventory.addItem(itemStack)

        for (overItemStack in leftover.values) {
            player.world.dropItem(player.location, overItemStack)
        }
    }

    fun getEnchantLevel(itemStack: ItemStack, id: String): Int? {
        val namespacedKey = NamespacedKey.fromString(id) ?: return  null
        val enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(namespacedKey)
        return itemStack.enchantments[enchantment]
    }

    fun getSkillLevel(player: Player, levelType: LevelType, skillId: String): Int? {
        val level = getTypeLevel(player, levelType)
        val skillDetail = Main.skillDetails[levelType] ?: return null
        val skill = skillDetail.firstOrNull { detail ->  detail.id == skillId} ?: return null
        val skillLevel = skill.skillLevels.indexOfLast{ skillLevel ->
            level >= skillLevel.unlockLevel
        } + 1
        return skillLevel
    }

    fun populateLoot(path: String, location: Location): Collection<ItemStack>? {
        val lootTablePath = NamespacedKey.fromString(path) ?: return null
        val loottable = Bukkit.getLootTable(lootTablePath) ?: return null

        val lootContext = LootContext.Builder(location).build()
        return loottable.populateLoot(Random(), lootContext)
    }

    fun addMana(player: Player, value: Int) {
        val mana = PlayerDatabase.getMana(player) ?: 0
        val addedMana = max(min(mana + value, 1000), 0)
        PlayerDatabase.setMana(player, addedMana)
        refreshManaBossBar(player)
    }
}
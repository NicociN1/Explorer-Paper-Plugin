package com.nicon.explorerPaper.utils

import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.definitions.Constants
import com.nicon.explorerPaper.skills.SkillData.SkillDetail
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
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
import java.util.Random
import java.util.UUID
import kotlin.math.max
import kotlin.math.min
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
     * 手に持っているアイテムを減らす
     */
    fun clearHandItems(player: Player, amount: Int? = null) {
        val inventory = player.inventory
        val handItem = inventory.itemInMainHand

        if (amount != null) {
            handItem.amount = max(handItem.amount - amount, 0)
            inventory.setItemInMainHand(handItem)
        } else {
            inventory.setItemInMainHand(ItemStack.empty())
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

    /**
     * サイドバーを更新
     */
    fun refreshSidebar(player: Player) {
        val board = player.scoreboard

        val objective: Objective = board.getObjective("sidebar") ?: board.registerNewObjective(
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
                .content("Gold: ${Utils.addCommaToNumber(PD.getGold(player) ?: 0)}")
                .build()
        )

        val amethystScore = objective.getScore("amethyst")
        amethystScore.score = -2
        amethystScore.customName(
            Component
                .text()
                .color(NamedTextColor.LIGHT_PURPLE)
                .content("Amethyst: ${Utils.addCommaToNumber(PD.getAmethyst(player) ?: 0)}")
                .build()
        )

        val blocksMinedScore = objective.getScore("blocksMined")
        blocksMinedScore.score = -3
        blocksMinedScore.customName(
            Component
                .text()
                .color(NamedTextColor.WHITE)
                .content("累計採掘回数: ${Utils.addCommaToNumber(PD.getBlocksMined(player) ?: 0)}")
                .build()
        )

        player.scoreboard = board
    }

    /**
     * レシピを解放させる
     */
    fun unlockRecipe(player: Player, recipeTag: String) {
        val unlockedRecipes = PD.getUnlockedRecipeTags(player)
        if (unlockedRecipes.contains(recipeTag)) return
        unlockedRecipes.add(recipeTag)
        PD.setUnlockedRecipeTags(player, unlockedRecipes)

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

        val levelName = getLevelTypeLabel(levelType)

        val level = getLevelWithType(player, levelType)
        val xp = getXpWithType(player, levelType)
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
        val maxMana = PD.getMaxMana(player)
        val currentMana = PD.getMana(player)
        val progress = (currentMana.toDouble() / maxMana).coerceIn(0.0, 1.0)

        manaBossBar.setTitle("マナ: ${currentMana}/${maxMana}")
        manaBossBar.progress = progress
    }

    fun getExpToNextLevel(level: Int): Int {
        val initial = 80.0

        if (level <= 1) {
            return initial.toInt()
        }

        val multiplier = 1.2
        var value = initial

        for (i in 2..level) {
            value *= multiplier
        }

        return value.roundToInt()
//        return 10
    }

    fun addExp(player: Player, levelType: LevelType, exp: Int) {
        val currentExp = getXpWithType(player, levelType)
        setXpWithType(player, levelType, currentExp + exp)
        refreshLevel(player, levelType)
        refreshExpBossBar(player, levelType)
    }

    fun getLevelWithType(player: Player, levelType: LevelType): Int {
        return when (levelType) {
            LevelType.LAND -> {
                PD.Level.getLandLevel(player)
            }

            LevelType.WOOD -> {
                PD.Level.getWoodLevel(player)
            }

            LevelType.STONE -> {
                PD.Level.getStoneLevel(player)
            }

            LevelType.ORE -> {
                PD.Level.getOreLevel(player)
            }
        }
    }

    fun getXpWithType(player: Player, levelType: LevelType): Int {
        return when (levelType) {
            LevelType.LAND -> {
                PD.Level.getLandXp(player)
            }

            LevelType.WOOD -> {
                PD.Level.getWoodXp(player)
            }

            LevelType.STONE -> {
                PD.Level.getStoneXp(player)
            }

            LevelType.ORE -> {
                PD.Level.getOreXp(player)
            }
        }
    }

    fun getLevelCapCountWithType(player: Player, levelType: LevelType): Int {
        return when (levelType) {
            LevelType.LAND -> {
                PD.Level.getLandLevelCapCount(player)
            }

            LevelType.WOOD -> {
                PD.Level.getWoodLevelCapCount(player)
            }

            LevelType.STONE -> {
                PD.Level.getStoneLevelCapCount(player)
            }

            LevelType.ORE -> {
                PD.Level.getOreLevelCapCount(player)
            }
        }
    }

    fun setLevelWithType(player: Player, levelType: LevelType, level: Int) {
        when (levelType) {
            LevelType.LAND -> {
                PD.Level.setLandLevel(player, level)
            }

            LevelType.WOOD -> {
                PD.Level.setWoodLevel(player, level)
            }

            LevelType.STONE -> {
                PD.Level.setStoneLevel(player, level)
            }

            LevelType.ORE -> {
                PD.Level.setOreLevel(player, level)
            }
        }
    }

    fun setXpWithType(player: Player, levelType: LevelType, xp: Int) {
        when (levelType) {
            LevelType.LAND -> {
                PD.Level.setLandXp(player, xp)
            }

            LevelType.WOOD -> {
                PD.Level.setWoodXp(player, xp)
            }

            LevelType.STONE -> {
                PD.Level.setStoneXp(player, xp)
            }

            LevelType.ORE -> {
                PD.Level.setOreXp(player, xp)
            }
        }
    }

    fun setLevelCapCountWithType(player: Player, levelType: LevelType, levelCap: Int) {
        when (levelType) {
            LevelType.LAND -> {
                PD.Level.setLandLevelCapCount(player, levelCap)
            }

            LevelType.WOOD -> {
                PD.Level.setWoodLevelCapCount(player, levelCap)
            }

            LevelType.STONE -> {
                PD.Level.setStoneLevelCapCount(player, levelCap)
            }

            LevelType.ORE -> {
                PD.Level.setOreLevelCapCount(player, levelCap)
            }
        }
    }

    fun getLevelTypeLabel(levelType: LevelType): String {
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
                "探鉱"
            }
        }
    }

    fun refreshLevel(player: Player, levelType: LevelType) {

        val xp = getXpWithType(player, levelType)
        val level = getLevelWithType(player, levelType)
        val requireExp = getExpToNextLevel(level)

        val levelCapCount = getLevelCapCountWithType(player, levelType)
        val levelCap = Main.levelCapDetails[levelType]?.getOrNull(levelCapCount - 1)?.level ?: Constants.initialLevelCap
        if (level >= levelCap) return

        if (xp >= requireExp) {
            setLevelWithType(player, levelType, level + 1)
            setXpWithType(player, levelType, xp - requireExp)

            val levelName = getLevelTypeLabel(levelType)
            player.sendMessage(
                Component
                    .text()
                    .content("${levelName}レベルアップ！ $level > ${level + 1}")
                    .color(NamedTextColor.GREEN)
                    .build()
            )
            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
            player.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1f, 1f)

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
                    for (i in 0..<skillDetail.skillLevels.size) {
                        val skillLevel = skillDetail.skillLevels[i]
                        if (level + 1 == skillLevel.unlockLevel) {
                            onSkillUnlock(player, levelType, skillDetail, i + 1)
                        }
                    }
                }
            }

            if (level + 1 >= levelCap) {
                player.sendMessage(
                    Component
                        .text()
                        .color(NamedTextColor.RED)
                        .decoration(TextDecoration.BOLD, true)
                        .content("レベルキャップに到達しました。レベルキャップを解放するまでレベルは上昇しません。レベルキャップの解放はメニューのレベルメニューから行うことができます。")
                        .build()
                )
            }
        }
    }

    fun refreshLevelUnlockRecipe(player: Player) {
        val landLevel = getLevelWithType(player, LevelType.LAND)
        val woodLevel = getLevelWithType(player, LevelType.WOOD)
        val stoneLevel = getLevelWithType(player, LevelType.STONE)

        if (landLevel >= 2) {
            unlockRecipe(player, "land_level_2")
        }
        if (landLevel >= 10) {
            unlockRecipe(player, "land_level_10")
        }
        if (landLevel >= 20) {
            unlockRecipe(player, "land_level_20")
        }

        if (woodLevel >= 2) {
            unlockRecipe(player, "wood_level_2")
        }
        if (woodLevel >= 10) {
            unlockRecipe(player, "wood_level_10")
        }
        if (woodLevel >= 20) {
            unlockRecipe(player, "wood_level_20")
        }

        if (stoneLevel >= 2) {
            unlockRecipe(player, "stone_level_2")
        }
        if (stoneLevel >= 10) {
            unlockRecipe(player, "stone_level_10")
        }
        if (stoneLevel >= 20) {
            unlockRecipe(player, "stone_level_20")
        }
    }

    private fun onSkillUnlock(player: Player, levelType: LevelType, skillDetail: SkillDetail, skillLevel: Int) {
        val levelTypeName = getLevelTypeLabel(levelType)
        player.sendMessage(
            Component
                .text()
                .decoration(TextDecoration.BOLD, true)
                .color(NamedTextColor.AQUA)
                .content("${levelTypeName}スキル [${skillDetail.name}] Lv.${skillLevel} が解放されました！")
        )
        player.playSound(player.location, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1f)

        val skill = skillDetail.skillLevels.getOrNull(skillLevel - 1) ?: return
        if (skill.states == null) return
        val skillStates = PD.getSkillStates(player)
        skillStates[skillDetail.id] = skill.states!!.last()
        PD.setSkillStates(player, skillStates)
    }

    fun safeAddItem(player: Player, itemStack: ItemStack) {
        val leftover = player.inventory.addItem(itemStack)

        for (overItemStack in leftover.values) {
            player.world.dropItem(player.location, overItemStack)
        }
    }

    fun getEnchantLevel(itemStack: ItemStack, id: String): Int? {
        val namespacedKey = NamespacedKey.fromString(id) ?: return null
        val enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(namespacedKey)
        return itemStack.enchantments[enchantment]
    }

    /**
     * @return 指定されたskillIdのスキルが見つからなかったらnull,解放されていなければ0を返す
     */
    fun getSkillLevel(player: Player, levelType: LevelType, skillId: String): Int? {
        val level = getLevelWithType(player, levelType)
        val skillDetail = Main.skillDetails[levelType] ?: return null
        val skill = skillDetail.firstOrNull { detail -> detail.id == skillId } ?: return null
        val skillLevel = skill.skillLevels.indexOfLast { skillLevel ->
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

    /**
     * @param player 追加するプレイヤー
     * @param value 追加する値
     */
    fun addMana(player: Player, value: Int) {
        val mana = PD.getMana(player)
        val addedMana = max(mana + value, 0)
        PD.setMana(player, addedMana)
        refreshManaBossBar(player)
    }
}
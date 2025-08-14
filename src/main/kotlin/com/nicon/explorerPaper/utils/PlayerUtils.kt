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
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import java.util.UUID
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round

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

        fun getUnlockedRecipes(player: Player): MutableList<String> {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            val unlockedRecipes = Json.decodeFromString<MutableList<String>>(playerData.unlockedRecipes)

            return unlockedRecipes
        }

        fun setUnlockedRecipes(player: Player, unlockedRecipes: MutableList<String>) {
            createIfNotExists(player)

            val unlockedRecipesTxt = Json.encodeToString(unlockedRecipes)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("unlocked_recipes", unlockedRecipesTxt)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getLevel(player: Player): Int? {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.level
        }

        fun setLevel(player: Player, level: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("level", level)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
        }

        fun getXp(player: Player): Int? {
            val playerData = Database.playerDao.queryForId(player.uniqueId.toString())
            return playerData?.xp
        }

        fun setXp(player: Player, xp: Int) {
            createIfNotExists(player)

            val updateBuilder = Database.playerDao.updateBuilder()
            updateBuilder.updateColumnValue("xp", xp)
            updateBuilder.where().eq("uuid", player.uniqueId.toString())
            updateBuilder.update()
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
        goldScore.score = 2
        goldScore.customName(
            Component
                .text()
                .color(NamedTextColor.GOLD)
                .content("Gold: ${PlayerDatabase.getGold(player) ?: 0}")
                .build()
        )

        val amethystScore = objective.getScore("amethyst")
        amethystScore.score = 1
        amethystScore.customName(
            Component
                .text()
                .color(NamedTextColor.LIGHT_PURPLE)
                .content("Amethyst: ${PlayerDatabase.getAmethyst(player) ?: 0}")
                .build()
        )

        val blocksMinedScore = objective.getScore("blocksMined")
        blocksMinedScore.score = 0
        blocksMinedScore.customName(
            Component
                .text()
                .color(NamedTextColor.WHITE)
                .content("累計採掘回数: ${PlayerDatabase.getBlocksMined(player) ?: 0}")
                .build()
        )

        player.scoreboard = board
    }

    /**
     * レシピを解放させる
     */
    fun unlockRecipe(player: Player, recipeId: String, recipeName: String) {
        val unlockedRecipes = PlayerDatabase.getUnlockedRecipes(player)
        if (unlockedRecipes.contains(recipeId)) return
        unlockedRecipes.add(recipeId)
        PlayerDatabase.setUnlockedRecipes(player, unlockedRecipes)

        player.sendMessage(
            Component
                .text()
                .color(NamedTextColor.LIGHT_PURPLE)
                .content("[${recipeName}] のレシピを開放しました！")
                .build()
        )
        player.playSound(player.location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f)
    }

    val expBossBars = mutableMapOf<UUID, BossBar>()
    /**
     * ボスバーを更新
     */
    fun refreshBossBar(player: Player) {
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

        val level = PlayerDatabase.getLevel(player) ?: 0
        val xp = PlayerDatabase.getXp(player) ?: 0
        val requireXp = getExpToNextLevel(level)
        val xpProgress = xp.toDouble() / requireXp

        expBossBar.setTitle("Lv.${level} ${xp}/${requireXp}")
        expBossBar.progress = xpProgress
    }

    fun getExpToNextLevel(level: Int): Int {
        val base = 1.5
        val initial = 20.0
        return round(initial * base.pow((level - 1).toDouble())).toInt()
    }

    fun addExp(player: Player, exp: Int) {
        val currentExp = PlayerDatabase.getXp(player) ?: 0
        PlayerDatabase.setXp(player, currentExp + exp)
        refreshLevel(player)
        refreshBossBar(player)
    }

    fun refreshLevel(player: Player) {
        val xp = PlayerDatabase.getXp(player) ?: 0
        val level = PlayerDatabase.getLevel(player) ?: 0
        val requireExp = getExpToNextLevel(level)

        if (xp >= requireExp) {
            PlayerDatabase.setLevel(player, level + 1)
            PlayerDatabase.setXp(player, xp - requireExp)

            player.sendMessage(
                Component
                    .text()
                    .content("レベルが上がりました！ $level > ${level + 1}")
                    .color(NamedTextColor.GREEN)
                    .build()
            )
            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)

            player.server.broadcast(
                Component
                    .text()
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .content("${player.name} さんのレベルが${level + 1}になりました！")
                    .build()
            )

            refreshRecipeUnlock(player)
        }
    }

    fun refreshRecipeUnlock(player: Player) {
        val level = PlayerDatabase.getLevel(player) ?: return

        if (level >= 5) {
            unlockRecipe(player, "stone_pickaxe", "石のツルハシ")
            unlockRecipe(player, "stone_axe", "石の斧")
            unlockRecipe(player, "stone_shovel", "石のシャベル")
        }
        if (level >= 10) {
            unlockRecipe(player, "iron_pickaxe", "鉄のツルハシ")
            unlockRecipe(player, "iron_axe", "鉄の斧")
            unlockRecipe(player, "iron_shovel", "鉄のシャベル")
        }
        if (level >= 20) {
            unlockRecipe(player, "diamond_pickaxe", "ダイヤモンドのツルハシ")
            unlockRecipe(player, "diamond_axe", "ダイヤモンドの斧")
            unlockRecipe(player, "diamond_shovel", "ダイヤモンドのシャベル")
        }
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
}
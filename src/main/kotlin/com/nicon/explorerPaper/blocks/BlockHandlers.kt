package com.nicon.explorerPaper.blocks

import com.nicon.explorerPaper.skills.SkillEffects
import com.nicon.explorerPaper.utils.GameUtils
import com.nicon.explorerPaper.utils.PD
import com.nicon.explorerPaper.utils.PlayerUtils
import com.nicon.explorerPaper.utils.PlayerUtils.LevelType
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.Random

object BlockHandlers {
    fun onBreak(player: Player, block: Block) {
        val blockDetail = GameUtils.getBlockDetail(block) ?: return

        val blocksMined = PD.getBlocksMined(player)
        PD.setBlocksMined(player, blocksMined + 1)

        if (blockDetail.lootTable != null) {
            when (blockDetail.levelType) {
                LevelType.ORE -> {
                    val smeltLevel = PlayerUtils.getSkillLevel(player, blockDetail.levelType!!, "ore_auto_smelting")
                    val autoSmeltingState = PD.getSkillStates(player)["ore_auto_smelting"]
                    val smelted = blockDetail.smeltedLootTable
                    val normal = blockDetail.lootTable!!

                    val lootTable = when {
                        smeltLevel == null || smeltLevel < 1 || smelted == null || autoSmeltingState == "INACTIVE" -> normal
                        else -> smelted
                    }

                    lootItem(player, lootTable)
                }
                else -> {
                    lootItem(player, blockDetail.lootTable!!)
                }
            }
        }

        if (blockDetail.levelType != null && blockDetail.xp != null) {
            PlayerUtils.addExp(player, blockDetail.levelType!!, blockDetail.xp!!)
        }

        when (block.type) {
            Material.OAK_LOG -> {
                PlayerUtils.unlockRecipe(player, "wood_mined")
            }

            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE -> {
                PlayerUtils.unlockRecipe(player, "coal_ore_mined")
            }

            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE -> {
                PlayerUtils.unlockRecipe(player, "copper_ore_mined")
            }

            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE -> {
                PlayerUtils.unlockRecipe(player, "iron_ore_mined")
            }

            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE -> {
                PlayerUtils.unlockRecipe(player, "redstone_ore_mined")
            }

            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE -> {
                PlayerUtils.unlockRecipe(player, "lapis_ore_mined")
            }

            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE -> {
                PlayerUtils.unlockRecipe(player, "gold_ore_mined")
            }

            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE -> {
                PlayerUtils.unlockRecipe(player, "diamond_ore_mined")
            }

            Material.DEEPSLATE -> {
                PlayerUtils.unlockRecipe(player, "deepslate_mined")
            }

            else -> {}
        }

        if (blockDetail.gold != null) {
            val goldDrop = blockDetail.gold!!
            val goldRushLevel = PlayerUtils.getSkillLevel(player, LevelType.LAND, "land_gold_rush")
            val goldMultiplier = if (blockDetail.levelType == LevelType.LAND) when (goldRushLevel) {
                1 -> 3
                2 -> 6
                else -> 1
            } else 1
            if (Math.random() <= goldDrop.chance * goldMultiplier) {
                val goldValue = if (goldDrop.min < goldDrop.max) (Random().nextInt(goldDrop.min, goldDrop.max) + 1) * goldMultiplier else goldDrop.min
                val spawnLocation = block.location.add(0.5, 0.5, 0.5)
                GameUtils.spawnGold(goldValue, spawnLocation)
                player.playSound(spawnLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1f)
                val currentGold = PD.getGold(player)
                PD.setGold(player, currentGold + goldValue)
            }
        }
        if (blockDetail.amethyst != null) {
            val amethystDrop = blockDetail.amethyst!!
            if (Math.random() <= amethystDrop.chance) {
                val random = Random()
                val amethystValue = random.nextInt(amethystDrop.min, amethystDrop.max + 1)
                val spawnLocation = block.location.add(0.5, 0.5, 0.5)
                GameUtils.spawnAmethyst(amethystValue, spawnLocation)
                player.playSound(spawnLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                val currentAmethyst = PD.getAmethyst(player)
                PD.setAmethyst(player, currentAmethyst + amethystValue)
            }
        }

        SkillEffects.handleItemDrop(player, block)
    }

    fun lootItem(player: Player, lootTablePath: String) {
        val itemStacks =
            PlayerUtils.populateLoot(lootTablePath, player.location) ?: return

        for (itemStack in itemStacks) {
            PlayerUtils.safeAddItem(player, itemStack)
        }
    }
}
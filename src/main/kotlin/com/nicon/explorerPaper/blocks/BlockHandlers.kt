package com.nicon.explorerPaper.blocks

import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.definitions.Constants
import com.nicon.explorerPaper.skills.SkillEffects
import com.nicon.explorerPaper.utils.GameUtils
import com.nicon.explorerPaper.utils.PlayerUtils
import com.nicon.explorerPaper.utils.PlayerUtils.LevelType
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.Random

object BlockHandlers {
    fun onBreak(player: Player, block: Block) {
        val blockId = block.type.key.toString()
        val blockDetail = GameUtils.getBlockDetail(block) ?: return

        val blocksMined = PlayerUtils.PlayerDatabase.getBlocksMined(player) ?: 0
        PlayerUtils.PlayerDatabase.setBlocksMined(player, blocksMined + 1)

        if (blockDetail.lootTable != null) {
            when (blockDetail.levelType) {
                LevelType.WOOD -> {
                    val woodDoubleDropLevel = PlayerUtils.getSkillLevel(player, LevelType.WOOD, "wood_double_drop")
                    val doubleDropChance = when (woodDoubleDropLevel) {
                        1 -> 0.2
                        2 -> 0.6
                        else -> 0.0
                    }
                    if (Math.random() <= doubleDropChance) {
                        repeat(2) {
                            lootItem(player, blockDetail.lootTable!!)
                        }
                    } else {
                        lootItem(player, blockDetail.lootTable!!)
                    }
                }
                LevelType.ORE -> {
                    val smeltLevel = PlayerUtils.getSkillLevel(player, blockDetail.levelType!!, "ore_auto_smelting")
                    val autoSmeltingState = PlayerUtils.PlayerDatabase.getSkillStates(player)["ore_auto_smelting"]
                    val smelted = blockDetail.smeltedLootTable
                    val normal = blockDetail.lootTable!!

                    val lootTable = when {
                        smeltLevel == null || smeltLevel < 1 || smelted == null || autoSmeltingState == "INACTIVE" -> normal
                        smeltLevel == 1 && Constants.HIGH_GRADE_ORES.contains(block.type) -> normal
                        else -> smelted
                    }

                    val dropBonusLevel = PlayerUtils.getSkillLevel(player, blockDetail.levelType!!, "ore_drop_bonus")

                    val isApplyBonus = when (dropBonusLevel) {
                        1 -> Math.random() <= 0.25
                        2 -> Math.random() <= 0.5
                        else -> false
                    }

                    val repeatCount = if (isApplyBonus) Random().nextInt(3) + 1 else 1

                    repeat(repeatCount) {
                        lootItem(player, lootTable)
                    }
                }
                else -> {
                    lootItem(player, blockDetail.lootTable!!)
                }
            }
        }

        if (blockDetail.levelType != null && blockDetail.xp != null) {
            PlayerUtils.addExp(player, blockDetail.levelType!!, blockDetail.xp!!)
        }

        when (blockId) {
            "minecraft:oak_log" -> {
                PlayerUtils.unlockRecipe(player, "wood_mined")
            }

            "minecraft:coal_ore" -> {
                PlayerUtils.unlockRecipe(player, "coal_ore_mined")
            }

            "minecraft:copper_ore" -> {
                PlayerUtils.unlockRecipe(player, "copper_ore_mined")
            }

            "minecraft:iron_ore" -> {
                PlayerUtils.unlockRecipe(player, "iron_ore_mined")
            }

            "minecraft:redstone_ore" -> {
                PlayerUtils.unlockRecipe(player, "redstone_ore_mined")
            }

            "minecraft:lapis_ore" -> {
                PlayerUtils.unlockRecipe(player, "lapis_ore_mined")
            }

            "minecraft:gold_ore" -> {
                PlayerUtils.unlockRecipe(player, "gold_ore_mined")
            }

            "minecraft:diamond_ore" -> {
                PlayerUtils.unlockRecipe(player, "diamond_ore_mined")
            }
        }

        if (blockDetail.gold != null) {
            val goldDrop = blockDetail.gold!!
            val goldRushLevel = PlayerUtils.getSkillLevel(player, LevelType.LAND, "land_gold_rush")
            val goldMultiplier = if (blockDetail.levelType == LevelType.LAND) when (goldRushLevel) {
                1 -> 2
                2 -> 4
                else -> 1
            } else 1
            if (Math.random() <= goldDrop.chance * goldMultiplier) {
                val random = Random()
                val goldValue = (random.nextInt(goldDrop.min, goldDrop.max) + 1) * goldMultiplier
                val spawnLocation = block.location.add(0.5, 0.5, 0.5)
                GameUtils.spawnGold(goldValue, spawnLocation)
                player.playSound(spawnLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                val currentGold = PlayerUtils.PlayerDatabase.getGold(player) ?: 0
                PlayerUtils.PlayerDatabase.setGold(player, currentGold + goldValue)
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
                val currentAmethyst = PlayerUtils.PlayerDatabase.getAmethyst(player) ?: 0
                PlayerUtils.PlayerDatabase.setAmethyst(player, currentAmethyst + amethystValue)
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
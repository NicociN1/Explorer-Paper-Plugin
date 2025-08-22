package com.nicon.explorerPaper.skills

import com.nicon.explorerPaper.definitions.ItemDefinitions
import com.nicon.explorerPaper.utils.GameUtils
import com.nicon.explorerPaper.utils.PD
import com.nicon.explorerPaper.utils.PlayerUtils
import com.nicon.explorerPaper.utils.PlayerUtils.LevelType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.Random

object SkillEffects {
    fun handleAreaMining(player: Player, block: Block) {
        val blockDetail = GameUtils.getBlockDetail(block) ?: return

        when (blockDetail.levelType) {
            LevelType.LAND -> {
                val miningLevel = PlayerUtils.getSkillLevel(player, blockDetail.levelType!!, "land_area_mining") ?: return
                val miningState = PD.getSkillStates(player)["land_area_mining"]
                val skillDetail = GameUtils.getSkillDetail(blockDetail.levelType!!, "land_area_mining")
                if (skillDetail != null && !player.isSneaking) {
                    when (miningState) {
                        "1x2" -> {
                            if (miningLevel >= 1) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 1, 2, 5)
                            }
                        }
                        "5x2" -> {
                            if (miningLevel >= 2) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 5, 2, 5)
                            }
                        }
                        "7x3" -> {
                            if (miningLevel >= 3) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 7, 3, 4)
                            }
                        }
                        "9x3" -> {
                            if (miningLevel >= 4) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 9, 3, 4)
                            }
                        }
                        "11x4" -> {
                            if (miningLevel >= 5) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 11, 4, 3)
                            }
                        }
                        "13x4" -> {
                            if (miningLevel >= 6) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 13, 4, 3)
                            }
                        }
                    }
                }
            }

            LevelType.WOOD -> {
                val miningLevel = PlayerUtils.getSkillLevel(player, blockDetail.levelType!!, "wood_area_mining") ?: return
                val miningState = PD.getSkillStates(player)["wood_area_mining"]
                val skillDetail = GameUtils.getSkillDetail(blockDetail.levelType!!, "wood_area_mining")
                if (skillDetail != null && !player.isSneaking) {
                    when (miningState) {
                        "x3" -> {
                            if (miningLevel >= 1) {
                                GameUtils.countMining(player, block, arrayOf(Material.OAK_LOG), 1, 5)
                            }
                        }
                        "x8" -> {
                            if (miningLevel >= 2) {
                                GameUtils.countMining(player, block, arrayOf(Material.OAK_LOG), 7, 5)
                            }
                        }
                        "x16" -> {
                            if (miningLevel >= 3) {
                                GameUtils.countMining(player, block, arrayOf(Material.OAK_LOG), 15, 4)
                            }
                        }
                        "x32" -> {
                            if (miningLevel >= 4) {
                                GameUtils.countMining(player, block, arrayOf(Material.OAK_LOG), 31, 4)
                            }
                        }
                        "x64" -> {
                            if (miningLevel >= 5) {
                                GameUtils.countMining(player, block, arrayOf(Material.OAK_LOG), 63, 3)
                            }
                        }
                        "x128" -> {
                            if (miningLevel >= 6) {
                                GameUtils.countMining(player, block, arrayOf(Material.OAK_LOG), 127, 3)
                            }
                        }
                    }
                }
            }

            LevelType.STONE -> {
                val miningLevel = PlayerUtils.getSkillLevel(player, blockDetail.levelType!!, "stone_area_mining") ?: return
                val miningState = PD.getSkillStates(player)["stone_area_mining"]
                val skillDetail = GameUtils.getSkillDetail(blockDetail.levelType!!, "stone_area_mining")
                if (skillDetail != null && !player.isSneaking) {
                    when (miningState) {
                        "1x2" -> {
                            if (miningLevel >= 1) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 1, 2, 5)
                            }
                        }
                        "3x3" -> {
                            if (miningLevel >= 2) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 3, 3, 5)
                            }
                        }
                        "5x5" -> {
                            if (miningLevel >= 3) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 5, 5, 4)
                            }
                        }
                        "7x7" -> {
                            if (miningLevel >= 4) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 7, 7, 4)
                            }
                        }
                        "9x9" -> {
                            if (miningLevel >= 5) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 9, 9, 3)
                            }
                        }
                        "11x11" -> {
                            if (miningLevel >= 6) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 11, 11, 3)
                            }
                        }
                    }
                }
            }

            LevelType.ORE -> {
                val miningLevel = PlayerUtils.getSkillLevel(player, blockDetail.levelType!!, "ore_area_mining") ?: return
                val miningState = PD.getSkillStates(player)["ore_area_mining"]
                val skillDetail = GameUtils.getSkillDetail(blockDetail.levelType!!, "ore_area_mining")
                if (skillDetail != null && !player.isSneaking) {
                    when (miningState) {
                        "x3" -> {
                            if (miningLevel >= 1) {
                                GameUtils.countMining(player, block, arrayOf(block.type), 2, 5)
                            }
                        }
                        "x6" -> {
                            if (miningLevel >= 2) {
                                GameUtils.countMining(player, block, arrayOf(block.type), 5, 5)
                            }
                        }
                        "x10" -> {
                            if (miningLevel >= 3) {
                                GameUtils.countMining(player, block, arrayOf(block.type), 9, 4)
                            }
                        }
                        "x16" -> {
                            if (miningLevel >= 4) {
                                GameUtils.countMining(player, block, arrayOf(block.type), 15, 4)
                            }
                        }
                        "x32" -> {
                            if (miningLevel >= 5) {
                                GameUtils.countMining(player, block, arrayOf(block.type), 31, 3)
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }

    fun handleItemDrop(player: Player, block: Block) {
        val blockDetail = GameUtils.getBlockDetail(block) ?: return
        val blockMaterial = block.type

        when (blockDetail.levelType) {
            LevelType.LAND -> {
                val antiquesLevel = PlayerUtils.getSkillLevel(player, LevelType.LAND, "land_drop_antiques")
                if (antiquesLevel != null && antiquesLevel >= 1) {
                    val antiquesChance = 1.0 / 80.0
                    if (Math.random() <= antiquesChance) {
                        val itemStacks = PlayerUtils.populateLoot("ex:skills/antiques", block.location)
                        if (itemStacks != null) {
                            for (itemStack in itemStacks) {
                                PlayerUtils.safeAddItem(player, itemStack)
                            }
                            onRareDrop(player, "骨董品", antiquesChance)
                        }
                    }
                }

                val explorerChestChance = 1.0 / 300.0
                if (Math.random() <= explorerChestChance) {
                    val explorersChest = ItemDefinitions.getExplorersChest()
                    PlayerUtils.safeAddItem(player, explorersChest)
                    onRareDrop(player, "探検家のチェスト", explorerChestChance)
                }
            }

            LevelType.WOOD -> {
                if (blockMaterial == Material.OAK_LOG) {
                    val sapChance = 1.0 / 80.0
                    if (Math.random() <= sapChance) {
                        val sap = ItemDefinitions.getSap()
                        PlayerUtils.safeAddItem(player, sap)
                        onRareDrop(player, "樹液", sapChance)
                    }
                }

                val fruitsChestLevel = PlayerUtils.getSkillLevel(player, LevelType.WOOD, "wood_fruits_chest")
                if (fruitsChestLevel != null && fruitsChestLevel >= 1) {
                    val fruitsChestChance = 1.0 / 150.0
                    val fruitsChest = ItemDefinitions.getFruitsChest()
                    if (Math.random() <= fruitsChestChance) {
                        PlayerUtils.safeAddItem(player, fruitsChest)
                        onRareDrop(player, "森の恵みチェスト", fruitsChestChance)
                    }
                }
            }

            LevelType.STONE -> {
                val oreDropLevel = PlayerUtils.getSkillLevel(player, LevelType.STONE, "stone_ore_drop")
                if (oreDropLevel != null && oreDropLevel >= 1) {
                    val oreDropChance = 1.0 / 90.0
                    if (Math.random() <= oreDropChance) {
                        val itemStacks = PlayerUtils.populateLoot("ex:skills/ores", block.location)
                        if (itemStacks != null) {
                            for (itemStack in itemStacks) {
                                PlayerUtils.safeAddItem(player, itemStack)
                            }
                        }
                        onSkillTriggered(player, "鉱石混入")
                    }
                }

                val boneChance = 1.0 / 400.0
                if (Math.random() <= boneChance) {
                    val boneBlock = ItemStack.of(Material.BONE_BLOCK)
                    boneBlock.editMeta { meta ->
                        meta.displayName(
                            Component
                                .text()
                                .decoration(TextDecoration.ITALIC, false)
                                .content("化石ブロック")
                                .build()
                        )
                    }
                    PlayerUtils.safeAddItem(player, boneBlock)
                    onRareDrop(player, "化石ブロック", boneChance)
                }

                if (blockMaterial == Material.DEEPSLATE) {
                    val ancientDebrisChance = 1.0 / 200.0
                    if (Math.random() <= ancientDebrisChance) {
                        val ancientDebris = ItemStack.of(Material.ANCIENT_DEBRIS)
                        PlayerUtils.safeAddItem(player, ancientDebris)
                        PlayerUtils.unlockRecipe(player, "get_ancient_debris")
                        onRareDrop(player, "古代の残骸", ancientDebrisChance)
                    }
                }
            }

            LevelType.ORE -> {
                val randomBuffLevel = PlayerUtils.getSkillLevel(player, LevelType.ORE, "ore_random_buff")
                if (randomBuffLevel != null && randomBuffLevel >= 1) {
                    val effectChance = 1.0 / 20.0
                    if (Math.random() <= effectChance) {
                        giveRandomEffect(player)
                        onSkillTriggered(player, "鉱夫の祝福")
                    }
                }
            }

            else -> {

            }
        }
    }

    private fun onRareDrop(player: Player, itemName: String, chance: Double) {
        player.playSound(player.location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f)
        player.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1f)
        player.sendMessage(
            Component
                .text()
                .color(NamedTextColor.LIGHT_PURPLE)
                .content("[RareDrop!] $itemName (${"%.4g".format(chance * 100)}%)")
                .build()
        )
    }
    private fun onSkillTriggered(player: Player, skillName: String) {
        player.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f)
        player.sendMessage(
            Component
                .text()
                .color(NamedTextColor.AQUA)
                .content("[Skill発動!] $skillName")
                .build()
        )
    }

    private fun giveRandomEffect(player: Player) {
        val potionEffect: PotionEffect = when (Random().nextInt(4)) {
            0 -> {
                PotionEffect(PotionEffectType.HASTE, 20 * 20, 1, false, true, true)
            }
            1 -> {
                PotionEffect(PotionEffectType.SPEED, 20 * 20, 2, false, true, true)
            }
            2 -> {
                PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 20, 2, false, true, true)
            }
            3 -> {
                PotionEffect(PotionEffectType.NIGHT_VISION, 30 * 20, 0, false, true, true)
            }
            else -> null
        } ?: return

        player.addPotionEffect(potionEffect)
    }
}
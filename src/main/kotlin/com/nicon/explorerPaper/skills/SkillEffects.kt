package com.nicon.explorerPaper.skills

import com.nicon.explorerPaper.skills.SkillData.SkillDetail
import com.nicon.explorerPaper.utils.GameUtils
import com.nicon.explorerPaper.utils.ItemUtils
import com.nicon.explorerPaper.utils.PlayerUtils
import com.nicon.explorerPaper.utils.PlayerUtils.LevelType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.Random

object SkillEffects {
    fun handleAreaMining(player: Player, block: Block) {
        val blockDetail = GameUtils.getBlockDetail(block) ?: return
        val mana = PlayerUtils.PlayerDatabase.getMana(player) ?: 0

        when (blockDetail.levelType) {
            LevelType.LAND -> {
                val miningLevel = PlayerUtils.getSkillLevel(player, blockDetail.levelType!!, "land_area_mining")
                val miningState = PlayerUtils.PlayerDatabase.getSkillStates(player)["land_area_mining"]
                val skillDetail = GameUtils.getSkillDetail(blockDetail.levelType!!, "land_area_mining")
                if (skillDetail != null) {
                    when (miningLevel) {
                        1 -> {
                            if (miningState == "3x3" || miningState == null) {
                                if (useMana(player, skillDetail, 0, mana)) {
                                    GameUtils.areaMining(player, block, blockDetail.levelType!!, 3)
                                }
                            }
                        }

                        2 -> when (miningState) {
                            "3x3" -> if (useMana(player, skillDetail, 0, mana)) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 3)
                            }
                            
                            null, "5x5" -> if (useMana(player, skillDetail, 1, mana)) {
                                GameUtils.areaMining(player, block, blockDetail.levelType!!, 5)
                            }
                        }
                    }
                }
            }

            LevelType.WOOD -> {
                val miningLevel = PlayerUtils.getSkillLevel(player, blockDetail.levelType!!, "wood_area_mining")
                val miningState = PlayerUtils.PlayerDatabase.getSkillStates(player)["wood_area_mining"]
                val skillDetail = GameUtils.getSkillDetail(blockDetail.levelType!!, "wood_area_mining")
                if (skillDetail != null) {
                    when (miningLevel) {
                        1 -> {
                            if (miningState == "TRUNK_ONLY" || miningState == null) {
                                if (useMana(player, skillDetail, 0, mana)) {
                                    GameUtils.allMining(player, block, arrayOf(Material.OAK_LOG))
                                }
                            }
                        }

                        2 -> {
                            when (miningState) {
                                "TRUNK_ONLY" -> {
                                    if (useMana(player, skillDetail, 0, mana)) {
                                        GameUtils.allMining(player, block, arrayOf(Material.OAK_LOG))
                                    }
                                }
                                
                                null, "WOOD_ALL" -> {
                                    if (useMana(player, skillDetail, 1, mana)) {
                                        GameUtils.allMining(player, block, arrayOf(Material.OAK_LOG, Material.OAK_LEAVES))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            LevelType.STONE -> {
                val miningLevel = PlayerUtils.getSkillLevel(player, blockDetail.levelType!!, "stone_area_mining")
                val miningState = PlayerUtils.PlayerDatabase.getSkillStates(player)["stone_area_mining"]
                val skillDetail = GameUtils.getSkillDetail(blockDetail.levelType!!, "stone_area_mining")
                if (skillDetail != null) {
                    when (miningLevel) {
                        1 -> {
                            if (miningState == "3x3" || miningState == null) {
                                if (useMana(player, skillDetail, 0, mana)) {
                                    GameUtils.areaMining(player, block, blockDetail.levelType!!, 3)
                                }
                            }
                        }

                        2 -> {
                            when (miningState) {
                                "3x3" -> {
                                    if (useMana(player, skillDetail, 0, mana)) {
                                        GameUtils.areaMining(player, block, blockDetail.levelType!!, 3)
                                    }
                                }
                                
                                null, "5x5" -> {
                                    if (useMana(player, skillDetail, 1, mana)) {
                                        GameUtils.areaMining(player, block, blockDetail.levelType!!, 5)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            LevelType.ORE -> {
                val miningLevel = PlayerUtils.getSkillLevel(player, blockDetail.levelType!!, "ore_area_mining")
                val miningState = PlayerUtils.PlayerDatabase.getSkillStates(player)["ore_area_mining"]
                val skillDetail = GameUtils.getSkillDetail(blockDetail.levelType!!, "ore_area_mining")
                if (skillDetail != null) {
                    if (miningState == "ACTIVE" || miningState == null) {
                        when (miningLevel) {
                            1 -> {
                                if (useMana(player, skillDetail, 0, mana)) {
                                    GameUtils.allMining(player, block, arrayOf(block.type), 12)
                                }
                            }

                            2 -> {
                                if (useMana(player, skillDetail, 1, mana)) {
                                    GameUtils.allMining(player, block, arrayOf(block.type))
                                }
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
                handleLoot(
                    player,
                    block,
                    blockDetail.levelType!!,
                    "land_drop_antiques",
                    mapOf(1 to 0.01, 2 to 0.04)
                ) { _, _ ->
                    val itemStacks = PlayerUtils.populateLoot("ex:skills/antiques", block.location)
                    if (itemStacks != null) {
                        for (itemStack in itemStacks) {
                            PlayerUtils.safeAddItem(player, itemStack)
                        }
                    }
                }


                handleLoot(
                    player,
                    block,
                    blockDetail.levelType!!,
                    "land_drop_explorers_chest",
                    mapOf(1 to 0.005, 2 to 0.02)
                ) { _, _ ->
                    val explorersChest = ItemStack.of(Material.CHEST)
                    explorersChest.editMeta { meta ->
                        meta.displayName(
                            Component
                                .text()
                                .decoration(TextDecoration.ITALIC, false)
                                .content("探検家のチェスト")
                                .build()
                        )
                        meta.lore(
                            listOf(
                                Component
                                    .text()
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(NamedTextColor.WHITE)
                                    .content("手に持って右クリックで開封")
                                    .build()
                            )
                        )
                    }
                    ItemUtils.addCustomTag(explorersChest, "explorersChest", "true")
                    PlayerUtils.safeAddItem(player, explorersChest)
                }
            }

            LevelType.WOOD -> {
                if (blockMaterial == Material.OAK_LOG) {
                    handleLoot(
                        player,
                        block,
                        blockDetail.levelType!!,
                        "wood_drop_sap",
                        mapOf(1 to 0.02, 2 to 0.06)
                    ) { _, _ ->
                        val sap = ItemStack.of(Material.HONEY_BOTTLE)
                        sap.editMeta { meta ->
                            meta.displayName(
                                Component
                                    .text()
                                    .decoration(TextDecoration.ITALIC, false)
                                    .content("樹液")
                                    .build()
                            )
                            meta.lore(
                                listOf(
                                    Component
                                        .text()
                                        .decoration(TextDecoration.ITALIC, false)
                                        .color(NamedTextColor.WHITE)
                                        .content("手に持って右クリックで開封")
                                        .build()
                                )
                            )
                        }
                        PlayerUtils.safeAddItem(player, sap)
                    }
                } else if (blockMaterial == Material.OAK_LEAVES) {
                    handleLoot(
                        player,
                        block,
                        blockDetail.levelType!!,
                        "wood_drop_fruits",
                        mapOf(1 to 0.02, 2 to 0.06)
                    ) { _, _ ->
                        val itemStacks = PlayerUtils.populateLoot("ex:skills/fruits", block.location)

                        if (itemStacks != null) {
                            for (itemStack in itemStacks) {
                                PlayerUtils.safeAddItem(player, itemStack)
                            }
                        }
                    }
                }
            }

            LevelType.STONE -> {
                handleLoot(
                    player,
                    block,
                    blockDetail.levelType!!,
                    "stone_drop_ore",
                    mapOf(1 to 0.02, 2 to 0.06)
                ) { _, _ ->
                    val itemStacks = PlayerUtils.populateLoot("ex:skills/ores", block.location)

                    if (itemStacks != null) {
                        for (itemStack in itemStacks) {
                            PlayerUtils.safeAddItem(player, itemStack)
                        }
                    }
                }

                handleLoot(
                    player,
                    block,
                    blockDetail.levelType!!,
                    "stone_drop_bone",
                    mapOf(1 to 0.005, 2 to 0.02)
                ) { _, _ ->
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
                }

                if (blockMaterial == Material.DEEPSLATE) {
                    handleLoot(
                        player,
                        block,
                        blockDetail.levelType!!,
                        "stone_drop_ancient_debris",
                        mapOf(1 to 0.002, 2 to 0.006)
                    ) { _, _ ->
                        val ancientDebris = ItemStack.of(Material.ANCIENT_DEBRIS)
                        PlayerUtils.safeAddItem(player, ancientDebris)
                    }
                }
            }

            LevelType.ORE -> {
                handleLoot(
                    player,
                    block,
                    blockDetail.levelType!!,
                    "ore_random_buff",
                    mapOf(1 to 0.08, 2 to 0.08)
                ) { _, _ ->
                    val level = PlayerUtils.getSkillLevel(player, blockDetail.levelType!!, "ore_random_buff") ?: return@handleLoot
                    giveRandomEffect(player, level)
                }
            }

            else -> {

            }
        }
    }

    private fun handleLoot(
        player: Player,
        block: Block,
        skillType: LevelType,
        skillId: String,
        dropChances: Map<Int, Double>,
        dropAction: (Player, Block) -> Unit
    ) {
        val level = PlayerUtils.getSkillLevel(player, skillType, skillId)
        if (level != null && level >= 1) {
            val dropChance = dropChances[level] ?: 0.0
            if (Math.random() <= dropChance) {
                dropAction(player, block)
            }
        }
    }

    private fun giveRandomEffect(player: Player, skillLevel: Int) {
        val potionEffect: PotionEffect = when (Random().nextInt(4)) {
            0 -> {
                PotionEffect(PotionEffectType.HASTE, 20, if (skillLevel == 1) 0 else 1, false, true)
            }
            1 -> {
                PotionEffect(PotionEffectType.SPEED, 20, if (skillLevel == 1) 1 else 2, false, true)
            }
            2 -> {
                PotionEffect(PotionEffectType.JUMP_BOOST, 20, if (skillLevel == 1) 1 else 2, false, true)
            }
            3 -> {
                PotionEffect(PotionEffectType.NIGHT_VISION, if (skillLevel == 1) 20 else 60, 0, false, true)
            }
            else -> null
        } ?: return

        player.addPotionEffect(potionEffect)
    }

    private fun useMana(player: Player, skillDetail: SkillDetail, index: Int, mana: Int): Boolean {
        val skillLevel = skillDetail.skillLevels.getOrNull(index)
        if (skillLevel != null && skillLevel.manaCost != null) {
            if (mana >= skillLevel.manaCost!!) {
                PlayerUtils.addMana(player, -skillLevel.manaCost!!)
                return true
            } else {
                player.sendMessage(
                    Component
                        .text()
                        .color(NamedTextColor.RED)
                        .content("マナが足りません")
                )
            }
        }
        return false
    }
}
package com.nicon.explorerPaper.skills

import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.utils.InventoryUI
import com.nicon.explorerPaper.utils.InventoryUI.*
import com.nicon.explorerPaper.utils.PlayerUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SkillScreen {
    fun openLevelsMenu(inventoryUI: InventoryUI, player: Player) {
        val landLevel = PlayerUtils.getTypeLevel(player, PlayerUtils.LevelType.LAND)
        val woodLevel = PlayerUtils.getTypeLevel(player, PlayerUtils.LevelType.WOOD)
        val stoneLevel = PlayerUtils.getTypeLevel(player, PlayerUtils.LevelType.STONE)
        val oreLevel = PlayerUtils.getTypeLevel(player, PlayerUtils.LevelType.ORE)

        val landIcon = ItemStack.of(Material.GRASS_BLOCK)
        landIcon.editMeta { meta ->
            meta.displayName(
                Component
                    .text()
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false)
                    .content("整地レベル: ${landLevel}/80")
                    .build()
            )
            meta.lore(
                listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("スキルを確認")
                        .build()
                )
            )
        }
        val woodIcon = ItemStack.of(Material.OAK_LOG)
        woodIcon.editMeta { meta ->
            meta.displayName(
                Component
                    .text()
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false)
                    .content("伐採レベル: ${woodLevel}/80")
                    .build()
            )
            meta.lore(
                listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("スキルを確認")
                        .build()
                )
            )
        }
        val stoneIcon = ItemStack.of(Material.STONE)
        stoneIcon.editMeta { meta ->
            meta.displayName(
                Component
                    .text()
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false)
                    .content("採石レベル: ${stoneLevel}/80")
                    .build()
            )
            meta.lore(
                listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("スキルを確認")
                        .build()
                )
            )
        }
        val oreIcon = ItemStack.of(Material.COAL_ORE)
        oreIcon.editMeta { meta ->
            meta.displayName(
                Component
                    .text()
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false)
                    .content("鉱石採掘レベル: ${oreLevel}/80")
                    .build()
            )
            meta.lore(
                listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("スキルを確認")
                        .build()
                )
            )
        }

        val levelsMenuPage = Page().lockEmptySlots()

        levelsMenuPage
            .button(19, UISlot(landIcon) {
                openSkillMenu(inventoryUI, player, PlayerUtils.LevelType.LAND)
            })
            .button(21, UISlot(woodIcon) {
                openSkillMenu(inventoryUI, player, PlayerUtils.LevelType.WOOD)
            })
            .button(23, UISlot(stoneIcon) {
                openSkillMenu(inventoryUI, player, PlayerUtils.LevelType.STONE)
            })
            .button(25, UISlot(oreIcon) {
                openSkillMenu(inventoryUI, player, PlayerUtils.LevelType.ORE)
            })

        inventoryUI.pushPage(levelsMenuPage)
    }

    fun openSkillMenu(inventoryUI: InventoryUI, player: Player, levelType: PlayerUtils.LevelType, isUpdate: Boolean = false) {
        val level = PlayerUtils.getTypeLevel(player, levelType)
        val skills = Main.skillDetails[levelType] ?: return
        val skillStates = PlayerUtils.PlayerDatabase.getSkillStates(player)

        val skillMenuPage = Page().lockEmptySlots()

        for (i in 0..<skills.size) {
            val skill = skills[i]

            val skillState = skillStates[skill.id]

            val material = Material.matchMaterial(skill.iconItemId) ?: return
            val skillIcon = ItemStack.of(material)
            skillIcon.editMeta { meta ->
                meta.displayName(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
                        .content(skill.name)
                        .build()
                )

                val lore = mutableListOf<Component>()

                val lastUnlockedIndex = skill.skillLevels.indexOfLast { skillLevel -> level >= skillLevel.unlockLevel }

                for (si in 0..<skill.skillLevels.size) {
                    val skillLevel = skill.skillLevels[si]

                    val color = if (level < skillLevel.unlockLevel) NamedTextColor.GRAY else NamedTextColor.GREEN
                    lore.add(
                        Component.text("")
                    )
                    lore.add(
                        Component
                            .text()
                            .decoration(TextDecoration.ITALIC, false)
                            .color(color)
                            .content(skillLevel.description)
                            .build()
                    )
                    lore.add(
                        Component
                            .text()
                            .decoration(TextDecoration.ITALIC, false)
                            .color(color)
                            .content("解放レベル: ${skillLevel.unlockLevel}")
                            .build()
                    )

                    if (lastUnlockedIndex == si) {
                        if (skillLevel.states != null) {
                            val statesId = skillState ?: skillLevel.states!!.last()

                            lore.add(
                                Component
                                    .text()
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(color)
                                    .content(
                                        "状態: ${statesIdToLabel(statesId)}"
                                    )
                                    .build()
                            )
                            lore.add(
                                Component
                                    .text()
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(color)
                                    .content("[右クリック] 状態切り替え")
                                    .build()
                            )
                        } else {
                            lore.add(
                                Component
                                    .text()
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(color)
                                    .content(
                                        "有効"
                                    )
                                    .build()
                            )
                        }
                    }
                }

                meta.lore(lore)
            }
            skillMenuPage.button(i, UISlot(skillIcon) {
                val currentSkillLevel = skill.skillLevels.last { skillLevel -> level >= skillLevel.unlockLevel }
                if (currentSkillLevel.states == null) return@UISlot
                if (skillState == null) {
                    skillStates[skill.id] = currentSkillLevel.states!!.first()
                } else {
                    when (val index = currentSkillLevel.states!!.indexOf(skillState)) {
                        -1 -> {
                            skillStates[skill.id] = currentSkillLevel.states!!.first()
                        }
                        currentSkillLevel.states!!.size - 1 -> {
                            skillStates[skill.id] = currentSkillLevel.states!!.first()
                        }
                        else -> {
                            skillStates[skill.id] = currentSkillLevel.states!![index + 1]
                        }
                    }
                }
                PlayerUtils.PlayerDatabase.setSkillStates(player, skillStates)

                openSkillMenu(inventoryUI, player, levelType, true)
            })
        }

        if (isUpdate) {
            inventoryUI.replacePage(skillMenuPage)
        } else {
            inventoryUI.pushPage(skillMenuPage)
        }
    }

    fun statesIdToLabel(state: String): String {
        return when (state) {
            "ACTIVE" -> {
                "有効"
            }
            "INACTIVE" -> {
                "無効"
            }
            "3x3" -> {
                "[3x3]"
            }
            "5x5" -> {
                "[5x5]"
            }
            "TRUNK_ONLY" -> {
                "幹のみ"
            }
            "WOOD_ALL" -> {
                "木全体"
            }
            else -> {
                ""
            }
        }
    }
}
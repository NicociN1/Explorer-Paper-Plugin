package com.nicon.explorerPaper.skills

import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.definitions.ItemDefinitions
import com.nicon.explorerPaper.utils.InventoryUI
import com.nicon.explorerPaper.utils.InventoryUI.*
import com.nicon.explorerPaper.utils.PD
import com.nicon.explorerPaper.utils.PlayerUtils
import com.nicon.explorerPaper.utils.PlayerUtils.LevelType
import com.nicon.explorerPaper.utils.TF
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object SkillScreen {
    /**
     * レベル一覧のメニューを開く
     */
    fun openLevelsMenu(inventoryUI: InventoryUI, player: Player) {
        val landLevel = PlayerUtils.getLevelWithType(player, LevelType.LAND)
        val woodLevel = PlayerUtils.getLevelWithType(player, LevelType.WOOD)
        val stoneLevel = PlayerUtils.getLevelWithType(player, LevelType.STONE)
        val oreLevel = PlayerUtils.getLevelWithType(player, LevelType.ORE)

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
                openSelectMenu(inventoryUI, player, LevelType.LAND)
            })
            .button(21, UISlot(woodIcon) {
                openSelectMenu(inventoryUI, player, LevelType.WOOD)
            })
            .button(23, UISlot(stoneIcon) {
                openSelectMenu(inventoryUI, player, LevelType.STONE)
            })
            .button(25, UISlot(oreIcon) {
                openSelectMenu(inventoryUI, player, LevelType.ORE)
            })
            .button(45, UISlot(ItemDefinitions.getBackMainMenu()) {
                inventoryUI.backPage()
            })

        inventoryUI.pushPage(levelsMenuPage)
    }

    /**
     * レベルに対して行う操作メニューを開く
     */
    fun openSelectMenu(inventoryUI: InventoryUI, player: Player, levelType: LevelType) {
        val skillIcon = ItemStack.of(Material.ENCHANTED_BOOK)
        skillIcon.editMeta { meta ->
            meta.displayName(TF.parse("<!italic><bold>スキルメニューを開く"))
        }
        val unlockLevelCapIcon = when (levelType) {
            LevelType.LAND -> ItemStack.of(Material.IRON_SHOVEL)
            LevelType.WOOD -> ItemStack.of(Material.IRON_AXE)
            LevelType.STONE -> ItemStack.of(Material.IRON_PICKAXE)
            LevelType.ORE -> ItemStack.of(Material.DIAMOND_PICKAXE)
        }
        unlockLevelCapIcon.editMeta { meta ->
            meta.displayName(TF.parse("<!italic><bold>レベルキャップを解放"))
        }
        val backPageIcon = ItemStack.of(Material.PLAYER_HEAD)
        backPageIcon.editMeta { meta ->
            meta.displayName(
                Component
                    .text()
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false)
                    .content("スキル一覧に戻る")
                    .build()
            )
        }

        val selectMenuPage = Page().lockEmptySlots()
            .button(21, UISlot(skillIcon) {
                openSkillMenu(inventoryUI, player, levelType)
            })
            .button(23, UISlot(unlockLevelCapIcon) {
                openUnlockLevelCapMenu(inventoryUI, player, levelType)
            })
            .button(45, UISlot(backPageIcon) {
                inventoryUI.backPage()
            })

        inventoryUI.pushPage(selectMenuPage)
    }

    /**
     * スキルメニューを開く
     */
    fun openSkillMenu(inventoryUI: InventoryUI, player: Player, levelType: LevelType, isUpdate: Boolean = false) {
        val level = PlayerUtils.getLevelWithType(player, levelType)
        val skills = Main.skillDetails[levelType] ?: return
        val skillStates = PD.getSkillStates(player)

        val skillMenuPage = Page().lockEmptySlots()

        for (i in 0..<skills.size) {
            val skill = skills[i]

            val skillState = skillStates[skill.id]

            val lastUnlockedIndex = skill.skillLevels.indexOfLast { skillLevel -> level >= skillLevel.unlockLevel }

            val material = Material.matchMaterial(skill.iconItemId) ?: return
            val skillIcon = ItemStack.of(material)
            skillIcon.editMeta { meta ->
                meta.displayName(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
                        .content("${skill.name} ${if (lastUnlockedIndex >= 0) "Lv.${lastUnlockedIndex + 1}" else ""}")
                        .build()
                )

                val lore = mutableListOf<Component>()

                lore.add(
                    Component.text("")
                )
                val currentLevel = skill.skillLevels.getOrNull(lastUnlockedIndex)
                if (currentLevel != null) {
                    lore.add(
                        Component
                            .text()
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.GREEN)
                            .content(currentLevel.description)
                            .build()
                    )
                    lore.add(
                        Component
                            .text()
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.GREEN)
                            .content("解放レベル: ${currentLevel.unlockLevel}")
                            .build()
                    )

                    if (currentLevel.states != null) {

                        lore.add(
                            Component
                                .text()
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.GREEN)
                                .content(
                                    "状態: ${statesIdToLabel(skillState)}"
                                )
                                .build()
                        )
                        lore.add(
                            Component
                                .text()
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.GREEN)
                                .content("[クリック] 状態切り替え")
                                .build()
                        )
                    } else {
                        lore.add(
                            Component
                                .text()
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.GREEN)
                                .content(
                                    "有効"
                                )
                                .build()
                        )
                    }
                } else {
                    lore.add(
                        Component
                            .text()
                            .color(NamedTextColor.WHITE)
                            .content("未解放")
                            .build()
                    )
                }

                lore.add(
                    Component.text("")
                )
                val nextLevel = skill.skillLevels.getOrNull(lastUnlockedIndex + 1)
                if (nextLevel != null) {
                    lore.add(
                        Component
                            .text()
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.GRAY)
                            .content(nextLevel.description)
                            .build()
                    )
                    lore.add(
                        Component
                            .text()
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.GRAY)
                            .content("解放レベル: ${nextLevel.unlockLevel}")
                            .build()
                    )
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
                PD.setSkillStates(player, skillStates)

                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)

                openSkillMenu(inventoryUI, player, levelType, true)
            })
        }

        val backPageIcon = ItemStack.of(Material.BOOK)
        backPageIcon.editMeta { meta ->
            meta.displayName(TF.parse("<reset><bold>操作選択メニューに戻る"))
        }

        skillMenuPage
            .button(45, UISlot(backPageIcon) {
                inventoryUI.backPage()
            })

        if (isUpdate) {
            inventoryUI.replacePage(skillMenuPage)
        } else {
            inventoryUI.pushPage(skillMenuPage)
        }
    }

    fun openUnlockLevelCapMenu(inventoryUI: InventoryUI, player: Player, levelType: LevelType, isUpdate: Boolean = false) {
        val unlockLevelCapPage = Page().lockEmptySlots()

        val levelCapDetail = Main.levelCapDetails[levelType] ?: return

        val levelCapCount = PlayerUtils.getLevelCapCountWithType(player, levelType)

        for (i in 0..<levelCapDetail.size) {
            val levelCap = levelCapDetail[i]

            when {
                levelCapCount > i -> {
                    val icon = ItemStack.of(Material.LIME_CONCRETE)
                    icon.editMeta { meta ->
                        meta.displayName(TF.parse("<reset><bold>Lv.${levelCap.level}"))
                        meta.lore(
                            listOf(
                                TF.parse("<white>解放済み!")
                            )
                        )
                    }
                    unlockLevelCapPage.button(i, UISlot(icon) {})
                }
                levelCapCount == i -> {
                    val icon = ItemStack.of(Material.RED_CONCRETE)
                    icon.editMeta { meta ->
                        meta.displayName(TF.parse("<reset><bold>Lv.${levelCap.level}"))
                        val lore: MutableList<Component> = mutableListOf()
                        lore.add(TF.parse(""))
                        lore.add(TF.parse("<reset><white>必要コスト:"))

                        val gold = PD.getGold(player)
                        val goldTxtColor = if (gold >= levelCap.requireGold) "gold" else "gray"
                        lore.add(TF.parse("<reset><${goldTxtColor}>${levelCap.requireGold}GOLD"))
                        for (item in levelCap.requireItems) {
                            val haveAmount = PlayerUtils.getItemAmount(player, item.id)
                            val itemTxtColor = if (haveAmount >= item.amount) "green" else "gray"
                            lore.add(TF.parse("<reset><${itemTxtColor}>${item.label} ${haveAmount}/${item.amount}"))
                        }
                        meta.lore(lore)

                        meta.addEnchant(Enchantment.LURE, 1, true)
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    }
                    unlockLevelCapPage.button(i, UISlot(icon) {
                        var isItemEnough = true
                        for (item in levelCap.requireItems) {
                            val haveAmount = PlayerUtils.getItemAmount(player, item.id)
                            if (haveAmount < item.amount) {
                                isItemEnough = false
                            }
                        }

                        val gold = PD.getGold(player)
                        val isGoldEnough = gold >= levelCap.requireGold

                        if (isItemEnough && isGoldEnough) {
                            PD.setGold(player, gold - levelCap.requireGold)
                            for (item in levelCap.requireItems) {
                                PlayerUtils.clearItems(player, item.id, item.amount)
                            }
                            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                            player.sendMessage(TF.parse("<bold><green>レベルキャップがLv.${levelCap.level}まで解放されました！"))
                            PlayerUtils.setLevelCapCountWithType(player, levelType, levelCapCount + 1)
                            openUnlockLevelCapMenu(inventoryUI, player, levelType, true)
                        } else {
                            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f)
                        }
                    })
                }
                else -> {
                    val icon = ItemStack.of(Material.RED_CONCRETE)
                    icon.editMeta { meta ->
                        meta.displayName(TF.parse("<reset><bold>Lv.${levelCap.level}"))
                        meta.lore(
                            listOf(
                                TF.parse("<reset><red>前のレベルキャップを解放してください")
                            )
                        )
                    }
                    unlockLevelCapPage.button(i, UISlot(icon) {})
                }
            }
        }

        unlockLevelCapPage.button(45, UISlot(ItemDefinitions.getBackPage()) {
            inventoryUI.backPage()
        })

        if (!isUpdate) {
            inventoryUI.pushPage(unlockLevelCapPage)
        } else {
            inventoryUI.replacePage(unlockLevelCapPage)
        }
    }

    fun statesIdToLabel(state: String?): String {
        return when (state) {
            "ACTIVE" -> {
                "有効"
            }
            "INACTIVE" -> {
                "無効"
            }
            null -> {
                "未設定"
            }
            else -> {
                state
            }
        }
    }

    fun getNextLevelCap(currentLevel: Int): Int? {
        return when (currentLevel) {
            20 -> 25
            25 -> 30
            30 -> 35
            35 -> 40
            40 -> 45
            45 -> 50
            50 -> 55
            55 -> 60
//            60 -> 65
//            70 -> 73
//            73 -> 75
//            75 -> 78
//            78 -> 80
            else -> null
        }
    }

    /**
     * @return 消費GOLDと消費アイテムの配列のペアを返す
     */
    fun getUnlockLevelCapCost(levelType: LevelType, unlockingLevelCap: Int): Pair<Int, Array<Pair<Material, Int>>>? {
        return when (unlockingLevelCap) {
            25 -> {
                Pair(1000, arrayOf(Pair(Material.COBBLESTONE, 64)))
            }
            else -> null
        }
    }
}
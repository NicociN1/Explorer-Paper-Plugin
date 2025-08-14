package com.nicon.explorerPaper.recipes

import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.definitions.ItemDefinitions
import com.nicon.explorerPaper.recipes.RecipeData.RecipeDetail
import com.nicon.explorerPaper.utils.GameUtils
import com.nicon.explorerPaper.utils.InventoryUI
import com.nicon.explorerPaper.utils.InventoryUI.Page
import com.nicon.explorerPaper.utils.InventoryUI.UISlot
import com.nicon.explorerPaper.utils.PlayerUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.min

object CraftScreen {
    fun openRecipeList(player: Player, inventoryUI: InventoryUI, page: Int) {
        val recipeListPage = Page().lockEmptySlots()

        val recipeDetails = Main.recipeDetails

        val pageSize = 45

        val fromIndex = page * pageSize
        val toIndex = fromIndex + pageSize - 1

        val maxPage = ceil(recipeDetails.size.toFloat() / pageSize) - 1

        val gridIcon = ItemDefinitions.getGridIcon()

        val unlockedRecipes = PlayerUtils.PlayerDatabase.getUnlockedRecipes(player)

        for (slot in fromIndex..min(toIndex, recipeDetails.size - 1)) {
            val recipeDetail = recipeDetails[slot]
            val material = Material.matchMaterial(recipeDetail.outputItem.id) ?: continue
            val itemStack = ItemStack.of(material, recipeDetail.outputItem.amount)

            itemStack.editMeta { meta ->
                if (recipeDetail.outputItem.properties != null && recipeDetail.outputItem.properties!!.customName != null) {
                    meta.displayName(
                        Component
                            .text()
                            .decoration(TextDecoration.ITALIC, false)
                            .content(recipeDetail.outputItem.properties!!.customName!!)
                            .build()
                    )
                }
                val lore = mutableListOf<Component>()
                if (unlockedRecipes.contains(recipeDetail.id)) {
                    lore.add(
                        Component
                            .text()
                            .decoration(TextDecoration.ITALIC, true)
                            .decoration(TextDecoration.BOLD, true)
                            .color(NamedTextColor.WHITE)
                            .content("解放済み")
                            .build()
                    )
                }
                lore.add(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.GREEN)
                        .content("解放条件: ${recipeDetail.unlockCondition}")
                        .build()
                )
                lore.add(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.GOLD)
                        .content("消費Gold: ${recipeDetail.goldCost}G")
                        .build()
                )
                meta.lore(lore)
            }
            recipeListPage.button(slot - fromIndex, UISlot(itemStack) {
                openCraft(player, inventoryUI, recipeDetail)
            })
        }

        for (slot in 45..<54) {
            when (slot) {
                45 -> {
                    val backPageIcon = ItemStack.of(Material.COMPASS)
                    backPageIcon.editMeta { meta ->
                        meta.displayName(
                            Component
                                .text()
                                .decoration(TextDecoration.BOLD, true)
                                .decoration(TextDecoration.ITALIC, false)
                                .content("ホームに戻る")
                                .build()
                        )
                    }

                    recipeListPage.button(slot, UISlot(backPageIcon) {
                        inventoryUI.backPage()
                    })
                }

                46 -> {
                    val playerInfoIcon = ItemDefinitions.getPlayerInfoIcon(player)

                    recipeListPage.button(slot, UISlot(playerInfoIcon) {})
                }

                48 if fromIndex > 0 -> {
                    val prevPageIcon = ItemStack.of(Material.ARROW)
                    prevPageIcon.editMeta { meta ->
                        meta.displayName(
                            Component
                                .text()
                                .decoration(TextDecoration.BOLD, true)
                                .decoration(TextDecoration.ITALIC, false)
                                .content("前のページ")
                                .build()
                        )
                    }

                    recipeListPage.button(slot, UISlot(prevPageIcon) {
                        openRecipeList(player, inventoryUI, page - 1)
                    })
                }

                50 if page < maxPage -> {
                    val nextPageIcon = ItemStack.of(Material.ARROW)
                    nextPageIcon.editMeta { meta ->
                        meta.displayName(
                            Component
                                .text()
                                .decoration(TextDecoration.BOLD, true)
                                .decoration(TextDecoration.ITALIC, false)
                                .content("次のページ")
                                .build()
                        )
                    }

                    recipeListPage.button(slot, UISlot(nextPageIcon) {
                        openRecipeList(player, inventoryUI, page + 1)
                    })
                }

                else -> {
                    recipeListPage.button(slot, UISlot(gridIcon) {})
                }
            }
        }

        inventoryUI.pushPage(recipeListPage)
    }

    fun openCraft(player: Player, inventoryUI: InventoryUI, recipeDetail: RecipeDetail, isUpdate: Boolean = false) {
        val craftPage = Page().lockEmptySlots()

        val gridIcon = ItemDefinitions.getGridIcon()

        val recipeSlots = (10..16).toList()

        for (i in 0..<recipeSlots.size) {
            val recipeSlot = recipeSlots[i]
            val requireItem = recipeDetail.requireItems.getOrNull(i) ?: break

            val recipeMaterial = Material.matchMaterial(requireItem.id) ?: continue
            val recipeItem = ItemStack.of(recipeMaterial, requireItem.amount)

            craftPage.button(recipeSlot, UISlot(recipeItem) {})
        }

        for (slot in 0..<inventoryUI.inventory.size) {
            when (slot) {
                31 -> {
                    val material = Material.matchMaterial(recipeDetail.outputItem.id)
                        ?: throw IllegalStateException("不正なアイテムID: ${recipeDetail.outputItem.id}")
                    val outputItem = ItemStack.of(material, recipeDetail.outputItem.amount)

                    if (recipeDetail.outputItem.properties != null) {
                        outputItem.editMeta { meta ->
                            if (recipeDetail.outputItem.properties!!.customName != null) {
                                meta.displayName(
                                    Component
                                        .text()
                                        .decoration(TextDecoration.ITALIC, false)
                                        .content(recipeDetail.outputItem.properties!!.customName!!)
                                        .build()
                                )
                            }
                        }
                    }

                    craftPage.button(slot, UISlot(outputItem) {
                        val isCanCraft =
                            getIsUnlockedRecipe(player, recipeDetail) && getIsHaveCosts(player, recipeDetail)
                        if (!isCanCraft) return@UISlot

                        for (requireItem in recipeDetail.requireItems) {
                            PlayerUtils.clearItems(player, requireItem.id, requireItem.amount)
                        }

                        val outputMaterial = Material.matchMaterial(recipeDetail.outputItem.id) ?: return@UISlot
                        val itemStack =
                            GameUtils.setItemDetail(ItemStack.of(outputMaterial, recipeDetail.outputItem.amount))

                        val detailedItemStack = GameUtils.setItemDetail(itemStack)

                        if (recipeDetail.outputItem.properties != null) {
                            detailedItemStack.editMeta { meta ->
                                if (recipeDetail.outputItem.properties!!.customName != null) {
                                    meta.displayName(
                                        Component
                                            .text()
                                            .decoration(TextDecoration.ITALIC, false)
                                            .content(recipeDetail.outputItem.properties!!.customName!!)
                                            .build()
                                    )
                                }
                            }
                        }

                        PlayerUtils.safeAddItem(player, detailedItemStack)

                        player.playSound(player.location, Sound.BLOCK_ANVIL_USE, 1f, 1f)

                        val gold = PlayerUtils.PlayerDatabase.getGold(player) ?: 0
                        PlayerUtils.PlayerDatabase.setGold(player, gold - recipeDetail.goldCost)
                        PlayerUtils.refreshSidebar(player)

                        openCraft(player, inventoryUI, recipeDetail, true)
                    })
                }

                40 -> {
                    val isHaveItems = getIsHaveCosts(player, recipeDetail)
                    val isUnlocked = getIsUnlockedRecipe(player, recipeDetail)
                    val isCanCraft = isHaveItems && isUnlocked
                    val craftingButtonItem = if (isCanCraft) Material.LIME_STAINED_GLASS else Material.RED_STAINED_GLASS
                    val craftingButton = ItemStack.of(craftingButtonItem)
                    craftingButton.editMeta { meta ->
                        meta.displayName(
                            Component
                                .text()
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.BOLD, true)
                                .color(if (isCanCraft) NamedTextColor.GREEN else NamedTextColor.RED)
                                .content(if (isCanCraft) "クラフト可能" else "クラフト不可")
                                .build()
                        )
                        val lore = mutableListOf(
                            Component
                                .text()
                                .color(NamedTextColor.GOLD)
                                .content("消費Gold: ${recipeDetail.goldCost}G")
                                .build()
                        )
                        if (!isCanCraft) {
                            lore.add(
                                Component
                                    .text()
                                    .color(NamedTextColor.RED)
                                    .content(if (!isUnlocked) "レシピが未解放です" else "アイテムもしくはGoldが足りません")
                                    .build()
                            )
                        }
                        meta.lore(lore)
                    }

                    craftPage.button(slot, UISlot(craftingButton) {})
                }

                45 -> {
                    val recipeListButton = ItemStack.of(Material.BOOK)
                    recipeListButton.editMeta { meta ->
                        meta.displayName(
                            Component
                                .text()
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.BOLD, true)
                                .content("レシピリストに戻る")
                                .build()
                        )
                    }

                    craftPage.button(slot, UISlot(recipeListButton) {
                        inventoryUI.backPage()
                    })
                }

                46 -> {
                    val playerInfoIcon = ItemDefinitions.getPlayerInfoIcon(player)

                    craftPage.button(slot, UISlot(playerInfoIcon) {})
                }

                else if (!recipeSlots.contains(slot)) -> {
                    craftPage.button(slot, UISlot(gridIcon) {})
                }
            }
        }

        if (!isUpdate) {
            inventoryUI.pushPage(craftPage)
        } else {
            inventoryUI.replacePage(craftPage)
        }
    }

    fun getIsUnlockedRecipe(player: Player, recipeDetail: RecipeDetail): Boolean {
        val unlockedRecipes = PlayerUtils.PlayerDatabase.getUnlockedRecipes(player)
        return unlockedRecipes.contains(recipeDetail.id)
    }

    fun getIsHaveCosts(player: Player, recipeDetail: RecipeDetail): Boolean {
        val totalRequired: Map<String, Int> =
            recipeDetail.requireItems
                .groupBy { it.id }
                .mapValues { (_, items) ->
                    items.sumOf { it.amount }
                }
        var canCraft = true
        for (requireItem in totalRequired) {
            val haveAmount = PlayerUtils.getItemAmount(player, requireItem.key)
            if (haveAmount < requireItem.value) {
                canCraft = false
            }
        }

        if (canCraft) {
            val gold = PlayerUtils.PlayerDatabase.getGold(player) ?: 0
            if (gold < recipeDetail.goldCost) {
                canCraft = false
            }
        }

        return canCraft
    }
}
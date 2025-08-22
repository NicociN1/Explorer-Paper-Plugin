package com.nicon.explorerPaper.definitions

import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.utils.GameUtils
import com.nicon.explorerPaper.utils.InventoryUI
import com.nicon.explorerPaper.utils.InventoryUI.Page
import com.nicon.explorerPaper.utils.InventoryUI.UISlot
import com.nicon.explorerPaper.utils.PD
import com.nicon.explorerPaper.utils.PlayerUtils
import com.nicon.explorerPaper.utils.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object StorageScreen {
    fun openStorageList(inventoryUI: InventoryUI, player: Player, isUpdate: Boolean = false) {
        player.playSound(player.location, Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f)

        val unlockedStoragesCount = GameUtils.getUnlockedStoragesCount(player)
        val storageListPage = Page().lockEmptySlots()

        for (i in 0..<Constants.MAX_STORAGE_COUNT) {
            val unlockCost = GameUtils.getStorageUnlockCost(i + 1) ?: continue

            val storageIcon = ItemStack.of(Material.CHEST)
            storageIcon.editMeta { meta ->
                meta.displayName(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .content("ストレージ #${i + 1}")
                        .build()
                )
                if (unlockedStoragesCount >= i + 1) {
                    meta.lore(
                        listOf(
                            Component
                                .text()
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.WHITE)
                                .content("[クリック] 保管庫を見る")
                                .build()
                        )
                    )
                } else if (unlockedStoragesCount == i) {
                    meta.lore(
                        listOf(
                            Component
                                .text()
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.GOLD)
                                .content("解放: ${Utils.addCommaToNumber(unlockCost)}")
                                .build()
                        )
                    )
                } else {
                    meta.lore(
                        listOf(
                            Component
                                .text()
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.RED)
                                .content("前のストレージを解放してください")
                                .build()
                        )
                    )
                }
            }

            storageListPage.button(i, UISlot(storageIcon) {
                val unlockedStoragesCount = GameUtils.getUnlockedStoragesCount(player)
                if (unlockedStoragesCount == i) {
                    val gold = PD.getGold(player)
                    if (gold >= unlockCost) {
                        PD.setGold(player, gold - unlockCost)
                        GameUtils.setUnlockedStorageCount(player, unlockedStoragesCount + 1)

                        PlayerUtils.refreshSidebar(player)

                        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)

                        openStorageList(inventoryUI, player, true)
                    } else {
                        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f)
                    }
                } else if (unlockedStoragesCount >= i - 1) {
                    openStorage(inventoryUI, player, i)
                }
            })
        }

        if (isUpdate) {
            inventoryUI.replacePage(storageListPage)
        } else {
            inventoryUI.pushPage(storageListPage)
        }
    }

    fun openStorage(inventoryUI: InventoryUI, player: Player, storageSlot: Int) {
        val storage: MutableMap<Int, ItemStack> = GameUtils.getPlayerStorage(player, storageSlot) ?: mutableMapOf()

        val storagePage = Page()
            .releaseSafeItems()
            .onClick {
                Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
                    val items = (0 until inventoryUI.inventory.size)
                        .mapNotNull { slot ->
                            inventoryUI.inventory.getItem(slot)?.let { slot to it }
                        }
                        .toMap().toMutableMap()
                    GameUtils.savePlayerStorage(player, storageSlot, items)
                }, 1L)
            }

        inventoryUI.pushPage(storagePage)

        for (slot in storage) {
            inventoryUI.inventory.setItem(slot.key, slot.value)
        }
    }
}
package com.nicon.explorerPaper.definitions

import com.nicon.explorerPaper.utils.InventoryUI
import com.nicon.explorerPaper.utils.InventoryUI.Page
import com.nicon.explorerPaper.utils.InventoryUI.UISlot
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class InventoryUIDefinitions {
    companion object {
        fun openMenuUI(player: Player) {
            val inventoryUI = InventoryUI(player, "メニュー");

            val trashBoxIcon = ItemStack.of(Material.BONE_MEAL)
            trashBoxIcon.editMeta { meta ->
                meta.displayName(
                    Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
                        .content("ゴミ箱")
                        .build()
                )
            }
            val homePage = Page()
                .lockEmptySlots()
                .button(22, UISlot(trashBoxIcon) {
                    player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)

                    val itemsTrashIcon = ItemStack.of(Material.BONE_MEAL)
                    itemsTrashIcon.editMeta { meta ->
                        meta.displayName(
                            Component.text()
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.BOLD, true)
                                .content("アイテムを破棄する")
                                .build()
                        )
                        meta.lore(mutableListOf(
                            Component.text()
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.RED)
                                .content("元に戻すことはできません")
                                .build()
                            )
                        )
                    }
                    val trashPage = Page()
                        .button(53, UISlot(itemsTrashIcon) {
                            player.playSound(player.location, Sound.BLOCK_ANVIL_LAND, 1f, 1f)

                            for (slot in 0..52) {
                                inventoryUI.inventory.setItem(slot, ItemStack.empty())
                            }
                        })
                    inventoryUI.pushPage(trashPage)
                })

            inventoryUI.replacePage(homePage)
            inventoryUI.show()
        }
    }
}
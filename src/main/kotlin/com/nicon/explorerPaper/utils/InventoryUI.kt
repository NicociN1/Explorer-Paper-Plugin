package com.nicon.explorerPaper.utils

import com.nicon.explorerPaper.Main
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class InventoryUI : Listener {
    val player: Player
    val size: Int
    val title: String
    var pageHistory: MutableList<Page>
        private set
    var currentPage: Page
        private set
    val inventory: Inventory

    constructor(player: Player, title: String, size: Int = 54) {
        this.player = player
        this.size = size
        this.title = title
        this.pageHistory = mutableListOf()
        currentPage = Page()
        inventory = Bukkit.createInventory(null, size, Component.text(title))

        Bukkit.getPluginManager().registerEvents(this, Main.instance)
    }

    fun pushPage(page: Page) {
        pageHistory.add(currentPage)
        replacePage(page)
    }
    fun backPage() {
        if (pageHistory.isEmpty()) return
        replacePage(pageHistory.last())
        pageHistory.removeLast()
    }
    fun replacePage(page: Page) {
        currentPage = page
        for (slot in 0..<inventory.size) {
            inventory.setItem(slot, ItemStack.empty())
        }
        for (slotEntry in page.uiSlots) {
            inventory.setItem(slotEntry.key, slotEntry.value.itemStack)
        }
    }

    fun show() {
        player.openInventory(inventory)
    }

    class Page {
        var onClickHandler: Runnable = Runnable {}
            private set

        var uiSlots: MutableMap<Int, UISlot> = mutableMapOf()
            private set

        var isLockedEmptySlot = false
            private set

        fun button(slot: Int, uiSlot: UISlot): Page {
            uiSlots[slot] = uiSlot
            return this
        }
        fun lockEmptySlots(): Page {
            isLockedEmptySlot = true
            return this
        }
        fun unlockEmptySlots(): Page {
            isLockedEmptySlot = false
            return this
        }

        fun onClick(onClickHandler: Runnable): Page {
            this.onClickHandler = onClickHandler
            return this
        }
    }

    class UISlot(
        val itemStack: ItemStack,
        val onClick: Runnable
    )

    @EventHandler
    fun onClickInventory(event: InventoryClickEvent) {
        if (event.clickedInventory !== inventory) return

        val uiSlot = currentPage.uiSlots[event.slot]
        if (uiSlot != null) {
            event.isCancelled = true
            uiSlot.onClick.run()
        } else if (currentPage.isLockedEmptySlot) {
            event.isCancelled = true
        }

        currentPage.onClickHandler.run()
    }

    @EventHandler
    fun onCloseInventory(event: InventoryCloseEvent) {
        if (event.inventory !== inventory) return

        for (slot in 0..<inventory.size) {
            if (currentPage.uiSlots.keys.contains(slot)) continue
            val item = inventory.getItem(slot) ?: continue
            player.inventory.addItem(item)
        }
    }
}
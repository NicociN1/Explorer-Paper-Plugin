package com.nicon.explorerPaper.events.player

import com.nicon.explorerPaper.Explorer
import com.nicon.explorerPaper.definitions.InventoryUIDefinitions
import com.nicon.explorerPaper.definitions.ItemDefinitions
import com.nicon.explorerPaper.definitions.MiscConstants
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot

class PlayerEvents : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        event.joinMessage(
            Component.text()
                .content("[参加] ${player.name}")
                .color(NamedTextColor.LIGHT_PURPLE)
                .build()
        )

        player.inventory.setItem(8, ItemDefinitions.getMenuItem())
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        event.quitMessage(
            Component.text()
                .content("[退出] ${event.player.name}")
                .color(NamedTextColor.LIGHT_PURPLE)
                .build()
        )
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        Explorer.instance.logger.info(event.cause.toString())
        event.isCancelled = true
    }

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        val player = event.player
        if (player.gameMode == GameMode.CREATIVE) return
        val itemStack = event.itemDrop.itemStack
        if (MiscConstants.DISABLED_DROP_ITEMS.contains(itemStack.type.key.toString())) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        Explorer.instance.logger.info("Interact!")
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            val itemId = player.inventory.getItem(EquipmentSlot.HAND).type.key.toString()

            Explorer.instance.logger.info(itemId)

            if (itemId == "minecraft:compass") {
                InventoryUIDefinitions.openMenuUI(player)
            }
        }
    }
}

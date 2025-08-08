package com.nicon.explorerPaper.events.block

import com.nicon.explorerPaper.Explorer
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.loot.LootContext
import org.bukkit.loot.LootTable

class BlockEvents : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        val blockId = block.type.key.toString()

        val blockDetails = Explorer.blockDetails ?: return
        val blockDetail = blockDetails[blockId] ?: return
        if (blockDetail.lootTable == null) return

        val key: NamespacedKey = NamespacedKey.fromString(blockDetail.lootTable!!) ?: return
        val lootTable: LootTable = Bukkit.getLootTable(key) ?: return
        val lootContext = LootContext.Builder(player.location)
        val itemStacks = lootTable.populateLoot(null, lootContext.build())
        for (item in itemStacks) {
            player.inventory.addItem(item)
        }
    }
}

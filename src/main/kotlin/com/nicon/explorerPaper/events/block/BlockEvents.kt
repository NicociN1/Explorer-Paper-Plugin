package com.nicon.explorerPaper.events.block

import com.nicon.explorerPaper.blocks.BlockHandlers
import com.nicon.explorerPaper.skills.SkillEffects
import com.nicon.explorerPaper.utils.GameUtils
import com.nicon.explorerPaper.utils.PlayerUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Sound
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

class BlockEvents : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        val blockDetail = GameUtils.getBlockDetail(block) ?: return

        val handItemStack = player.inventory.itemInMainHand
        val handItemId = handItemStack.type.key.toString()
        if (blockDetail.appropriateTools != null && !Regex(blockDetail.appropriateTools!!).matches(handItemId)) {
            event.isCancelled = true
            player.sendMessage(
                Component
                    .text()
                    .color(NamedTextColor.RED)
                    .content("このツールでは採掘できません")
                    .build()
            )
            player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
            return
        }

        BlockHandlers.onBreak(player, block)
        SkillEffects.handleAreaMining(player, block)

        PlayerUtils.refreshSidebar(player)
    }
}

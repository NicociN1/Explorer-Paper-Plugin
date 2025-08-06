package com.nicon.explorerPaper.events.player

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerEvents : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player
    }
}
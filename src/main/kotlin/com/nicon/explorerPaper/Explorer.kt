package com.nicon.explorerPaper

import com.nicon.explorerPaper.events.block.BlockEvents
import com.nicon.explorerPaper.events.player.PlayerEvents
import com.nicon.explorerPaper.events.world.WorldEvents
import com.nicon.items.BlockManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Explorer : JavaPlugin() {
    companion object {
        lateinit var instance: Explorer
            private set
        var blockDetails: MutableMap<String, BlockManager.BlockDetail>? = null
            private set
    }

    override fun onEnable() {
        instance = this
        instance.logger.info("Explorer Pluginが有効になりました！")

        blockDetails = BlockManager.details;

        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(PlayerEvents(), this)
        pluginManager.registerEvents(BlockEvents(), this)
        pluginManager.registerEvents(WorldEvents(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}

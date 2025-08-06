package com.nicon.explorerPaper

import com.nicon.explorerPaper.events.player.PlayerEvents
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class ExplorerPaper : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(PlayerEvents(), this);
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}

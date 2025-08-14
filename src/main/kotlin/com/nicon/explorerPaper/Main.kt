package com.nicon.explorerPaper

import com.nicon.explorerPaper.blocks.BlockData.BlockDetail
import com.nicon.explorerPaper.blocks.BlockManager
import com.nicon.explorerPaper.enchants.EnchantManager
import com.nicon.explorerPaper.events.block.BlockEvents
import com.nicon.explorerPaper.events.player.PlayerEvents
import com.nicon.explorerPaper.events.world.WorldEvents
import com.nicon.explorerPaper.items.ItemData.ItemDetail
import com.nicon.explorerPaper.items.ItemManager
import com.nicon.explorerPaper.recipes.RecipeData.RecipeDetail
import com.nicon.explorerPaper.recipes.RecipeManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var instance: Main
            private set
        lateinit var blockDetails: Map<String, BlockDetail>
            private set
        lateinit var itemDetails: Map<String, ItemDetail>
            private set
        lateinit var recipeDetails: Array<RecipeDetail>
            private set
        lateinit var enchantDetails: Map<String, Map<String, Int>>
            private set
    }

    override fun onEnable() {
        instance = this
        instance.logger.info("Explorer Pluginが有効になりました！")

        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        blockDetails = BlockManager.getDetails()
        itemDetails = ItemManager.getDetails()
        recipeDetails = RecipeManager.getDetails()
        enchantDetails = EnchantManager.getDetails()

        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(PlayerEvents(), this)
        pluginManager.registerEvents(BlockEvents(), this)
        pluginManager.registerEvents(WorldEvents(), this)

        Database.init(dataFolder.absolutePath)
    }

    override fun onDisable() {
        logger.info("Explorer Pluginが無効になりました")
        Database.close()
    }
}

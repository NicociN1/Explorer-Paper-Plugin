package com.nicon.explorerPaper.definitions

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ItemDefinitions {
    companion object {
        /**
         * メニューアイテムを取得
         */
        fun getMenuItem(): ItemStack {
            val itemStack = ItemStack.of(Material.COMPASS)
            val meta = itemStack.itemMeta
            meta.addEnchant(Enchantment.LURE, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemStack.setItemMeta(meta)

            return itemStack
        }
    }
}
package com.nicon.explorerPaper.utils

import com.nicon.explorerPaper.Main
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object ItemUtils {
    fun addCustomTag(item: ItemStack, keyName: String, value: String): ItemStack {
        val key = NamespacedKey(Main.instance, keyName)

        val clonedItem = item.clone()
        clonedItem.editMeta { meta ->
            meta?.persistentDataContainer?.set(key, PersistentDataType.STRING, value)
        }

        return clonedItem
    }

    fun removeCustomTag(item: ItemStack, keyName: String): ItemStack {
        val key = NamespacedKey(Main.instance, keyName)

        val clonedItem = item.clone()
        clonedItem.editMeta { meta ->
            meta?.persistentDataContainer?.remove(key)
        }

        return clonedItem
    }

    fun getCustomTag(item: ItemStack, keyName: String): String? {
        val meta = item.itemMeta ?: return null
        val key = NamespacedKey(Main.instance, keyName)

        return meta.persistentDataContainer.get(key, PersistentDataType.STRING)
    }
}
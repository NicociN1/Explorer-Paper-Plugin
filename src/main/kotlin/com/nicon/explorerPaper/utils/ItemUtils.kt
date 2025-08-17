package com.nicon.explorerPaper.utils

import com.nicon.explorerPaper.Main
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object ItemUtils {
    fun addCustomTag(item: ItemStack, keyName: String, value: String) {
        val key = NamespacedKey(Main.instance, keyName)

        item.editMeta { meta ->
            meta?.persistentDataContainer?.set(key, PersistentDataType.STRING, value)
        }
    }

    fun removeCustomTag(item: ItemStack, keyName: String) {
        val key = NamespacedKey(Main.instance, keyName)

        item.editMeta { meta ->
            meta?.persistentDataContainer?.remove(key)
        }
    }

    fun getCustomTag(item: ItemStack, keyName: String): String? {
        val meta = item.itemMeta ?: return null
        val key = NamespacedKey(Main.instance, keyName)

        return meta.persistentDataContainer.get(key, PersistentDataType.STRING)
    }
}
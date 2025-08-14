package com.nicon.explorerPaper.definitions

import com.nicon.explorerPaper.utils.PlayerUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
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

        /**
         * プレイヤー情報アイコンを取得
         */
        fun getPlayerInfoIcon(player: Player): ItemStack {
            val gold = PlayerUtils.PlayerDatabase.getGold(player)
            val level = PlayerUtils.PlayerDatabase.getLevel(player)
            val blocksMined = PlayerUtils.PlayerDatabase.getBlocksMined(player)

            val playerInfoIcon = ItemStack.of(Material.PLAYER_HEAD)
            playerInfoIcon.editMeta { meta ->
                meta.displayName(
                    Component
                        .text()
                        .decoration(TextDecoration.BOLD, true)
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("${player.name} の情報")
                        .build()
                )
                meta.lore(
                    listOf(
                        Component
                            .text()
                            .color(NamedTextColor.GOLD)
                            .decoration(TextDecoration.ITALIC, false)
                            .content("所持Gold: ${gold}G")
                            .build(),
                        Component
                            .text()
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false)
                            .content("レベル: $level")
                            .build(),
                        Component
                            .text()
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
                            .content("累計採掘回数: $blocksMined")
                            .build()
                    )
                )
            }

            return playerInfoIcon
        }

        fun getGridIcon(): ItemStack {
            val gridIcon = ItemStack.of(Material.GRAY_STAINED_GLASS)
            gridIcon.editMeta { meta ->
                meta.displayName(Component.text().content("").build())
            }

            return gridIcon
        }
    }
}
package com.nicon.explorerPaper.definitions

import com.nicon.explorerPaper.utils.ItemUtils
import com.nicon.explorerPaper.utils.PD
import com.nicon.explorerPaper.utils.PlayerUtils
import com.nicon.explorerPaper.utils.TF
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
        fun getPlayerInfoIcon(player: Player, isSkillMenu: Boolean = false): ItemStack {
            val gold = PD.getGold(player)
            val landLevel = PlayerUtils.getLevelWithType(player, PlayerUtils.LevelType.LAND)
            val woodLevel = PlayerUtils.getLevelWithType(player, PlayerUtils.LevelType.WOOD)
            val stoneLevel = PlayerUtils.getLevelWithType(player, PlayerUtils.LevelType.STONE)
            val oreLevel = PlayerUtils.getLevelWithType(player, PlayerUtils.LevelType.ORE)
            val blocksMined = PD.getBlocksMined(player)

            val playerInfoIcon = ItemStack.of(Material.PLAYER_HEAD)
            playerInfoIcon.editMeta { meta ->
                meta.displayName(
                    Component
                        .text()
                        .decoration(TextDecoration.BOLD, true)
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content(if (isSkillMenu) "[クリック] スキルメニュー ${player.name} の情報" else "${player.name} の情報")
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
                            .content("整地レベル: $landLevel")
                            .build(),
                        Component
                            .text()
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false)
                            .content("伐採レベル: $woodLevel")
                            .build(),
                        Component
                            .text()
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false)
                            .content("採石レベル: $stoneLevel")
                            .build(),
                        Component
                            .text()
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false)
                            .content("鉱石採掘レベル: $oreLevel")
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

        fun getExplorersChest(): ItemStack {
            val explorersChest = ItemStack.of(Material.CHEST)
            explorersChest.editMeta { meta ->
                meta.displayName(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .content("探検家のチェスト")
                        .build()
                )
                meta.lore(
                    listOf(
                        Component
                            .text()
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.WHITE)
                            .content("手に持って右クリックで開封")
                            .build()
                    )
                )
            }
            ItemUtils.addCustomTag(explorersChest, "explorersChest", "true")

            return explorersChest
        }

        fun getFruitsChest(): ItemStack {
            val fruitsCest = ItemStack.of(Material.CHEST)
            fruitsCest.editMeta { meta ->
                meta.displayName(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .content("森の恵みチェスト")
                        .build()
                )
                meta.lore(
                    listOf(
                        Component
                            .text()
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.WHITE)
                            .content("手に持って右クリックで開封")
                            .build()
                    )
                )
            }
            ItemUtils.addCustomTag(fruitsCest, "fruitsChest", "true")

            return fruitsCest
        }

        fun getBackMainMenu(): ItemStack {
            val backPageIcon = ItemStack.of(Material.COMPASS)
            backPageIcon.editMeta { meta ->
                meta.displayName(
                    Component
                        .text()
                        .decoration(TextDecoration.BOLD, true)
                        .decoration(TextDecoration.ITALIC, false)
                        .content("メインメニューに戻る")
                        .build()
                )
            }
            return backPageIcon
        }

        fun getBackPage(): ItemStack {
            val backPageIcon = ItemStack.of(Material.OAK_SIGN)
            backPageIcon.editMeta { meta ->
                meta.displayName(TF.parse("<reset><bold>戻る"))
            }
            return backPageIcon
        }

        fun getSap(): ItemStack {
            val sap = ItemStack.of(Material.HONEY_BOTTLE)
            sap.editMeta { meta ->
                meta.displayName(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .content("樹液")
                        .build()
                )
                meta.lore(
                    listOf(
                        Component
                            .text()
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.WHITE)
                            .content("飲むとマナの最大値が10増える")
                            .build()
                    )
                )
            }
            return sap
        }
    }
}
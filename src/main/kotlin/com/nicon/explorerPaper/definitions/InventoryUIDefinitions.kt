package com.nicon.explorerPaper.definitions

import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.enchants.EnchantScreen
import com.nicon.explorerPaper.recipes.CraftScreen
import com.nicon.explorerPaper.skills.SkillScreen
import com.nicon.explorerPaper.utils.GameUtils
import com.nicon.explorerPaper.utils.InventoryUI
import com.nicon.explorerPaper.utils.InventoryUI.Page
import com.nicon.explorerPaper.utils.InventoryUI.UISlot
import com.nicon.explorerPaper.utils.PlayerUtils
import com.nicon.explorerPaper.utils.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.floor

class InventoryUIDefinitions {
    companion object {
        fun openMenuUI(player: Player) {
            val inventoryUI = InventoryUI(player, "メニュー");

            val trashBoxIcon = ItemStack.of(Material.BONE_MEAL)
            trashBoxIcon.editMeta { meta ->
                meta.displayName(
                    Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
                        .content("ゴミ箱")
                        .build()
                )
                meta.lore(listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("いらないアイテムを破棄しよう")
                        .build(),
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, true)
                        .color(NamedTextColor.GRAY)
                        .content("Click Here!")
                        .build()
                ))
            }
            val teleportIcon = ItemStack.of(Material.ENDER_PEARL)
            teleportIcon.editMeta { meta ->
                meta.displayName(
                    Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
                        .content("テレポート")
                        .build()
                )
                meta.lore(listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("指定したプレイヤーやランダムな位置、地上にテレポート")
                        .build(),
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, true)
                        .color(NamedTextColor.GRAY)
                        .content("Click Here!")
                        .build()
                ))
            }
            val sellIcon = ItemStack.of(Material.GOLD_INGOT)
            sellIcon.editMeta { meta ->
                meta.displayName(
                    Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
                        .content("換金")
                        .build()
                )
                meta.lore(listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("アイテムをGoldに換金")
                        .build(),
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, true)
                        .color(NamedTextColor.GRAY)
                        .content("Click Here!")
                        .build()
                ))
            }
            val craftIcon = ItemStack.of(Material.CRAFTING_TABLE)
            craftIcon.editMeta { meta ->
                meta.displayName(
                    Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
                        .content("クラフト")
                        .build()
                )
                meta.lore(listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("素材を組み合わせてアイテムを作成")
                        .build(),
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, true)
                        .color(NamedTextColor.GRAY)
                        .content("Click Here!")
                        .build()
                ))
            }
            val initialToolsIcon = ItemStack.of(Material.WOODEN_PICKAXE)
            initialToolsIcon.editMeta { meta ->
                meta.displayName(
                    Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
                        .content("初期ツールを呼び出す")
                        .build()
                )
                meta.lore(listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("初期ツールを呼び出す 必要なくなったらゴミ箱に捨てよう")
                        .build(),
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, true)
                        .color(NamedTextColor.GRAY)
                        .content("Click Here!")
                        .build()
                ))
            }
            val materialsIcon = ItemStack.of(Material.OAK_LOG)
            materialsIcon.editMeta { meta ->
                meta.displayName(
                    Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
                        .content("所持素材一覧")
                        .build()
                )
                meta.lore(listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("所持している素材数を確認")
                        .build(),
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, true)
                        .color(NamedTextColor.GRAY)
                        .content("Click Here!")
                        .build()
                ))
            }
            val enchantIcon = ItemStack.of(Material.ENCHANTING_TABLE)
            enchantIcon.editMeta { meta ->
                meta.displayName(
                    Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
                        .content("エンチャント")
                        .build()
                )
                meta.lore(listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("ツールや装備にエンチャントを付与/除去/再抽選")
                        .build(),
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, true)
                        .color(NamedTextColor.GRAY)
                        .content("Click Here!")
                        .build()
                ))
            }
            val homePage = Page()
                .lockEmptySlots()
                .button(11, UISlot(teleportIcon) {
                    player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)

                    val randomTeleportIcon = ItemStack.of(Material.ENDER_EYE)
                    randomTeleportIcon.editMeta { meta ->
                        meta.displayName(
                            Component.text()
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.BOLD, true)
                                .content("ランダムな位置にテレポート")
                                .build()
                        )
                    }
                    val playerTeleportIcon = ItemStack.of(Material.PLAYER_HEAD)
                    playerTeleportIcon.editMeta { meta ->
                        meta.displayName(
                            Component.text()
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.BOLD, true)
                                .content("プレイヤーにテレポート")
                                .build()
                        )
                    }
                    val groundTeleportIcon = ItemStack.of(Material.GRASS_BLOCK)
                    groundTeleportIcon.editMeta { meta ->
                        meta.displayName(
                            Component.text()
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.BOLD, true)
                                .content("地上にテレポート")
                                .build()
                        )
                    }

                    val teleportPage = Page()
                        .button(20, UISlot(randomTeleportIcon) {
                            val x = floor((Math.random() * 20000) - 10000).toInt()
                            val z = floor((Math.random() * 20000) - 10000).toInt()
                            val y = player.world.getHighestBlockYAt(x, z)
                            player.teleport(Location(player.world, x.toDouble(), y.toDouble() + 1, z.toDouble()))
                        })
                        .button(22, UISlot(playerTeleportIcon) {

                        })
                        .button(24, UISlot(groundTeleportIcon) {
                            val highestY = player.world.getHighestBlockYAt(player.location)
                            player.teleport(Location(player.world, player.x, highestY.toDouble() + 1, player.z))
                        })
                    inventoryUI.pushPage(teleportPage)
                })
                .button(13, UISlot(craftIcon) {
                    player.playSound(player.location, Sound.BLOCK_CHEST_OPEN, 1f, 1f)

                    CraftScreen.openRecipeList(player, inventoryUI, 0)
                })
                .button(15, UISlot(sellIcon) {
                    player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)

                    val doSellIcon = ItemStack.of(Material.GOLD_INGOT)
                    doSellIcon.editMeta { meta ->
                        meta.displayName(
                            Component
                                .text()
                                .decoration(TextDecoration.BOLD, true)
                                .decoration(TextDecoration.ITALIC, false)
                                .content("アイテムを換金")
                                .build()
                        )
                        meta.lore(
                            listOf(
                                Component
                                    .text()
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(NamedTextColor.GOLD)
                                    .content("換金価格: 0Gold")
                                    .build()
                            )
                        )
                    }

                    val sellPage = Page()
                        .button(53, UISlot(doSellIcon) {
                            Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
                                player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)

                                for (slot in 0..<inventoryUI.inventory.size - 1) { //一番最後のスロットは除く
                                    val itemStack = inventoryUI.inventory.getItem(slot) ?: continue
                                    val itemDetail = Main.itemDetails[itemStack.type.key.toString()] ?: continue
                                    inventoryUI.inventory.setItem(slot, ItemStack.empty())

                                    val sellPrice = itemDetail.sellPrice * itemStack.amount
                                    val currentGold = PlayerUtils.PlayerDatabase.getGold(player) ?: 0
                                    PlayerUtils.PlayerDatabase.setGold(player, currentGold + sellPrice)
                                }

                                PlayerUtils.refreshSidebar(player)
                            }, 1)
                        })
                        .onClick {
                            Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
                                val itemStacks = inventoryUI.inventory.contents
                                    .filterIndexed { index, _ ->
                                        index != 53
                                    }
                                    .filterNotNull()
                                val sellPrice = GameUtils.calculateSellPrice(itemStacks)
                                doSellIcon.editMeta { meta ->
                                    meta.lore(
                                        listOf(
                                            Component
                                                .text()
                                                .decoration(TextDecoration.ITALIC, false)
                                                .color(NamedTextColor.GOLD)
                                                .content("換金価格: ${Utils.addCommaToNumber(sellPrice)}Gold")
                                                .build()
                                        )
                                    )
                                }
                                inventoryUI.inventory.setItem(53, doSellIcon)
                            }, 1)
                        }

                    inventoryUI.pushPage(sellPage)
                })
                .button(22, UISlot(ItemDefinitions.getPlayerInfoIcon(player)) {
                    SkillScreen.openLevelsMenu(inventoryUI, player)
                })
                .button(29, UISlot(initialToolsIcon) {
                    player.playSound(player.location, Sound.BLOCK_STONE_BREAK, 1f, 1f)
                    PlayerUtils.clearItems(player, Material.WOODEN_PICKAXE.key.toString())
                    PlayerUtils.clearItems(player, Material.WOODEN_SHOVEL.key.toString())
                    PlayerUtils.clearItems(player, Material.WOODEN_AXE.key.toString())

                    val woodenPickaxe = ItemStack.of(Material.WOODEN_PICKAXE)
                    val woodenAxe = ItemStack.of(Material.WOODEN_AXE)
                    val woodenShovel = ItemStack.of(Material.WOODEN_SHOVEL)
                    player.inventory.addItem(woodenPickaxe)
                    player.inventory.addItem(woodenAxe)
                    player.inventory.addItem(woodenShovel)
                })
                .button(31, UISlot(enchantIcon) {
                    EnchantScreen.openEnchantMenu(inventoryUI, player)
                })
                .button(33, UISlot(trashBoxIcon) {
                    player.playSound(player.location, Sound.BLOCK_COMPOSTER_FILL, 1f, 1f)

                    val itemsTrashIcon = ItemStack.of(Material.BONE_MEAL)
                    itemsTrashIcon.editMeta { meta ->
                        meta.displayName(
                            Component.text()
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.BOLD, true)
                                .content("アイテムを破棄する")
                                .build()
                        )
                        meta.lore(listOf(
                            Component.text()
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.RED)
                                .content("元に戻すことはできません")
                                .build()
                        )
                        )
                    }

                    val trashPage = Page()
                        .button(53, UISlot(itemsTrashIcon) {
                            player.playSound(player.location, Sound.BLOCK_ANVIL_LAND, 1f, 1f)

                            for (slot in 0..52) {
                                inventoryUI.inventory.setItem(slot, ItemStack.empty())
                            }
                        })
                    inventoryUI.pushPage(trashPage)
                })

            inventoryUI.replacePage(homePage)
            inventoryUI.show()
        }
    }
}
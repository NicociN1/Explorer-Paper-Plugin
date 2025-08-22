package com.nicon.explorerPaper.enchants

import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.definitions.ItemDefinitions
import com.nicon.explorerPaper.utils.InventoryUI
import com.nicon.explorerPaper.utils.InventoryUI.Page
import com.nicon.explorerPaper.utils.InventoryUI.UISlot
import com.nicon.explorerPaper.utils.PD
import com.nicon.explorerPaper.utils.PlayerUtils
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.Random
import kotlin.math.floor

object EnchantScreen {
    const val GIVE_ENCHANT_AMETHYST_COST = 30
    const val GIVE_ENCHANT_LAPIS_LAZULI = 8
    const val REROLL_ENCHANT_AMETHYST_COST = 30
    const val REROLL_ENCHANT_REDSTONE = 8
    const val DISASSEMBLE_ENCHANT_GOLD_COST = 10000

    fun openEnchantMenu(inventoryUI: InventoryUI, player: Player) {
        val enchantPage = Page().lockEmptySlots()

        val grantingIcon = ItemStack.of(Material.ENCHANTED_BOOK)
        grantingIcon.editMeta { meta ->
            meta.displayName(
                Component
                    .text()
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true)
                    .content("付与")
                    .build()
            )
            meta.lore(
                listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("ツールにエンチャントを付与する")
                        .build()
                )
            )
        }

        val rerollIcon = ItemStack.of(Material.ANVIL)
        rerollIcon.editMeta { meta ->
            meta.displayName(
                Component
                    .text()
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true)
                    .content("リロール")
                    .build()
            )
            meta.lore(
                listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("ツールのエンチャントを再抽選する")
                        .build()
                )
            )
        }

        val disassembleIcon = ItemStack.of(Material.DIAMOND_AXE)
        disassembleIcon.editMeta { meta ->
            meta.displayName(
                Component
                    .text()
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true)
                    .content("分解")
                    .build()
            )
            meta.lore(
                listOf(
                    Component
                        .text()
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE)
                        .content("ツールのエンチャントを分解してアメジストに変換する")
                        .build()
                )
            )
        }

        enchantPage
            .button(20, UISlot(grantingIcon) {
                openGranting(inventoryUI, player)
            })
            .button(22, UISlot(rerollIcon) {
                openReroll(inventoryUI, player)
            })
            .button(24, UISlot(disassembleIcon) {
                openDisassemble(inventoryUI, player)
            })

        inventoryUI.pushPage(enchantPage)
    }

    fun openGranting(inventoryUI: InventoryUI, player: Player) {
        val gridIcon = ItemDefinitions.getGridIcon()
        val doGrantingIcon = ItemStack.of(Material.ENCHANTED_BOOK)
        doGrantingIcon.editMeta { meta ->
            meta.displayName(
                Component
                    .text()
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true)
                    .content("エンチャントを付与する")
                    .build()
            )
            val lore = mutableListOf<Component>()

            val amethyst = PD.getAmethyst(player)
            lore.add(
                Component
                    .text()
                    .decoration(TextDecoration.ITALIC, false)
                    .color(if (amethyst >= GIVE_ENCHANT_AMETHYST_COST) NamedTextColor.LIGHT_PURPLE else NamedTextColor.RED)
                    .content("消費Amethyst: ${GIVE_ENCHANT_AMETHYST_COST}A")
                    .build()
            )

            val lapisAmount = PlayerUtils.getItemAmount(player, "minecraft:lapis_lazuli")
            lore.add(
                Component
                    .text()
                    .decoration(TextDecoration.ITALIC, false)
                    .color(if (lapisAmount >= GIVE_ENCHANT_LAPIS_LAZULI) NamedTextColor.WHITE else NamedTextColor.RED)
                    .content("消費アイテム: ラピスラズリx${GIVE_ENCHANT_LAPIS_LAZULI}")
                    .build()
            )

            meta.lore(lore)
        }

        val grantingPage = Page()

        for (slot in 0..<inventoryUI.inventory.size) {
            when(slot) {
                22 -> {
                }

                31 -> {
                    grantingPage.button(slot, UISlot(doGrantingIcon) {
                        val itemStack = inventoryUI.inventory.getItem(22) ?: return@UISlot

                        val canEnchantCount = getEnchantableCount(itemStack)

                        val amethyst = PD.getAmethyst(player)
                        val lapisAmount = PlayerUtils.getItemAmount(player, "minecraft:lapis_lazuli")

                        if (canEnchantCount == 0 || amethyst < GIVE_ENCHANT_AMETHYST_COST || lapisAmount < GIVE_ENCHANT_LAPIS_LAZULI || itemStack.enchantments.isNotEmpty()) {
                            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f)
                            return@UISlot
                        }

                        val enchants: MutableList<Pair<Enchantment, Int>> = mutableListOf()
                        val rollCount = Random().nextInt(canEnchantCount) + 1

                        for (i in 0..<rollCount) {
                            val enchant = rollEnchant(itemStack) ?: return@UISlot
                            enchants.add(enchant)
                        }
                        itemStack.removeEnchantments()
                        for (enchant in enchants) {
                            itemStack.addEnchantment(enchant.first, enchant.second)
                        }

                        PD.setAmethyst(player, amethyst - GIVE_ENCHANT_AMETHYST_COST)
                        PlayerUtils.clearItems(player, "minecraft:lapis_lazuli", GIVE_ENCHANT_LAPIS_LAZULI)

                        inventoryUI.inventory.setItem(22, ItemStack.empty())
                        PlayerUtils.safeAddItem(player, itemStack)

                        player.playSound(player.location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f)
                    })
                }

                else -> {
                    grantingPage.button(slot, UISlot(gridIcon) {})
                }
            }

        }

        inventoryUI.pushPage(grantingPage)
    }

    fun openReroll(inventoryUI: InventoryUI, player: Player) {
        val gridIcon = ItemDefinitions.getGridIcon()
        val doRerollIcon = ItemStack.of(Material.ANVIL)
        doRerollIcon.editMeta { meta ->
            meta.displayName(
                Component
                    .text()
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true)
                    .content("エンチャントをリロールする")
                    .build()
            )
            val lore = mutableListOf<Component>()

            val amethyst = PD.getAmethyst(player)
            lore.add(
                Component
                    .text()
                    .decoration(TextDecoration.ITALIC, false)
                    .color(if (amethyst >= REROLL_ENCHANT_AMETHYST_COST) NamedTextColor.LIGHT_PURPLE else NamedTextColor.RED)
                    .content("消費Amethyst: ${REROLL_ENCHANT_AMETHYST_COST}A")
                    .build()
            )

            val redstoneAmount = PlayerUtils.getItemAmount(player, "minecraft:redstone")
            lore.add(
                Component
                    .text()
                    .decoration(TextDecoration.ITALIC, false)
                    .color(if (redstoneAmount >= REROLL_ENCHANT_REDSTONE) NamedTextColor.WHITE else NamedTextColor.RED)
                    .content("消費アイテム: レッドストーンx${REROLL_ENCHANT_REDSTONE}")
                    .build()
            )

            meta.lore(lore)
        }

        val rerollPage = Page()

        for (slot in 0..<inventoryUI.inventory.size) {
            when(slot) {
                22 -> {
                }

                31 -> {
                    rerollPage.button(slot, UISlot(doRerollIcon) {
                        val itemStack = inventoryUI.inventory.getItem(22) ?: return@UISlot

                        val canEnchantCount = getEnchantableCount(itemStack)

                        val amethyst = PD.getAmethyst(player)
                        val redstoneAmount = PlayerUtils.getItemAmount(player, "minecraft:redstone")

                        if (canEnchantCount == 0 || amethyst < REROLL_ENCHANT_AMETHYST_COST || redstoneAmount < REROLL_ENCHANT_REDSTONE || itemStack.enchantments.isEmpty()) {
                            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 0.8f)
                            return@UISlot
                        }

                        val enchants: MutableList<Pair<Enchantment, Int>> = mutableListOf()
                        val rollCount = Random().nextInt(canEnchantCount) + 1

                        for (i in 0..<rollCount) {
                            val enchant = rollEnchant(itemStack) ?: return@UISlot
                            enchants.add(enchant)
                        }
                        itemStack.removeEnchantments()
                        for (enchant in enchants) {
                            itemStack.addEnchantment(enchant.first, enchant.second)
                        }

                        PD.setAmethyst(player, amethyst - REROLL_ENCHANT_AMETHYST_COST)
                        PlayerUtils.clearItems(player, "minecraft:redstone", REROLL_ENCHANT_REDSTONE)

                        inventoryUI.inventory.setItem(22, ItemStack.empty())
                        PlayerUtils.safeAddItem(player, itemStack)

                        player.playSound(player.location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f)
                    })
                }

                else -> {
                    rerollPage.button(slot, UISlot(gridIcon) {})
                }
            }

        }

        inventoryUI.pushPage(rerollPage)
    }

    fun openDisassemble(inventoryUI: InventoryUI, player: Player) {
        val gridIcon = ItemDefinitions.getGridIcon()
        val doDisassembleIcon = ItemStack.of(Material.DIAMOND_AXE)
        doDisassembleIcon.editMeta { meta ->
            meta.displayName(
                Component
                    .text()
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true)
                    .content("エンチャントを分解する")
                    .build()
            )
            val lore = mutableListOf<Component>()

            val gold = PD.getGold(player)
            lore.add(
                Component
                    .text()
                    .decoration(TextDecoration.ITALIC, false)
                    .color(if (gold >= DISASSEMBLE_ENCHANT_GOLD_COST) NamedTextColor.GOLD else NamedTextColor.RED)
                    .content("消費Gold: ${DISASSEMBLE_ENCHANT_GOLD_COST}G")
                    .build()
            )

            meta.lore(lore)
        }

        val disassemblePage = Page()

        for (slot in 0..<inventoryUI.inventory.size) {
            when(slot) {
                22 -> {
                }

                31 -> {
                    disassemblePage.button(slot, UISlot(doDisassembleIcon) {
                        val itemStack = inventoryUI.inventory.getItem(22) ?: return@UISlot

                        val canEnchantCount = getEnchantableCount(itemStack)

                        val gold = PD.getGold(player)

                        if (canEnchantCount == 0 || gold < DISASSEMBLE_ENCHANT_GOLD_COST || itemStack.enchantments.isEmpty()) {
                            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 0.8f)
                            return@UISlot
                        }

                        PD.setGold(player, gold - DISASSEMBLE_ENCHANT_GOLD_COST)

                        val amethyst = PD.getAmethyst(player)

                        val addAmethyst = calculateAmethyst(itemStack)

                        PD.setAmethyst(player, amethyst + addAmethyst)

                        itemStack.removeEnchantments()

                        inventoryUI.inventory.setItem(22, ItemStack.empty())
                        PlayerUtils.safeAddItem(player, itemStack)

                        player.playSound(player.location, Sound.BLOCK_ANVIL_LAND, 1f, 1f)
                        player.sendMessage(
                            Component
                                .text()
                                .color(NamedTextColor.LIGHT_PURPLE)
                                .content("エンチャントの分解に成功し、${addAmethyst}Amethystを手に入れた！")
                                .build()
                        )
                        PlayerUtils.refreshSidebar(player)
                    })
                }

                else -> {
                    disassemblePage.button(slot, UISlot(gridIcon) {})
                }
            }

        }

        inventoryUI.pushPage(disassemblePage)
    }

    fun rollEnchant(itemStack: ItemStack): Pair<Enchantment, Int>? {
        val itemId = itemStack.type.key.toString()
        val enchantDetail = Main.enchantDetails.entries.firstOrNull{ (regexStr, _) ->
            Regex(regexStr).matches(itemId)
        }?.value ?: return null

        val totalWeight = enchantDetail.values.sum()

        val rand = Random().nextInt(totalWeight)

        var cumulative = 0
        val selected = enchantDetail.entries.first { entry ->
            cumulative += entry.value
            rand < cumulative
        }

        val key = NamespacedKey.fromString(selected.key)
        if (key == null) {
            Main.instance.logger.info("エンチャントKeyがnullです")
            return null
        }
        val enchantRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
        val enchantment = enchantRegistry.get(key)
        if (enchantment == null) {
            Main.instance.logger.info("Enchantmentがnullです")
            return null
        }
        return Pair(enchantment, Random().nextInt(enchantment.maxLevel) + 1)
    }

    fun getEnchantableCount(itemStack: ItemStack): Int {
        val itemId = itemStack.type.key.toString()
        val enchantDetail = Main.enchantDetails.entries.firstOrNull{ (regexStr, _) ->
            Regex(regexStr).matches(itemId)
        }?.value ?: return 0

        return enchantDetail.size
    }

    fun calculateAmethyst(itemStack: ItemStack): Int {
        var addAmethyst = 0
        for (enchant in itemStack.enchantments) {
            addAmethyst += (floor(enchant.value.toDouble() / enchant.key.maxLevel.toDouble()) * 15).toInt()
        }

        return addAmethyst
    }
}
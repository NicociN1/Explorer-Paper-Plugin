package com.nicon.explorerPaper.events.player

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.definitions.InventoryUIDefinitions
import com.nicon.explorerPaper.definitions.ItemDefinitions
import com.nicon.explorerPaper.definitions.Constants
import com.nicon.explorerPaper.utils.GameUtils
import com.nicon.explorerPaper.utils.ItemUtils
import com.nicon.explorerPaper.utils.PD
import com.nicon.explorerPaper.utils.PlayerUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import java.util.UUID

class PlayerEvents : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        event.joinMessage(
            Component.text()
                .color(NamedTextColor.LIGHT_PURPLE)
                .content("[参加] ${player.name}")
                .build()
        )

        PlayerUtils.clearItems(player, Material.COMPASS.key.toString())
        player.inventory.addItem(ItemDefinitions.getMenuItem())

        if (!player.hasPlayedBefore()) {
            PlayerUtils.clearItems(player, Material.WOODEN_PICKAXE.key.toString())
            PlayerUtils.clearItems(player, Material.WOODEN_SHOVEL.key.toString())
            PlayerUtils.clearItems(player, Material.WOODEN_AXE.key.toString())

            val woodenPickaxe = ItemStack.of(Material.WOODEN_PICKAXE)
            val woodenAxe = ItemStack.of(Material.WOODEN_AXE)
            val woodenShovel = ItemStack.of(Material.WOODEN_SHOVEL)
            player.inventory.addItem(woodenPickaxe)
            player.inventory.addItem(woodenAxe)
            player.inventory.addItem(woodenShovel)
        }

        for (slot in 0..<player.inventory.size) {
            val itemStack = player.inventory.getItem(slot) ?: continue
            player.inventory.setItem(slot, itemStack)
        }

        val board = Bukkit.getScoreboardManager().newScoreboard
        val objective = board.registerNewObjective(
            "sidebar",
            Criteria.DUMMY,
            Component.text().content("<Explorer Server>").decoration(TextDecoration.BOLD, true).build()
        )
        objective.displaySlot = DisplaySlot.SIDEBAR
        player.scoreboard = board

        PlayerUtils.refreshLevelUnlockRecipe(player)
        PlayerUtils.refreshSidebar(player)
        PlayerUtils.refreshManaBossBar(player)
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        event.quitMessage(
            Component.text()
                .content("[退出] ${event.player.name}")
                .color(NamedTextColor.LIGHT_PURPLE)
                .build()
        )

        PlayerUtils.expBossBars.remove(event.player.uniqueId)
        PlayerUtils.manaBossBars.remove(event.player.uniqueId)
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        Main.instance.logger.info(event.cause.toString())
        event.isCancelled = true
    }

    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent) {
        val player = event.player
        if (player.gameMode == GameMode.CREATIVE) return
        val itemStack = event.itemDrop.itemStack
        if (Constants.DISABLED_DROP_ITEMS.contains(itemStack.type.key.toString())) {
            event.isCancelled = true
        }
    }

    val itemInteractCooldown: MutableList<UUID> = mutableListOf()

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (itemInteractCooldown.contains(player.uniqueId)) {
                player.sendMessage(
                    Component
                        .text("クールダウン中です")
                )
                return
            }
            itemInteractCooldown.add(player.uniqueId)
            Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
                itemInteractCooldown.remove(player.uniqueId)
            }, 5L)

            val handItem = player.inventory.itemInMainHand

            event.isCancelled = true
            when (handItem.type) {
                Material.COMPASS -> {
                    InventoryUIDefinitions.openMenuUI(player)
                }
                Material.CHEST -> {
                    when {
                        ItemUtils.getCustomTag(handItem, "explorersChest") == "true" -> {
                            val isSuccess = GameUtils.openLootChest(player, "ex:chests/explorers_chest", "探検家のチェスト")
                            if (isSuccess) {
                                PlayerUtils.clearHandItems(player, 1)
                                player.playSound(player.location, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
                            }
                        }
                        ItemUtils.getCustomTag(handItem, "fruitsChest") == "true" -> {
                            val isSuccess = GameUtils.openLootChest(player, "ex:chests/fruits_chest", "森の恵みチェスト")
                            if (isSuccess) {
                                PlayerUtils.clearHandItems(player, 1)
                                player.playSound(player.location, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
                            }
                        }
                    }
                }
                Material.HONEY_BOTTLE -> {
                    PlayerUtils.clearItems(player, "minecraft:honey_bottle", 1)
                    val currentMaxMana = PD.getMaxMana(player)
                    PD.setMaxMana(player, currentMaxMana + 10)
                    player.sendMessage(
                        Component
                            .text()
                            .color(NamedTextColor.GOLD)
                            .content("樹液を飲み、マナの最大値が10増加した！")
                    )
                    player.playSound(player.location, Sound.ITEM_HONEY_BOTTLE_DRINK, 1f, 1f)
                    player.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f)

                    PlayerUtils.refreshManaBossBar(player)
                }
                Material.APPLE -> {
                    PlayerUtils.clearItems(player, "minecraft:apple", 1)
                    PlayerUtils.addMana(player, 50)
                    player.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f)
                }
                Material.GOLDEN_APPLE -> {
                    PlayerUtils.clearItems(player, "minecraft:golden_apple", 1)
                    PlayerUtils.addMana(player, 300)
                    player.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f)
                }
                Material.ENCHANTED_GOLDEN_APPLE -> {
                    PlayerUtils.clearItems(player, "minecraft:enchanted_golden_apple", 1)
                    PlayerUtils.addMana(player, 1000)
                    player.playSound(player.location, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1f)
                }
                else -> {
                    event.isCancelled = false
                }
            }

            if (event.action == Action.RIGHT_CLICK_BLOCK && Constants.DISABLED_INTERACT_BLOCKS.contains(block?.type)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        if (player.gameMode == GameMode.CREATIVE) return
        if (Constants.ENABLED_PLACE_BLOCKS.contains(event.itemInHand.type.key.toString())) return
        event.isCancelled = true
    }

    @EventHandler
    fun onItemDamage(event: PlayerItemDamageEvent) {
        val itemId = event.item.type.key.toString()

        if (Constants.DISABLED_DURABILITY_ITEMS.contains(itemId)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onArmorChange(event: PlayerArmorChangeEvent) {
        val player = event.player
        val newItemStack = event.newItem
        val oldItemStack = event.oldItem

        for (enchant in newItemStack.enchantments) {
            when (enchant.key.key.toString()) {
                "ex:night_vision" -> {
                    player.addPotionEffect(
                        PotionEffect(
                            PotionEffectType.NIGHT_VISION,
                            Int.MAX_VALUE,
                            1,
                            false,
                            false,
                            true
                        )
                    )
                }
                "ex:flight" -> {
                    player.allowFlight = true
                }
            }
        }

        for (enchant in oldItemStack.enchantments) {
            when (enchant.key.key().toString()) {
                "ex:night_vision" -> {
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION)
                }
                "ex:flight" -> {
                    player.allowFlight = false
                }
            }
        }
    }

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onEat(event: PlayerItemConsumeEvent) {
        event.isCancelled = true
    }
}

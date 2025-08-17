package com.nicon.explorerPaper.events.player

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.definitions.InventoryUIDefinitions
import com.nicon.explorerPaper.definitions.ItemDefinitions
import com.nicon.explorerPaper.definitions.Constants
import com.nicon.explorerPaper.utils.GameUtils
import com.nicon.explorerPaper.utils.ItemUtils
import com.nicon.explorerPaper.utils.PlayerUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot

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

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            val handItem = player.inventory.getItem(EquipmentSlot.HAND)
            val itemId = handItem.type.key.toString()

            when (itemId) {
                "minecraft:compass" -> {
                    InventoryUIDefinitions.openMenuUI(player)
                }
                "minecraft:chest" -> {
                    val isExplorersChest = ItemUtils.getCustomTag(handItem, "explorersChest") == "true"
                    if (isExplorersChest) {
                        GameUtils.openLootChest(player, "ex:chests/explorers_chest", "探検家のチェスト")
                    }
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
            when (enchant.key.key().toString()) {
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
}

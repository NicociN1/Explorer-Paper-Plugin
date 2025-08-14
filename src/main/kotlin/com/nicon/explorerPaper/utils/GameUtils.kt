package com.nicon.explorerPaper.utils

import com.nicon.explorerPaper.Main
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer
import kotlin.math.floor
import kotlin.math.min

object GameUtils {
    fun spawnGold(goldValue: Int, location: Location) {
        val goldFunc: Consumer<Item> = Consumer<Item> { item ->
            item.pickupDelay = 99999
            val despawnDelay = floor(((Math.random() * 0.3) + 2) * 20).toLong()
            Main.instance.server.scheduler.runTaskLater(Main.instance, Runnable {
                item.remove()
            }, despawnDelay)
        }

        if (goldValue < 10) {
            for (i in 1..goldValue) {
                location.world.dropItem(location, ItemStack.of(Material.GOLD_NUGGET), goldFunc)
            }
        } else if (goldValue < 100) {
            repeat(goldValue / 10) {
                location.world.dropItem(location, ItemStack.of(Material.GOLD_INGOT), goldFunc)
            }
        } else if (goldValue < 1000) {
            repeat(min( goldValue / 100, 10)) {
                location.world.dropItem(location, ItemStack.of(Material.GOLD_INGOT), goldFunc)
            }
        }
    }

    fun spawnAmethyst(amethystValue: Int, location: Location) {
        repeat(min(amethystValue, 10)) {
            location.world.dropItem(location, ItemStack.of(Material.AMETHYST_SHARD), { item ->
                item.pickupDelay = 99999
                val despawnDelay = floor(((Math.random() * 0.3) + 2) * 20).toLong()
                Main.instance.server.scheduler.runTaskLater(Main.instance, Runnable {
                    item.remove()
                }, despawnDelay)
            })
        }
    }

    fun calculateSellPrice(itemStacks: Collection<ItemStack>): Int {
        var gold = 0

        for (itemStack in itemStacks) {
            val itemDetail = Main.itemDetails[itemStack.type.key.toString()] ?: continue
            gold += itemDetail.sellPrice * itemStack.amount
        }

        return gold
    }

    fun setItemDetail(itemStack: ItemStack): ItemStack {
        val newItemStack = itemStack.clone()
        val itemDetail = Main.itemDetails[itemStack.type.key.toString()] ?: return newItemStack
        newItemStack.editMeta { meta ->
            meta.lore(
                listOf(
                    Component
                        .text()
                        .color(NamedTextColor.GOLD)
                        .content("価値: ${itemDetail.sellPrice}G")
                        .build()
                )
            )
        }

        return newItemStack
    }
}
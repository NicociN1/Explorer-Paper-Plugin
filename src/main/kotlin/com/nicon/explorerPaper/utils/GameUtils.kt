package com.nicon.explorerPaper.utils

import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.blocks.BlockData
import com.nicon.explorerPaper.blocks.BlockData.BlockDetail
import com.nicon.explorerPaper.blocks.BlockHandlers
import com.nicon.explorerPaper.skills.SkillData
import com.nicon.explorerPaper.skills.SkillData.SkillDetail
import com.nicon.explorerPaper.utils.PlayerUtils.LevelType
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.LootContext
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

    fun openLootChest(player: Player, lootTablePath: String, title: String) {
        val inventory = Bukkit.createInventory(null, InventoryType.CHEST, Component.text(title))
        val path = NamespacedKey.fromString(lootTablePath) ?: return
        val loottable = Bukkit.getLootTable(path) ?: return

        val lootContext = LootContext.Builder(player.location).build()
        loottable.fillInventory(inventory, null, lootContext)
        player.openInventory(inventory)
    }

    fun getBlockDetail(block: Block): BlockDetail? {
        val blockId = block.type.key.toString()
        return Main.blockDetails.entries.firstOrNull { (regexStr, _) ->
            Regex(regexStr).matches(blockId)
        }?.value
    }

    fun areaMining(player: Player, block: Block, levelType: LevelType, size: Int) {
        val rayTrace = player.rayTraceBlocks(10.0) ?: return
        val hitBlock = rayTrace.hitBlock ?: return
        val face = rayTrace.hitBlockFace ?: return

        if (hitBlock != block) return
        val xHalf = if (face == BlockFace.EAST || face == BlockFace.WEST) 0 else size / 2
        val zHalf = if (face == BlockFace.SOUTH || face == BlockFace.NORTH) 0 else size / 2
        val yRange = if (face == BlockFace.UP || face == BlockFace.DOWN) 0..0 else (0..<size)
        for (dx in -xHalf..xHalf) {
            for (dy in yRange) {
                for (dz in -zHalf..zHalf) {
                    val target = block.location.clone().add(dx.toDouble(), dy.toDouble(), dz.toDouble()).block

                    if (block == target) continue
                    if (getBlockDetail(target)?.levelType != levelType) continue

                    BlockHandlers.onBreak(player, target)
                    target.type = Material.AIR
                }
            }
        }
    }

    fun allMining(player: Player, startBlock: Block, allowBlocks: Array<Material>, limit: Int = 256) {
        val blocksToBreak = mutableSetOf<Block>()
        collectAdjacentBlocks(startBlock, allowBlocks, blocksToBreak, limit)

        if (blocksToBreak.size <= limit) {
            for (block in blocksToBreak) {
                if (block.type != Material.AIR) {
                    if (startBlock == block) continue
                    BlockHandlers.onBreak(player, block)
                    block.type = Material.AIR
                }
            }
        }
    }

    private fun collectAdjacentBlocks(block: Block, allowBlocks: Array<Material>, collected: MutableSet<Block>, limit: Int) {
        if (block.type == Material.AIR) return
        if (!collected.add(block)) return

        val faces = listOf(
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST
        )

        for (face in faces) {
            val neighbor = block.getRelative(face)
            if (!allowBlocks.contains(neighbor.type)) continue
            collectAdjacentBlocks(neighbor, allowBlocks, collected, limit)
        }
    }

    fun getSkillDetail(levelType: LevelType, id: String): SkillDetail? {
        val skillDetails = Main.skillDetails[levelType] ?: return null
        return skillDetails.first { detail -> detail.id == id }
    }
}
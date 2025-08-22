package com.nicon.explorerPaper.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nicon.explorerPaper.Database
import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.blocks.BlockData.BlockDetail
import com.nicon.explorerPaper.blocks.BlockHandlers
import com.nicon.explorerPaper.definitions.Constants
import com.nicon.explorerPaper.model.PlayerStorages
import com.nicon.explorerPaper.skills.SkillData.SkillDetail
import com.nicon.explorerPaper.utils.PlayerUtils.LevelType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.LootContext
import java.util.UUID
import java.util.function.Consumer
import kotlin.collections.mutableMapOf
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

    val openLootChestCooldown: MutableList<UUID> = mutableListOf()

    fun openLootChest(player: Player, lootTablePath: String, title: String): Boolean {
        if (openLootChestCooldown.contains(player.uniqueId)) return false
        val key = NamespacedKey.fromString(lootTablePath) ?: return false

        val inventory = Bukkit.createInventory(null, 9 * 3, Component.text(title))
        val lootTable = Bukkit.getLootTable(key) ?: return false
        lootTable.fillInventory(inventory, null, LootContext.Builder(player.location).build())
        player.openInventory(inventory)

        openLootChestCooldown.add(player.uniqueId)
        Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
            openLootChestCooldown.remove(player.uniqueId)
        }, 20L)
        return true
    }

    fun getBlockDetail(block: Block): BlockDetail? {
        val blockId = block.type.key.toString()
        return Main.blockDetails.entries.firstOrNull { (regexStr, _) ->
            Regex(regexStr).matches(blockId)
        }?.value
    }

    /**
     * 指定した範囲を破壊する
     * @param levelType 破壊を許可するブロックのlevelType
     * @param manaPerBlock 1ブロックにつき消費するマナ
     */
    fun areaMining(player: Player, startBlock: Block, levelType: LevelType, width: Int, height: Int, manaPerBlock: Int) {
        val startMana = PD.getMana(player)
        val requireMana = manaPerBlock * (width * height - 1)
        if (startMana < requireMana) {
            onManaNotEnough(player, requireMana)
            return
        }

        val rayTrace = player.rayTraceBlocks(10.0) ?: return
        val hitBlock = rayTrace.hitBlock ?: return
        val face = rayTrace.hitBlockFace ?: return

        if (face == BlockFace.UP || face == BlockFace.DOWN) return

        if (hitBlock != startBlock) return
        val xHalf = if (face == BlockFace.EAST || face == BlockFace.WEST) 0 else width / 2
        val zHalf = if (face == BlockFace.SOUTH || face == BlockFace.NORTH) 0 else width / 2
        val yRange = 0..<height
        for (dx in -xHalf..xHalf) {
            for (dy in yRange) {
                for (dz in -zHalf..zHalf) {
                    val target = startBlock.location.clone().add(dx.toDouble(), dy.toDouble(), dz.toDouble()).block

                    if (startBlock == target) continue
                    if (getBlockDetail(target)?.levelType != levelType) continue

                    val currentMana = PD.getMana(player)
                    if (manaPerBlock > currentMana) return
                    PlayerUtils.addMana(player, -manaPerBlock)

                    BlockHandlers.onBreak(player, target)
                    target.breakNaturally(true)
                }
            }
        }
    }

    /**
     * 指定した回数ブロックを破壊する
     * @param allowBlocks 破壊を許可するブロックリスト
     * @param manaPerBlock 1ブロックにつき消費するマナ
     */
    fun countMining(player: Player, startBlock: Block, allowBlocks: Array<Material>, limit: Int, manaPerBlock: Int) {
        val startMana = PD.getMana(player)
        val requireMana = manaPerBlock * limit
        if (startMana < requireMana) {
            onManaNotEnough(player, requireMana)
            return
        }

        var limitCount = 0

        fun breakBlocks(block: Block) {
            Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
                for (face in Constants.faces) {
                    val neighbor = block.getRelative(face)
                    if (!allowBlocks.contains(neighbor.type)) continue

                    if (limitCount > limit) return@Runnable
                    limitCount++

                    val currentMana = PD.getMana(player)
                    if (currentMana < manaPerBlock) return@Runnable
                    PlayerUtils.addMana(player, -manaPerBlock)

                    BlockHandlers.onBreak(player, neighbor)
                    neighbor.breakNaturally(true)

                    breakBlocks(neighbor)
                }
            }, 2L)
        }

        breakBlocks(startBlock)
    }

    val manaMesssageCooldown: MutableList<UUID> = mutableListOf()

    fun onManaNotEnough(player: Player, requireMana: Int) {
        if (manaMesssageCooldown.contains(player.uniqueId)) return
        player.sendMessage(
            Component
                .text()
                .color(NamedTextColor.RED)
                .content("マナが足りません (必要: ${requireMana})")
                .build()
        )
        manaMesssageCooldown.add(player.uniqueId)
        Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
            manaMesssageCooldown.remove(player.uniqueId)
        }, 60L)
    }

    fun getSkillDetail(levelType: LevelType, id: String): SkillDetail? {
        val skillDetails = Main.skillDetails[levelType] ?: return null
        return skillDetails.first { detail -> detail.id == id }
    }

    val gson = Gson()

    fun getPlayerStorage(player: Player, storageSlot: Int): MutableMap<Int, ItemStack>? {
        val storagesObj = getStoragesObj(player)
        val storageObj = storagesObj[storageSlot.toString()] ?: return null
        return storageObj.mapValues { (_, value) -> ItemStack.deserialize(value) }.mapKeys { (key) -> key.toInt() }.toMutableMap()
    }
    fun savePlayerStorage(player: Player, storageSlot: Int, storageData: MutableMap<Int, ItemStack>) {
        val storagesObj = getStoragesObj(player)
        val storageObj = storageData.mapValues { (_, value) -> value.serialize() }.toMutableMap()

        storagesObj[storageSlot.toString()] = storageObj.mapKeys { (key) -> key.toString() }.toMap().toMutableMap()

        val enderChestsJson = gson.toJson(storagesObj)

        Database.playerStorageDao.createIfNotExists(
            PlayerStorages(
                player.uniqueId.toString()
            )
        )

        val updateBuilder = Database.playerStorageDao.updateBuilder()
        updateBuilder.updateColumnValue("storages", enderChestsJson)
        updateBuilder.where().eq("uuid", player.uniqueId.toString())
        updateBuilder.update()
    }

    private fun getStoragesObj(player: Player): MutableMap<String, MutableMap<String, MutableMap<String, Any>>> {
        val storageDatabase = Database.playerStorageDao.queryForId(player.uniqueId.toString())
            ?: return mutableMapOf()

        val type = object : TypeToken<MutableMap<String, MutableMap<String, MutableMap<String, Any>>>>() {}.type
        return try {
            gson.fromJson(storageDatabase.storages, type) ?: mutableMapOf()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableMapOf()
        }
    }

    fun getUnlockedStoragesCount(player: Player): Int {
        val storageDatabase = Database.playerStorageDao.queryForId(player.uniqueId.toString())
        return storageDatabase?.unlockedStorageCount ?: 0
    }

    fun setUnlockedStorageCount(player: Player, count: Int) {
        Database.playerStorageDao.createIfNotExists(
            PlayerStorages(
                player.uniqueId.toString()
            )
        )

        val updateBuilder = Database.playerStorageDao.updateBuilder()
        updateBuilder.updateColumnValue("unlocked_storage_count", count)
        updateBuilder.where().eq("uuid", player.uniqueId.toString())
        updateBuilder.update()
    }

    fun getStorageUnlockCost(count: Int): Int? {
        return when (count) {
            1 -> 1000
            2 -> 30000
            3 -> 50000
            4 -> 100000
            5 -> 300000
            6 -> 500000
            7 -> 800000
            8 -> 1000000
            9 -> 3000000
            else -> null
        }
    }
}
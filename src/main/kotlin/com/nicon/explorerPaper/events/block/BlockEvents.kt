package com.nicon.explorerPaper.events.block

import com.nicon.explorerPaper.Main
import com.nicon.explorerPaper.utils.GameUtils
import com.nicon.explorerPaper.utils.PlayerUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.loot.LootContext
import java.util.Random

class BlockEvents : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        val blockId = block.type.key.toString()

        val blockDetail = Main.blockDetails.entries.firstOrNull { (regexStr, _) ->
            Regex(regexStr).matches(blockId)
        }?.value ?: return

        val handItemStack = player.inventory.itemInMainHand
        val handItemId = handItemStack.type.key.toString()
        if (blockDetail.appropriateTools != null && !Regex(blockDetail.appropriateTools!!).matches(handItemId)) {
            event.isCancelled = true
            player.sendMessage(
                Component
                    .text()
                    .color(NamedTextColor.RED)
                    .content("このツールでは採掘できません")
                    .build()
            )
            player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
            return
        }

        val blocksMined = PlayerUtils.PlayerDatabase.getBlocksMined(player) ?: 0
        PlayerUtils.PlayerDatabase.setBlocksMined(player, blocksMined + 1)

        if (blockDetail.lootTable != null) {
            val multiDropLevel = PlayerUtils.getEnchantLevel(handItemStack, "ex:multi_drop")

            val rollCount = if (multiDropLevel == null) 1 else Random().nextInt(multiDropLevel + 1) + 1

            repeat(rollCount) {
                val path = NamespacedKey.fromString(blockDetail.lootTable!!) ?: return
                val loottable = Bukkit.getLootTable(path) ?: return

                val lootContext = LootContext.Builder(event.block.location).build()
                val itemStacks = loottable.populateLoot(Random(), lootContext)

                for (itemStack in itemStacks) {
                    val detailedItemStack = GameUtils.setItemDetail(itemStack)
                    PlayerUtils.safeAddItem(player, detailedItemStack)
                }
            }
        }

        PlayerUtils.addExp(player, blockDetail.xp)

        when(blockId) {
            "minecraft:oak_log" -> {
                PlayerUtils.unlockRecipe(player, "stick", "棒")
                PlayerUtils.unlockRecipe(player, "planks", "建材")
            }
            "minecraft:coal_ore" -> {
                PlayerUtils.unlockRecipe(player, "coal", "石炭")
                PlayerUtils.unlockRecipe(player, "torch", "松明")
            }
            "minecraft:copper_ore" -> {
                PlayerUtils.unlockRecipe(player, "copper_ingot", "銅鉱石")
            }
            "minecraft:iron_ore" -> {
                PlayerUtils.unlockRecipe(player, "iron_ingot", "鉄鉱石")
            }
            "minecraft:redstone_ore" -> {
                PlayerUtils.unlockRecipe(player, "redstone", "レッドストーン")
            }
            "minecraft:lapis_ore" -> {
                PlayerUtils.unlockRecipe(player, "lapis_lazuli", "ラピスラズリ")
            }
            "minecraft:gold_ore" -> {
                PlayerUtils.unlockRecipe(player, "gold_ingot", "金鉱石")
            }
            "minecraft:diamond_ore" -> {
                PlayerUtils.unlockRecipe(player, "diamond", "ダイヤモンド")
            }
        }

        if (blockDetail.gold != null) {
            val goldDrop = blockDetail.gold!!
            if (Math.random() <= goldDrop.chance) {
                val random = Random()
                val goldValue = random.nextInt(goldDrop.min, goldDrop.max + 1)
                val spawnLocation = block.location.add(0.5, 0.5, 0.5)
                GameUtils.spawnGold(goldValue, spawnLocation)
                player.playSound(spawnLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                val currentGold = PlayerUtils.PlayerDatabase.getGold(player) ?: 0
                PlayerUtils.PlayerDatabase.setGold(player, currentGold + goldValue)
            }
        }
        if (blockDetail.amethyst != null) {
            val amethystDrop = blockDetail.amethyst!!
            if (Math.random() <= amethystDrop.chance) {
                val random = Random()
                val amethystValue = random.nextInt(amethystDrop.min, amethystDrop.max + 1)
                val spawnLocation = block.location.add(0.5, 0.5, 0.5)
                GameUtils.spawnAmethyst(amethystValue, spawnLocation)
                player.playSound(spawnLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                val currentAmethyst = PlayerUtils.PlayerDatabase.getAmethyst(player) ?: 0
                PlayerUtils.PlayerDatabase.setAmethyst(player, currentAmethyst + amethystValue)
            }
        }

        PlayerUtils.refreshSidebar(player)
    }
}

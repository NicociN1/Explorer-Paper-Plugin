package com.nicon.explorerPaper.events.world

import com.nicon.explorerPaper.Main
import org.bukkit.Difficulty
import org.bukkit.GameRule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.WorldLoadEvent

class WorldEvents : Listener {
    @EventHandler
    fun onWorldLoad(event: WorldLoadEvent) {
        Main.instance.logger.info("ワールドが読み込まれました！")
        val world = event.world
        world.setGameRule<Boolean>(GameRule.DO_MOB_LOOT, false)
        world.setGameRule<Boolean>(GameRule.DO_FIRE_TICK, false)
        world.setGameRule<Boolean>(GameRule.FALL_DAMAGE, false)
        world.setGameRule<Boolean>(GameRule.DISABLE_RAIDS, true)
        world.setGameRule<Boolean>(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule<Boolean>(GameRule.DO_ENTITY_DROPS, false)
        world.setGameRule<Boolean>(GameRule.DO_IMMEDIATE_RESPAWN, true)
        world.setGameRule<Boolean>(GameRule.DO_MOB_SPAWNING, false)
        world.setGameRule<Boolean>(GameRule.DO_PATROL_SPAWNING, false)
        world.setGameRule<Boolean>(GameRule.DO_TILE_DROPS, false)
        world.setGameRule<Boolean>(GameRule.TNT_EXPLODES, false)
        world.setGameRule<Boolean>(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule<Boolean>(GameRule.DROWNING_DAMAGE, false)
        world.setGameRule<Boolean>(GameRule.FIRE_DAMAGE, false)
        world.setGameRule<Boolean>(GameRule.FREEZE_DAMAGE, false)
        world.setGameRule<Boolean>(GameRule.KEEP_INVENTORY, true)

        world.time = 0

        world.difficulty = Difficulty.PEACEFUL
    }

    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {
        for (entity in event.world.entities) {
            if (entity.entitySpawnReason == SpawnReason.NATURAL) {
                entity.remove()
            }
        }
    }
}
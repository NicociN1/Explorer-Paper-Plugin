@file:Suppress("UnstableApiUsage")

package com.nicon.explorerPaper

import io.papermc.paper.datapack.DatapackRegistrar
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.bootstrap.PluginProviderContext
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.plugin.lifecycle.event.registrar.RegistrarEvent
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects


class ExplorerBootstrap : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        val manager: LifecycleEventManager<BootstrapContext> = context.lifecycleManager
        manager.registerEventHandler<RegistrarEvent<DatapackRegistrar>>(
            LifecycleEvents.DATAPACK_DISCOVERY,
            LifecycleEventHandler { event: RegistrarEvent<DatapackRegistrar> ->
                val registrar: DatapackRegistrar = event.registrar()
                val uri: URI? = Objects.requireNonNull(
                    ExplorerBootstrap::class.java.getResource("/explorer")
                ).toURI()
                if (uri != null) {
                    try {
                        registrar.discoverPack(uri, "explorer")
                    } catch (e: URISyntaxException) {
                        throw RuntimeException(e)
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                }
            })
    }

    override fun createPlugin(context: PluginProviderContext): JavaPlugin {
        return Explorer()
    }
}
package com.nicon.explorerPaper.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

object TF {
    private val mm: MiniMessage = MiniMessage.miniMessage()

    fun parse(
        raw: String,
        vararg tags: TagResolver
    ): Component =
        mm.deserialize(raw, *tags)

    fun placeholder(name: String, value: String): TagResolver =
        Placeholder.parsed(name, value)

    fun placeholderComp(name: String, value: Component): TagResolver =
        Placeholder.component(name, value)
}
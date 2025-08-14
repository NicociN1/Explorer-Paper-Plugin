package com.nicon.explorerPaper.enchants

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer

object EnchantManager {
    private const val RESOURCE_PATH = "data/enchant.yml"

    fun getDetails(): Map<String, Map<String, Int>> {
        val options = LoaderOptions()
        val constructor = Constructor(EnchantData::class.java, options)
        val dOptions = DumperOptions()
        val represent = Representer(dOptions)
        represent.getPropertyUtils().isSkipMissingProperties = true
        val y = Yaml(constructor, represent)
        val enchantData = y.loadAs<EnchantData?>(
            EnchantData::class.java.getClassLoader().getResourceAsStream(
                RESOURCE_PATH
            ), EnchantData::class.java
        )
        if (enchantData == null) {
            throw RuntimeException(RESOURCE_PATH + "が見つかりませんでした。")
        }
        return enchantData.enchants
    }
}
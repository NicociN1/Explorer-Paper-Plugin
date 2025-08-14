package com.nicon.explorerPaper.items

import com.nicon.explorerPaper.items.ItemData.ItemDetail
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.TypeDescription
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer

object ItemManager {
    private const val RESOURCE_PATH = "data/item.yml"

    fun getDetails(): Map<String, ItemDetail> {
        val options = LoaderOptions()
        val constructor = Constructor(ItemData::class.java, options)
        val desc = TypeDescription(ItemData::class.java)
        desc.addPropertyParameters("items", String::class.java, ItemDetail::class.java)
        constructor.addTypeDescription(desc)
        val dOptions = DumperOptions()
        val represent = Representer(dOptions)
        represent.getPropertyUtils().isSkipMissingProperties = true
        val y = Yaml(constructor, represent)
        val itemData = y.loadAs<ItemData?>(
            ItemData::class.java.getClassLoader().getResourceAsStream(
                RESOURCE_PATH
            ), ItemData::class.java
        )
        if (itemData == null) {
            throw RuntimeException(RESOURCE_PATH + "が見つかりませんでした。")
        }
        return itemData.items
    }
}
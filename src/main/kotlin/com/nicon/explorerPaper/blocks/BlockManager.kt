package com.nicon.items

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.TypeDescription
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer

class BlockManager {
    var blocks: MutableMap<String, BlockDetail>? = null

    class BlockDetail {
        var lootTable: String? = null
    }

    companion object {
        private const val RESOURCE_PATH = "data/block.yml"

        val details: MutableMap<String, BlockDetail>?
            get() {
                val options = LoaderOptions()
                val constructor = Constructor(BlockManager::class.java, options)
                val desc = TypeDescription(BlockManager::class.java)
                desc.addPropertyParameters("blocks", String::class.java, BlockDetail::class.java)
                constructor.addTypeDescription(desc)
                val dOptions = DumperOptions()
                val represent = Representer(dOptions)
                represent.getPropertyUtils().isSkipMissingProperties = true
                val y = Yaml(constructor, represent)
                val itemData = y.loadAs<BlockManager?>(
                    BlockManager::class.java.getClassLoader().getResourceAsStream(
                        RESOURCE_PATH
                    ), BlockManager::class.java
                )
                if (itemData == null) {
                    throw RuntimeException(RESOURCE_PATH + "が見つかりませんでした。")
                }
                return itemData.blocks
            }
    }
}
package com.nicon.explorerPaper.levelCaps

import com.nicon.explorerPaper.levelCaps.LevelCapData.LevelCapDetail
import com.nicon.explorerPaper.skills.SkillData.SkillDetail
import com.nicon.explorerPaper.utils.PlayerUtils
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer

object LevelCapManager {
    private const val RESOURCE_PATH = "data/levelcap.yml"

    fun getDetails(): MutableMap<PlayerUtils.LevelType, Array<LevelCapDetail>> {
        val options = LoaderOptions()
        val constructor = Constructor(LevelCapData::class.java, options)
        val dOptions = DumperOptions()
        val represent = Representer(dOptions)
        represent.getPropertyUtils().isSkipMissingProperties = true
        val y = Yaml(constructor, represent)
        val skillData = y.loadAs<LevelCapData?>(
            LevelCapData::class.java.getClassLoader().getResourceAsStream(
                RESOURCE_PATH
            ), LevelCapData::class.java
        )
        if (skillData == null) {
            throw RuntimeException(RESOURCE_PATH + "が見つかりませんでした。")
        }
        return skillData.levelCaps
    }
}
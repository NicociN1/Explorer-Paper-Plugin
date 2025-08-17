package com.nicon.explorerPaper.skills

import com.nicon.explorerPaper.skills.SkillData.SkillDetail
import com.nicon.explorerPaper.utils.PlayerUtils
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer

object SkillManager {
    private const val RESOURCE_PATH = "data/skill.yml"

    fun getDetails(): Map<PlayerUtils.LevelType, Array<SkillDetail>> {
        val options = LoaderOptions()
        val constructor = Constructor(SkillData::class.java, options)
        val dOptions = DumperOptions()
        val represent = Representer(dOptions)
        represent.getPropertyUtils().isSkipMissingProperties = true
        val y = Yaml(constructor, represent)
        val skillData = y.loadAs<SkillData?>(
            SkillData::class.java.getClassLoader().getResourceAsStream(
                RESOURCE_PATH
            ), SkillData::class.java
        )
        if (skillData == null) {
            throw RuntimeException(RESOURCE_PATH + "が見つかりませんでした。")
        }
        return skillData.skills
    }
}
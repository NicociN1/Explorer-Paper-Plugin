package com.nicon.explorerPaper.recipes

import com.nicon.explorerPaper.recipes.RecipeData.RecipeDetail
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer

object RecipeManager {
    private const val RESOURCE_PATH = "data/recipe.yml"

    fun getDetails(): Array<RecipeDetail> {
        val options = LoaderOptions()
        val constructor = Constructor(RecipeData::class.java, options)
        val dOptions = DumperOptions()
        val represent = Representer(dOptions)
        represent.getPropertyUtils().isSkipMissingProperties = true
        val y = Yaml(constructor, represent)
        val recipeData = y.loadAs<RecipeData?>(
            RecipeData::class.java.getClassLoader().getResourceAsStream(
                RESOURCE_PATH
            ), RecipeData::class.java
        )
        if (recipeData == null) {
            throw RuntimeException(RESOURCE_PATH + "が見つかりませんでした。")
        }
        return recipeData.recipes
    }
}
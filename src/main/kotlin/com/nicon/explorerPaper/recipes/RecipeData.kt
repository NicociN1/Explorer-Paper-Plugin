package com.nicon.explorerPaper.recipes

class RecipeData {
    var recipes: Array<RecipeDetail> = arrayOf()

    constructor()

    class RecipeDetail {
        var id: String = ""
        var requireItems: Array<InputItem> = arrayOf()
        var outputItem: OutputItem = OutputItem()
        var unlockCondition: String = ""
        var goldCost: Int = 0

        constructor()

        class InputItem {
            var id: String = ""
            var amount: Int = 1

            constructor()
        }

        class OutputItem {
            var id: String = ""
            var amount: Int = 1
            var properties: Property? = null

            constructor()

            class Property {
                var customName: String? = null

                constructor()
            }
        }
    }
}
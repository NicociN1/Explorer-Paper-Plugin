package com.nicon.explorerPaper.items

class ItemData {
    var items: MutableMap<String, ItemDetail> = mutableMapOf()

    constructor() {}

    class ItemDetail {
        var sellPrice: Int = 0

        constructor() {}
    }
}
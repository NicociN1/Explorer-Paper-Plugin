package com.nicon.explorerPaper.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "player_storages")
data class PlayerStorages(
    @DatabaseField(id = true)
    var uuid: String = "",

    @DatabaseField(columnName = "storages", defaultValue = "{}")
    var storages: String = "{}",

    @DatabaseField(columnName = "unlocked_storage_count")
    var unlockedStorageCount: Int = 0
)

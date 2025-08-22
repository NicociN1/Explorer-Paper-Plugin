package com.nicon.explorerPaper

import com.nicon.explorerPaper.model.Players
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import com.nicon.explorerPaper.model.PlayerStorages

object Database {
    private var connectionSource: JdbcConnectionSource? = null
    lateinit var playerDao: Dao<Players, String>
        private set
    lateinit var playerStorageDao: Dao<PlayerStorages, String>
        private set

    fun init(dataFolderPath: String) {
        val dbPath = "jdbc:sqlite:$dataFolderPath/data.db"
        connectionSource = JdbcConnectionSource(dbPath)

        playerDao = DaoManager.createDao(connectionSource, Players::class.java)
        TableUtils.createTableIfNotExists(connectionSource, Players::class.java)
        playerStorageDao = DaoManager.createDao(connectionSource, PlayerStorages::class.java)
        TableUtils.createTableIfNotExists(connectionSource, PlayerStorages::class.java)
    }

    fun close() {
        connectionSource?.close()
    }
}

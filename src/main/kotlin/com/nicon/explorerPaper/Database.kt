package com.nicon.explorerPaper

import Players
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils

object Database {
    private var connectionSource: JdbcConnectionSource? = null
    lateinit var playerDao: Dao<Players, String>
        private set

    fun init(dataFolderPath: String) {
        val dbPath = "jdbc:sqlite:$dataFolderPath/data.db"
        connectionSource = JdbcConnectionSource(dbPath)

        playerDao = DaoManager.createDao(connectionSource, Players::class.java)
        TableUtils.createTableIfNotExists(connectionSource, Players::class.java)
    }

    fun close() {
        connectionSource?.close()
    }
}

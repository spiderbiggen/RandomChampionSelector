package com.spiderbiggen.randomchampionselector.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.data.storage.database.converters.StringArrayConverter
import com.spiderbiggen.randomchampionselector.data.storage.database.daos.ChampionDAO

@Database(entities = [(Champion::class)], version = 4, exportSchema = false)
@TypeConverters(StringArrayConverter::class)
abstract class SimpleDatabase : RoomDatabase(), IDataInteractor {
    protected abstract fun championDAO(): ChampionDAO

    override suspend fun addChampions(champions: Collection<Champion>) =
        championDAO().insertAll(champions)

    override suspend fun findRoleList(): List<String> {
        val roleLists = championDAO().getAllRoles()
        return roleLists.flatMap { it.split(",") }.distinct()
    }

    override suspend fun findChampionList(role: String?): List<Champion> =
        championDAO().getAll(role.roleTransform())

    override suspend fun findChampion(championKey: Int): Champion =
        championDAO().getChampion(championKey)

    override suspend fun findRandomChampion(role: String?): Champion =
        championDAO().getRandom(role.roleTransform())

    private fun String?.roleTransform(): String =
        if (isNullOrEmpty() || equals("all", ignoreCase = true)) "" else this!!

}

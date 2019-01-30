package com.spiderbiggen.randomchampionselector.data.storage.database

import android.content.Context
import androidx.room.Room
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.model.IRequiresContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Stefan Breetveld on 18-3-2017.
 * Part of RandomChampionSelector.
 */
object DatabaseManager : IDataInteractor, IRequiresContext {
    private lateinit var database: SimpleDatabase

    override fun useContext(context: Context) {
        if (!this::database.isInitialized) {
            database = Room.databaseBuilder(context.applicationContext, SimpleDatabase::class.java, "random_champion_main").fallbackToDestructiveMigration().build()
        }
    }

    override suspend fun addChampion(champion: Champion) =
            simpleAsync(champion) { database.championDAO().insert(it) }

    override suspend fun addChampions(champions: Collection<Champion>) =
            simpleAsync(champions) { database.championDAO().insertAll(it) }

    override suspend fun removeChampion(champion: Champion) =
            simpleAsync(champion) { database.championDAO().delete(it) }

    override suspend fun removeChampions(champions: Collection<Champion>) =
            simpleAsync(champions) { database.championDAO().deleteAll(it) }

    override suspend fun updateChampion(champion: Champion) =
            simpleAsync(champion) { database.championDAO().update(it) }

    override suspend fun updateChampions(champions: Collection<Champion>) =
            simpleAsync(champions) { database.championDAO().updateAll(it) }

    private suspend fun <T, R> simpleAsync(o: T, l: suspend (T) -> R): R =
            withContext(Dispatchers.Default) { l(o) }

    override suspend fun findRoleList(): List<String> {
        val roleLists = database.championDAO().getAllRoles()
        return roleLists.flatMap { it.split(",") }.distinct()
    }

    override suspend fun findChampionList(role: String?): List<Champion> {
        return getChampionList(role)
    }

    override suspend fun findChampion(championKey: Int): Champion {
        return database.championDAO().getChampion(championKey)
    }

    override suspend fun findRandomChampion(championKey: Int?, role: String?): Champion {
        var champion: Champion
        do {
            champion = getRandomChampion(role)
        } while (champion.key == championKey)
        return champion
    }

    private suspend fun getChampionList(role: String?): List<Champion> = simpleAsync(role) {
        database.championDAO().getAll(role.roleTransform())
    }

    private suspend fun getRandomChampion(role: String?): Champion = simpleAsync(role) {
        database.championDAO().getRandom(role.roleTransform())
    }

    private fun String?.roleTransform(): String = if (isNullOrEmpty() || equals("all", ignoreCase = true)) "" else this!!

}

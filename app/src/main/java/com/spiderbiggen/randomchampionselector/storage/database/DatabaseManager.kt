package com.spiderbiggen.randomchampionselector.storage.database

import android.arch.persistence.room.Room
import android.content.Context
import com.spiderbiggen.randomchampionselector.model.Champion
import com.spiderbiggen.randomchampionselector.storage.database.callbacks.IDataInteractor
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * Created by Stefan Breetveld on 18-3-2017.
 * Part of RandomChampionSelector.
 */
object DatabaseManager : IDataInteractor {
    lateinit var database: SimpleDatabase

    fun useContext(context: Context) {
        database = Room.databaseBuilder(context.applicationContext, SimpleDatabase::class.java, "random_champion_main").fallbackToDestructiveMigration().build()
    }

    fun addChampions(champions: Collection<Champion>): Disposable {
        return Observable.just(champions)
                .subscribeOn(Schedulers.io())
                .subscribe { database.championDAO().insert(it) }
    }

    override fun findRoleList(listener: Consumer<List<String>>): Disposable =
            database.championDAO().allRoles
                    .subscribeOn(Schedulers.io())
                    .flatMap { Flowable.fromIterable(it).map { it.split(",") } }
                    .distinct()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listener)

    override fun findChampionList(listener: Consumer<List<Champion>>, role: String?): Disposable =
            getChampionListFlowable(role)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listener)

    override fun findChampion(listener: Consumer<Champion>, championKey: Int): Disposable {
        return database.championDAO().getChampion(championKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener)
    }

    override fun findRandomChampion(listener: Consumer<Champion>, championKey: Int, role: String?): Disposable {
        return getRandomChampionFlowable(role)
                .subscribeOn(Schedulers.io())!!
                .repeat()
                .takeUntil { it.key != championKey }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener)
    }

    private fun getChampionListFlowable(role: String?): Flowable<List<Champion>> {
        return when {
            isWildcardRole(role) -> database.championDAO().all
            else -> database.championDAO().getAll(role)
        }
    }

    private fun getRandomChampionFlowable(role: String?): Flowable<Champion> {
        return when {
            isWildcardRole(role) -> database.championDAO().random
            else -> database.championDAO().getRandom(role)
        }
    }

    private fun isWildcardRole(role: String?): Boolean = role.isNullOrEmpty() || role.equals("all", ignoreCase = true)
}

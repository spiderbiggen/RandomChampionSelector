package com.spiderbiggen.randomchampionselector.data.storage.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Class that defines the champion object.
 *
 * @author Stefan Breetveld
 */
@Entity(tableName = "champion")
data class ChampionEntity(
    @PrimaryKey val key: Int,
    val id: String,
    val name: String,
    val title: String,
    val lore: String,
    val blurb: String,
    val roles: List<String>,
    @Embedded val info: InfoEntity
) : Serializable

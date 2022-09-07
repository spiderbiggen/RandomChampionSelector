package com.spiderbiggen.randomchampionselector.domain.storage

import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import java.io.File

interface FileRepository {
    /**
     * Delete all champion related images.
     * @see deleteRecursively
     */
    fun deleteChampionImages(): Boolean

    /**
     * Get the location of the bitmap for this champion
     */
    fun getBitmapFile(c: Champion): File
}
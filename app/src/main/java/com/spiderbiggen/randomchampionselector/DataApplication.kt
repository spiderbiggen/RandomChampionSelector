package com.spiderbiggen.randomchampionselector

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Custom implementation of [Application] to make sure the different managers and the database are initialized
 *
 * @author Stefan Breetveld
 */
@HiltAndroidApp
class DataApplication : Application()
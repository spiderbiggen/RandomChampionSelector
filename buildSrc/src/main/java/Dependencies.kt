object Versions {
    const val agp = "7.0.4"
    const val room = "2.4.0"
    const val kotlin = "1.6.10"
    const val lifecycle = "2.4.0"
    const val coroutines = "1.5.1"
    const val work = "2.5.0"
    const val daggerHilt = "2.38.1"
    const val androidHilt = "1.0.0"
    const val moshi = "1.13.0"
}

object BuildPlugins {
    const val android = "com.android.tools.build:gradle:${Versions.agp}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinExtensions = "org.jetbrains.kotlin:kotlin-android-extensions:${Versions.kotlin}"
    const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.daggerHilt}"
}

object Deps {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"

    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.2"
    const val appCompat = "androidx.appcompat:appcompat:1.4.0"
    const val activityKtx = "androidx.activity:activity-ktx:1.4.0"
    const val annotation = "androidx.annotation:annotation:1.3.0"
    const val cardview = "androidx.cardview:cardview:1.0.0"
    const val vectordrawable = "androidx.vectordrawable:vectordrawable:1.1.0"
    const val recyclerView = "androidx.recyclerview:recyclerview:1.2.1"
    const val preferenceKtx = "androidx.preference:preference-ktx:1.1.1"
    const val material = "com.google.android.material:material:1.4.0"
    const val coreKtx = "androidx.core:core-ktx:1.7.0"

    const val lifecycleViewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val lifecycleLivedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    const val lifecycleCommon = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    const val daggerHilt = "com.google.dagger:hilt-android:${Versions.daggerHilt}"
    const val daggerHiltCompiler = "com.google.dagger:hilt-compiler:${Versions.daggerHilt}"

    const val daggerHiltJetpackCompiler = "androidx.hilt:hilt-compiler:${Versions.androidHilt}"
    const val daggerHiltWork = "androidx.hilt:hilt-work:${Versions.androidHilt}"

    const val okhttp3LoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:3.9.1"
    const val retrofit2 = "com.squareup.retrofit2:retrofit:2.6.2"

    const val moshi = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"

    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
}
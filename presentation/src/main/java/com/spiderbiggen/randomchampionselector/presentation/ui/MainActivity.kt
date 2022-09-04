package com.spiderbiggen.randomchampionselector.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.spiderbiggen.randomchampionselector.presentation.R
import com.spiderbiggen.randomchampionselector.presentation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {

            setContentView(root)
            val toolbar = toolbar
            setSupportActionBar(toolbar) // uncomment this line

            val navHostFragment =  supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.championsOverviewFragment, R.id.championDetailsFragment -> {
                        supportActionBar?.hide() // to hide
                    }
                    else -> {
                        supportActionBar?.show() // to show
                    }
                }
            }
        }
    }


}
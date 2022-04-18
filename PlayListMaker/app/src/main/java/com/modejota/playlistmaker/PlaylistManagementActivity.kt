package com.modejota.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.modejota.playlistmaker.databinding.ActivityPlaylistManagementBinding

class PlaylistManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistManagementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.allSongs_fragment,
            R.id.selectedSongs_fragment
        ))
        setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navHostFragment.navController)
    }

}
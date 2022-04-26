package com.modejota.playlistmaker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.modejota.playlistmaker.R
import com.modejota.playlistmaker.databinding.ActivityPlaylistManagementBinding

/**
 * Class to represent the Playlist Management, where fragments with those functionality will be held.
 */
class PlaylistManagementActivity : AppCompatActivity() {

    /**
     * ViewBinding object to be used in the activity.
     */
    private lateinit var binding: ActivityPlaylistManagementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the bottom navigation bar (the fragment-container, the controller)
        // and the fragment in each element of the container.
        val bottomNavigationView = binding.bottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment

        // Set up the action bar, so text shown change for diferent fragments.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.allSongs_fragment,
            R.id.selectedSongs_fragment
        ))
        setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navHostFragment.navController)
    }

}
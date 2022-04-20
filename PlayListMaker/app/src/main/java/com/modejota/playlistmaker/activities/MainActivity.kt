package com.modejota.playlistmaker.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.modejota.playlistmaker.adapters.PlaylistAdapter
import com.modejota.playlistmaker.R
import com.modejota.playlistmaker.helpers.SharedData
import com.modejota.playlistmaker.helpers.Utilities
import com.modejota.playlistmaker.databinding.ActivityMainBinding

/**
 * Class MainActivity, entry point for the app.
 */
class MainActivity : AppCompatActivity(), PlaylistAdapter.OnItemClickListener {

    /**
     * ViewBinding object for this activity
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Reference to the upper ActionMenu in the Toolbar
     */
    private var actionMenu: Menu? = null

    /**
     * List of paths selected when the user long-clicks on a playlist
     */
    private val selectedPaths = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()

        askForPermission()  // Ask for permission to write/read external storage (API dependent)

        binding.createPlaylistFloatingButton.setOnClickListener {
            val intent = Intent(this, PlaylistManagementActivity::class.java)
            SharedData.setAllSongs(Utilities.getMusicFromInternalStorage(this))
            startActivity(intent)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            initRecyclerView()
            binding.swipeRefreshLayout.isRefreshing = false
        }
        SharedData.clearAll()   // Clear all data when the activity is created
    }

    override fun onStart() {
        super.onStart()
        SharedData.clearAll()   // At the start of the activity, clear all data
        initRecyclerView()      // and re-initialize to keep consistency
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // If the user granted the permission, then we can re-initialize the view and get the data
        if (grantResults.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initRecyclerView()
                }
            } else {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initRecyclerView()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        actionMenu = menu   // Save the reference to the menu
        menuInflater.inflate(R.menu.action_bar_delete_playlist, menu)
        showActionMenu(false)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Method to show or hide the ActionMenu
     *
     * @param show Boolean to show or hide the ActionMenu
     */
    private fun showActionMenu(show: Boolean) {
        actionMenu?.let {
            it.findItem(R.id.selectedPlaylistDelete).isVisible = show
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.selectedPlaylistDelete -> {
                confirmDelete()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Method to confirm the deletion of the selected playlists' files in the internal storage
     */
    private fun confirmDelete() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(getString(R.string.delete_playlist_title))
        alertDialog.setMessage(getString(R.string.delete_playlist_message))
        alertDialog.setPositiveButton(getString(R.string.affirmative)) { _, _ ->
            selectedPaths.forEach {
                Utilities.deletePlaylistFileFromStorage(it)
            }
            initRecyclerView()  // Re-initialize the view to keep consistency
            Toast.makeText(this, getString(R.string.confirm_playlist_deleted), Toast.LENGTH_SHORT).show()
        }
        alertDialog.setNegativeButton(getString(R.string.negative)) { _, _ -> }
        alertDialog.show()
    }

    /**
     * Method to initialize the RecyclerView.
     * It will get the playlists' data from the internal storage and load it into the RecyclerView
     */
    private fun initRecyclerView() {
        binding.rvPlaylistList.layoutManager = LinearLayoutManager(this)
        val playlistsList = Utilities.getPlaylistsData()
        val isClickedList = mutableListOf<Boolean>()
        isClickedList.addAll(Array(playlistsList.size) { false })
        binding.rvPlaylistList.adapter = PlaylistAdapter(playlistsList, isClickedList, this)
        selectedPaths.clear()   // At start, no path is selected, so no ActionMenu is shown
        showActionMenu(false)
    }

    /**
     * Function overriding from own interface. Called when the user clicks on a playlist.
     * Launchs and activity to modify the playlist's songs.
     *
     * @param position Position in the RecyclerView of the clicked item
     */
    override fun onItemClick(position: Int) {
        val adapter = binding.rvPlaylistList.adapter as PlaylistAdapter
        SharedData.setPlaylistPath(adapter.getItem(position).path)
        SharedData.setAllSongs(Utilities.getMusicFromInternalStorage(this))
        val intent = Intent(this, PlaylistManagementActivity::class.java)
        startActivity(intent)
    }

    /**
     * Function overriding from own interface. Called when the user long-clicks on a playlist.
     *
     * @param position Position in the RecyclerView of the long-clicked item
     */
    override fun onItemLongClick(position: Int) {
        val adapter = binding.rvPlaylistList.adapter as PlaylistAdapter
        val item = adapter.getItem(position).path
        if (selectedPaths.contains(item)) {
            selectedPaths.remove(item)
        } else {
            selectedPaths.add(item)
        }
        adapter.changeVisibility(position)  // Change the visibility of the item's tick
        // While at least one item is selected, avoid entering in a playlist
        adapter.setClickable(selectedPaths.isEmpty())
        showActionMenu(selectedPaths.isNotEmpty())  // Show the ActionMenu if at least one item is selected

    }

    /**
     * Function to ask for the necessary permissions.
     * Different permissions are asked depending on the Android version. (Q)
     */
    private fun askForPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    21
                )
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 21
                )
            }
        }
    }
}
package com.modejota.playlistmaker

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
import com.modejota.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), PlaylistAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private var actionMenu: Menu? = null
    private val selectedPaths = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()

        askForPermission()

        binding.createPlaylistFloatingButton.setOnClickListener {
            val intent = Intent(this, PlaylistManagementActivity::class.java)
            SharedData.setAllSongs(Utilities.getMusicFromInternalStorage(this))
            startActivity(intent)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            initRecyclerView()
            binding.swipeRefreshLayout.isRefreshing = false
        }
        SharedData.clearAll()

    }

    override fun onStart() {
        super.onStart()
        SharedData.clearAll()
        initRecyclerView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        actionMenu = menu
        menuInflater.inflate(R.menu.action_bar_delete_playlist, menu)
        showActionMenu(false)
        return super.onCreateOptionsMenu(menu)
    }

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

    private fun confirmDelete() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(getString(R.string.delete_playlist_title))
        alertDialog.setMessage(getString(R.string.delete_playlist_message))
        alertDialog.setPositiveButton(getString(R.string.affirmative)) { _, _ ->
            selectedPaths.forEach {
                Utilities.deletePlaylistFileFromStorage(it)
            }
            initRecyclerView()
            Toast.makeText(this, getString(R.string.confirm_playlist_deleted), Toast.LENGTH_SHORT).show()
        }
        alertDialog.setNegativeButton(getString(R.string.negative)) { _, _ -> }
        alertDialog.show()
    }

    private fun initRecyclerView() {
        binding.rvPlaylistList.layoutManager = LinearLayoutManager(this)
        val playlistsList = Utilities.getPlaylistsData()
        val isClickedList = mutableListOf<Boolean>()
        isClickedList.addAll(Array(playlistsList.size) { false })
        binding.rvPlaylistList.adapter = PlaylistAdapter(playlistsList, isClickedList, this)
        selectedPaths.clear()
        showActionMenu(false)
    }

    override fun onItemClick(position: Int) {
        val adapter = binding.rvPlaylistList.adapter as PlaylistAdapter
        SharedData.setPlaylistPath(adapter.getItem(position).path)
        SharedData.setAllSongs(Utilities.getMusicFromInternalStorage(this))
        val intent = Intent(this, PlaylistManagementActivity::class.java)
        startActivity(intent)
    }

    override fun onItemLongClick(position: Int) {
        val adapter = binding.rvPlaylistList.adapter as PlaylistAdapter
        val item = adapter.getItem(position).path
        if (selectedPaths.contains(item)) {
            selectedPaths.remove(item)
        } else {
            selectedPaths.add(item)
        }
        adapter.changeVisibility(position)
        showActionMenu(selectedPaths.isNotEmpty())

    }

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
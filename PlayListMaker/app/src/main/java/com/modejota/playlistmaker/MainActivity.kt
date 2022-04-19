package com.modejota.playlistmaker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.modejota.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

    private fun initRecyclerView() {
        binding.rvPlaylistList.layoutManager = LinearLayoutManager(this)
        val playlistsList = Utilities.getPlaylistsData()
        binding.rvPlaylistList.adapter = PlaylistAdapter(playlistsList)
        (binding.rvPlaylistList.adapter as PlaylistAdapter).onItemClick={
            val adapter = binding.rvPlaylistList.adapter as PlaylistAdapter
            val item = playlistsList.indexOfFirst { playlist -> playlist == it }
            SharedData.setPlaylistPath(adapter.getItem(item).path)
            SharedData.setAllSongs(Utilities.getMusicFromInternalStorage(this))
            val intent = Intent(this, PlaylistManagementActivity::class.java)
            startActivity(intent)
        }
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
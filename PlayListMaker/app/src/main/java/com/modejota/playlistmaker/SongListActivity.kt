package com.modejota.playlistmaker

import android.content.ContentUris
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.modejota.playlistmaker.databinding.ActivitySongListBinding
import java.util.concurrent.TimeUnit


class SongListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvSongList.layoutManager = LinearLayoutManager(this)
        val songsList = getMusicFromInternalStorage()
        Toast.makeText(this, "Found ${songsList.size} songs", Toast.LENGTH_SHORT).show()
        binding.rvSongList.adapter = SongAdapter(songsList)
    }

    private fun getMusicFromInternalStorage(): MutableList<Song> {
        val musicList = mutableListOf<Song>()
        val collection =
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
        )
        val selection = "${MediaStore.Audio.Media.DURATION} >= ?"
        val selectionArgs = arrayOf(
            TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS).toString()
        )
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
        val query = contentResolver.query(collection,projection,selection,selectionArgs,sortOrder)
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                val duration = cursor.getLong(durationColumn)
                musicList += Song(id, contentUri, name, artist, album, duration)
            }
        }
        return musicList
    }
}
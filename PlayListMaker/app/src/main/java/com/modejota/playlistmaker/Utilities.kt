package com.modejota.playlistmaker

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.concurrent.TimeUnit
import kotlin.math.floor

object Utilities {

    fun getMusicFromInternalStorage(context: Context): MutableList<Song> {
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
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
        )
        val selection = "${MediaStore.Audio.Media.DURATION} >= ?"
        val selectionArgs = arrayOf(
            TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS).toString()
        )
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
        val contentResolver = context.contentResolver
        val query = contentResolver.query(collection,projection,selection,selectionArgs,sortOrder)
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                var artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val contentUri: String = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).path!!
                val duration = cursor.getLong(durationColumn)
                if (artist == "<unknown>") {
                    artist = context.resources.getString(R.string.unknown_artist)
                }
                musicList += Song(id, contentUri, name, artist, album, albumId, duration)
            }
        }
        musicList.sortBy { it.title }
        return musicList
    }

    fun getPlaylistsData(): List<Playlist> {
        val musicFolder = File(Environment.getExternalStorageDirectory().absolutePath + "/Music")
        val files = mutableListOf<Playlist>()
        musicFolder.walk().forEach {
            if (it.isFile and it.path.endsWith(".m3u8")) {
                files.add(Playlist(it.path,it.nameWithoutExtension, countSongs(it)))
            }
        }
        return files
    }

    fun createFilePublic(fileName: String, songList: List<Song>) {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "$fileName.m3u8")
        if (!file.exists()) {
            file.createNewFile()
        } else {
            file.delete()
            file.createNewFile()
        }
        file.writeText("#EXTM3U\n")
        for (song in songList) {
            val segs = floor((song.duration / 1000).toDouble()).toInt()
            file.appendText("#EXTINF:${segs},${song.title}\n")
            file.appendText("${song.path}\n")
        }
    }

    fun parseM3U8(file: File): MutableList<Song> {
        val allSongs = SharedData.getAllSongs()
        val songList = mutableListOf<Song>()
        val reader = BufferedReader(FileReader(file))
        var line = reader.readLine()
        while (line != null) {
            if (line.startsWith("#")) {
                line = reader.readLine()
                continue
            }
            val path = Uri.parse(line).path
            val song = allSongs.find { it.path == path }
            if (song != null) {
                songList.add(song)
            }
            line = reader.readLine()
        }
        return songList
    }

    fun getSongsByIDs(): MutableList<Song> {
        val allSongs = SharedData.getAllSongs()
        val songList = mutableListOf<Song>()
        for (songID in SharedData.getSongsID()) {
            val song = allSongs.find { song -> song.ID == songID }
            if (song != null) {
                songList += song
            }
        }
        return songList
    }

    private fun countSongs(file: File): Int {
        var count = 0
        file.forEachLine { if (it.startsWith("#EXTINF")) { count++ } }
        return count
    }
}
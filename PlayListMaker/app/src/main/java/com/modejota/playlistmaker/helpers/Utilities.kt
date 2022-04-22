package com.modejota.playlistmaker.helpers

import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.modejota.playlistmaker.R
import com.modejota.playlistmaker.models.Playlist
import com.modejota.playlistmaker.models.Song
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.concurrent.TimeUnit
import kotlin.math.floor

/**
 * Singleton class for utility functions used all over the app
 */
object Utilities {

    /**
     * Function to retrieve all songs (longer than 30 seconds) from the device's storage
     * sorted by title ascending.
     *
     * @param context Context of the activity/fragment
     * @return List of songs in the device sorted by title ascending
     */
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
            MediaStore.Audio.Media.DATA,
        )
        val selection = "${MediaStore.Audio.Media.DURATION} >= ?"
        val selectionArgs = arrayOf(
            TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS).toString()
        )
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
        val contentResolver = context.contentResolver
        // Setup a SQL-like query to get selected data fron all songs longer than 30 seconds
        val query = contentResolver.query(collection,projection,selection,selectionArgs,sortOrder)
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            // For each result, retrieve the data, create a new Song object and add it to the list
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                var artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val path = cursor.getString(data)
                val duration = cursor.getLong(durationColumn)
                if (artist == "<unknown>") {    // Little prettier output for unknown artist
                    artist = context.resources.getString(R.string.unknown_artist)
                }
                musicList += Song(id, path, name, artist, album, albumId, duration)
            }
        }
        musicList.sortBy { it.title }
        return musicList
    }

    /**
     * Function to retrive playlists' files (.m3u) from the device's storage
     *
     * @return List of playlists (represented as project's object) in the device
     */
    fun getPlaylistsData(): MutableList<Playlist> {
        val musicFolder = File(Environment.getExternalStorageDirectory().absolutePath + "/Music")
        val files = mutableListOf<Playlist>()
        musicFolder.walk().forEach {
            if (it.isFile and it.path.endsWith(".m3u")) {
                files.add(Playlist(it.path,it.nameWithoutExtension, countSongs(it)))
            }
        }
        return files
    }

    /**
     * Function to create a playlist file (.m3u) in the device's storage, under Music folder
     *
     * @param fileName Name of the playlist file (without .m3u extension)
     * @param songList List of songs to add to the playlist
     */

    fun createFilePublic(fileName: String, songList: List<Song>) {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "$fileName.m3u")
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

    /**
     * Function to retrieve the songs of a playlist file (.m3u)
     * It assumess the file follows the standard, so #EXTM3U is the first line, and for each song,
     * there is a line with #EXTINF:<duration>,<song name> and other line with the path.
     * Non standard attributes, such as #EXTALB or #EXTART, are not supposed to appear.
     *
     * @param file File of the playlist (.m3u)
     * @return List of songs of the playlist
     */

    fun parseM3U(file: File): MutableList<Song> {
        val allSongs = SharedData.getAllSongs()
        val songList = mutableListOf<Song>()
        val reader = BufferedReader(FileReader(file))
        var line = reader.readLine()
        while (line != null) {
            if (line.startsWith("#")) {
                line = reader.readLine()
                continue
            }
            val path = line
            val song = allSongs.find { it.path == path }
            if (song != null) {
                songList.add(song)
            }
            line = reader.readLine()
        }
        return songList
    }

    /**
     * Function to retrieve the songs with specific IDs from the shared data in the app
     *
     * @return Songs with the specific IDs
     */
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

    /**
     * Function to delete a file from the device's storage
     *
     * @param path Path of the file to delete
     */
    fun deletePlaylistFileFromStorage(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * Function to count the number of songs in a playlist file (.m3u)
     *
     * @param file File of the playlist (.m3u)
     * @return Number of songs in the playlist
     */
    private fun countSongs(file: File): Int {
        var count = 0
        file.forEachLine {
            if (it.startsWith("#EXTINF")) {
                count++
            }
        }
        return count
    }
}
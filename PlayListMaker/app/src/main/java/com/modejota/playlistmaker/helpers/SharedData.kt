package com.modejota.playlistmaker.helpers

import com.modejota.playlistmaker.models.Song

/**
 * Singleton class to share data between fragments in the playlist management
 * activity and receive data from the MainActivity.
 */
object SharedData {

    /**
     * Path to the playlist file (if user clicked on a playlist to edit it, empty otherwise).
     */
    private var playlistPath: String = ""

    /**
     * List of IDs of the songs confirmed by the user to be shown in selected fragment.
     * If user clicked on a playlist to edit it, also contains the IDs of the songs already in the playlist.
     */
    private var songsID = mutableListOf<Long>()

    /**
     * List of songs retrieved from the device internal storage.
     */
    private var allSongs = mutableListOf<Song>()

    /**
     * Function to set the path of the playlist being edited
     *
     * @param path Path to the playlist file
     */
    fun setPlaylistPath(path: String) {
        playlistPath = path
    }

    /**
     * Function to get the path of the playlist being edited
     *
     * @return Path to the playlist file
     */
    fun getPlaylistPath() = playlistPath

    /**
     * Function to add an array of selected songs' IDs to the inner list of selected songs' IDs
     *
     * @param ids Array of selected songs' IDs
     */
    fun addSongsID(ids: List<Long>) = songsID.addAll(ids)

    /**
     * Function to return the list of selected songs' IDs
     *
     * @return List of selected songs' IDs
     */
    fun getSongsID(): MutableList<Long> = songsID

    /**
     * Function to retrieve the list of songs from the device internal storage
     *
     * @return List of songs from the device internal storage
     */
    fun getAllSongs(): MutableList<Song> = allSongs

    /**
     * Function to set the songs retrieved from the device internal storage
     *
     * @param songs List of songs from the device internal storage
     */
    fun setAllSongs(songs: MutableList<Song>) {
        allSongs = songs
    }

    /**
     * Function to clear all data stored in the singleton class
     */
    fun clearAll() {
        playlistPath = ""
        songsID.clear()
        allSongs.clear()
    }

}
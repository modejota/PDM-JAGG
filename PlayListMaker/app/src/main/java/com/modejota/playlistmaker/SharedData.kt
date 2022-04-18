package com.modejota.playlistmaker

object SharedData {
    private var playlistPath: String = ""
    private var songsID = mutableListOf<Long>()     // IDs de las canciones seleccionadas (confirmadas por el usuario)
    private var allSongs = mutableListOf<Song>()    // Todas las canciones leídas de la base de datos

    fun setPlaylistPath(path: String) { playlistPath = path }
    fun getPlaylistPath() = playlistPath

    fun addSongsID(ids: List<Long>) = songsID.addAll(ids)
    fun getSongsID(): MutableList<Long> = songsID

    fun getAllSongs(): MutableList<Song> = allSongs
    fun setAllSongs(songs: MutableList<Song>) { allSongs = songs }

    fun clearAll() {
        playlistPath = ""
        songsID.clear()
        allSongs.clear()
    }
}
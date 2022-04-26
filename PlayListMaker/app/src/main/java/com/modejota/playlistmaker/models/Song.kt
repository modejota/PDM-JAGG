package com.modejota.playlistmaker.models

/**
 * Data class for song objects.
 *
 * @property ID Unique ID for the song.
 * @property path Path to the song.
 * @property title Title of the song.
 * @property author Author of the song.
 * @property album Album of the song.
 * @property albumID ID of the album.
 * @property duration Duration of the song (milliseconds).
 *
 * @author José Alberto Gómez García    -   @modejota
 */
data class Song(val ID: Long,
                val path: String,
                val title: String,
                val author: String,
                val album: String,
                val albumID: Long,
                val duration: Long)
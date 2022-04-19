package com.modejota.playlistmaker

data class Song(val ID: Long,
                val path: String,
                val title: String,
                val author: String,
                val album: String,
                val albumID: Long,
                val duration: Long)
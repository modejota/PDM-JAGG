package com.modejota.playlistmaker

import android.net.Uri

data class Song(val ID: Long,
                val uri: Uri,
                val title: String,
                val author: String,
                val album: String,
                val albumID: Long,
                val duration: Long)
package com.modejota.playlistmaker

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.modejota.playlistmaker.databinding.ItemSongBinding

class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemSongBinding.bind(view)

    fun render(song: Song) {
        binding.songTitle.text = song.title
        binding.songAuthor.text = song.author
        binding.songDuration.text = millisToString(song.duration)
    }

    private fun millisToString(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val remainingMinutes = minutes % 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", remainingMinutes, remainingSeconds)
    }
}
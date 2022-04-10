package com.modejota.playlistmaker

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(private val songList: MutableList<Song>) : RecyclerView.Adapter<SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SongViewHolder(layoutInflater.inflate(R.layout.item_song, parent, false))
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.render(songList[position])
    }

    override fun getItemCount() : Int = songList.size
}
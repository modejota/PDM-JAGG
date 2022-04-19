package com.modejota.playlistmaker

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.modejota.playlistmaker.databinding.ItemPlaylistBinding

class PlaylistAdapter(private val playlistList: List<Playlist>) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    var onItemClick: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(layoutInflater.inflate(R.layout.item_playlist, parent, false))
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.render(playlistList[position])
    }

    override fun getItemCount(): Int = playlistList.size

    fun getItem(position: Int): Playlist = playlistList[position]

    inner class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val binding = ItemPlaylistBinding.bind(view)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(playlistList[adapterPosition])
            }
        }

        fun render(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.playlistSize.text =
                itemView.context.resources.getQuantityString(R.plurals.playlist_size, playlist.size, playlist.size)
        }

    }

}
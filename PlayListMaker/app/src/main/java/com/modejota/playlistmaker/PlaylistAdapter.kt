package com.modejota.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.modejota.playlistmaker.databinding.ItemPlaylistBinding

class PlaylistAdapter(
    private val playlistList: List<Playlist>,
    private val isClickedList: MutableList<Boolean>,
    private val listener: OnItemClickListener,
    ) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(layoutInflater.inflate(R.layout.item_playlist, parent, false))
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.render(playlistList[position])
        holder.changeVisibility(isClickedList[position])
    }

    override fun getItemCount(): Int = playlistList.size

    fun changeVisibility(position: Int) {
        isClickedList[position] = !isClickedList[position]
        notifyItemChanged(position)
    }

    fun getItem(position: Int): Playlist = playlistList[position]

    inner class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, View.OnLongClickListener {

        private val binding = ItemPlaylistBinding.bind(view)

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

        override fun onLongClick(p0: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(position)
            }
            return true
        }

        fun render(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.playlistSize.text =
                itemView.context.resources.getQuantityString(R.plurals.playlist_size, playlist.size, playlist.size)
        }

        fun changeVisibility(selected: Boolean) {
            if (selected) {
                binding.selectedTickPlaylist.visibility = View.VISIBLE
            } else {
                binding.selectedTickPlaylist.visibility = View.INVISIBLE
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

}
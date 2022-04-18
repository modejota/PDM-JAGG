package com.modejota.playlistmaker

import android.content.ContentUris
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.modejota.playlistmaker.databinding.ItemSongBinding
import com.squareup.picasso.Picasso
import java.util.*


class SongAdapter(
    private val songList: MutableList<Song>,
    private val isClickedList: MutableList<Boolean>,
    private val listener: OnItemClickListener,
    ) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    object Constants {
        val albumUri: Uri = Uri.parse("content://media/external/audio/albumart")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SongViewHolder(layoutInflater.inflate(R.layout.item_song, parent, false))
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.render(songList[position])
        holder.changeVisibility(isClickedList[position])
    }

    override fun getItemCount() : Int = songList.size

    fun deleteSong(position: Int) {
        songList.removeAt(position)
        isClickedList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getSongList() : List<Song> = songList

    fun moveSong(from: Int, to: Int) {
        Collections.swap(songList, from, to)
        Collections.swap(isClickedList, from, to)
        notifyItemMoved(from, to)
    }

    fun changeVisibility(position: Int) {
        isClickedList[position] = !isClickedList[position]
        notifyItemChanged(position)
    }

    inner class SongViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val binding = ItemSongBinding.bind(view)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

        fun render(song: Song) {
            binding.songTitle.text = song.title
            binding.songAuthor.text = song.author

            val uri: Uri = ContentUris.withAppendedId(Constants.albumUri, song.albumID)
            Picasso.get().load(uri)
                .fit()
                .centerCrop()
                .error(R.drawable.ic_baseline_album)
                .into(binding.albumCover)
        }

        fun changeVisibility(selected: Boolean) {
            if (selected) {
                binding.selectedTick.visibility = View.VISIBLE
            } else {
                binding.selectedTick.visibility = View.INVISIBLE
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}
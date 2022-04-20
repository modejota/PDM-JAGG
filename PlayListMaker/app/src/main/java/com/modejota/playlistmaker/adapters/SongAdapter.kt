package com.modejota.playlistmaker.adapters

import android.content.ContentUris
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.modejota.playlistmaker.R
import com.modejota.playlistmaker.databinding.ItemSongBinding
import com.modejota.playlistmaker.models.Song
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Adapter class for the songs RecyclerViews
 *
 * @property songList    List of songs
 * @property isClickedList   List of booleans to keep track of which songs are clicke
 * @property listener   Listener for click event
 */
class SongAdapter(
    private val songList: MutableList<Song>,
    private val isClickedList: MutableList<Boolean>,
    private val listener: OnItemClickListener,
    ) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    /**
     * Auxiliary constants wrapper
     */
    object Constants {
        /**
         * Constant for the prefix of the URI where an album cover is stored.
         */
        val albumUri: Uri = Uri.parse("content://media/external/audio/albumart")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SongViewHolder(layoutInflater.inflate(R.layout.item_song, parent, false))
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.render(songList[position])
        holder.changeVisibility(isClickedList[position]) // Set visibility of the tick when rendering the item
    }

    override fun getItemCount() : Int = songList.size

    /**
     * Function to delete a song from the adapter.
     * It also deletes its entry from isClickedList (if removed, no need to keep track, cant be clicked)
     *
     * @param position Position of the song to be removed
     */
    fun deleteSong(position: Int) {
        songList.removeAt(position)
        isClickedList.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Function to get the list of songs in the adapter
     *
     * @return List of songs
     */
    fun getSongList() : List<Song> = songList

    /**
     * Function to swap the position of two songs in the adapter
     *
     * @param from Position of the song to be moved
     * @param to Position where the song will be moved
     */
    fun moveSong(from: Int, to: Int) {
        Collections.swap(songList, from, to)
        Collections.swap(isClickedList, from, to)
        notifyItemMoved(from, to)
    }

    /**
     * Function to change the visibility of the tick for a certain song
     *
     * @param position Position of the song in the list
     */
    fun changeVisibility(position: Int) {
        isClickedList[position] = !isClickedList[position]
        notifyItemChanged(position)
    }

    /**
     * ViewHolder class for the songs RecyclerViews
     * It requires click listener.
     *
     * @constructor
     * Initialize the click listener
     *
     * @param view View of the item
     */
    inner class SongViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        /**
         * ViewBinding object for the ViewHolder
         */
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

        /**
         * Function to render the song item
         *
         * @param song Song to be rendered
         */
        fun render(song: Song) {
            binding.songTitle.text = song.title
            binding.songAuthor.text = song.author

            // Given the albumID, get the album cover
            val uri: Uri = ContentUris.withAppendedId(Constants.albumUri, song.albumID)
            Picasso.get().load(uri)
                .fit()
                .centerCrop()
                .error(R.drawable.ic_baseline_album) // If the album cover is not found, use the default one
                .into(binding.albumCover)
        }

        /**
         * Function to change the visibility of the tick
         *
         * @param selected Boolean to determine if the tick should be visible or not
         */
        fun changeVisibility(selected: Boolean) {
            if (selected) {
                binding.selectedTick.visibility = View.VISIBLE
            } else {
                binding.selectedTick.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * Interface to handle the click event of the RecyclerView.
     * As interface, it will force implementation of the function where needed.
     */
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}
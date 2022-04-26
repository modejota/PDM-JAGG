package com.modejota.playlistmaker.adapters

import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.modejota.playlistmaker.R
import com.modejota.playlistmaker.databinding.ItemPlaylistBinding
import com.modejota.playlistmaker.models.Playlist

/**
 * Addapter class for playlist RecyclerView
 *
 * @property playlistList   List of playlists data
 * @property isClickedList  List of booleans to keep track of clicked playlists
 * @property listener       Listener for click and long-click events
 *
 * @author José Alberto Gómez García    -   @modejota
 */
class PlaylistAdapter(
    private val playlistList: MutableList<Playlist>,
    private val isClickedList: MutableList<Boolean>,
    private val listener: OnItemClickListener,
    ) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    /**
     * Variable to indicate if the playlist can be clicked
     * @property isClickable
     */
    private var isClickable = true

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(layoutInflater.inflate(R.layout.item_playlist, parent, false))
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.render(playlistList[position])
        holder.changeVisibility(isClickedList[position])    // Set visibility of the tick when rendering the item
    }

    override fun getItemCount(): Int = playlistList.size

    /**
     * Setter of the clickable variable
     *
     * @param click Boolean value to set the clickable variable
     */
    fun setClickable(click: Boolean) {
        isClickable = click
    }

    /**
     * Function to change the visibility of the tick for a certain playlist
     *
     * @param position Position of the playlist in the list
     */
    fun changeVisibility(position: Int) {
        isClickedList[position] = !isClickedList[position]
        notifyItemChanged(position)
    }

    /**
     * Function to return a concret playlist from the list
     *
     * @param position  Position of the playlist in the list
     * @return Playlist object
     */
    fun getItem(position: Int): Playlist = playlistList[position]

    /**
     * ViewHolder class for the playlist RecyclerView
     * It requieres click and long-click listeners
     *
     * @constructor
     * Initialize the click and long-click listeners
     *
     * @param view View of the item
     */
    inner class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, View.OnLongClickListener {

        /**
         * ViewBinding object for the ViewHolder
         */
        private val binding = ItemPlaylistBinding.bind(view)

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                if (isClickable) {
                    listener.onItemClick(position)
                } else {
                    Toast.makeText(v?.context, itemView.context.resources.getString(R.string.playlists_are_selected), Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onLongClick(p0: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(position)
            }
            return true
        }

        /**
         * Function to render the playlist item
         *
         * @param playlist Playlist object to render
         */
        fun render(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.playlistSize.text =
                itemView.context.resources.getQuantityString(R.plurals.playlist_size, playlist.size, playlist.size)
        }

        /**
         * Function to change the visibility of the tick
         *
         * @param selected Boolean value to set the visibility of the tick
         */
        fun changeVisibility(selected: Boolean) {
            if (selected) {
                binding.selectedTickPlaylist.visibility = View.VISIBLE
            } else {
                binding.selectedTickPlaylist.visibility = View.INVISIBLE
            }
        }

    }

    /**
     * Interface to handle the click and long-click events of the RecyclerView.
     * As interface, it will force implementation of the functions where needed.
     */
    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

}
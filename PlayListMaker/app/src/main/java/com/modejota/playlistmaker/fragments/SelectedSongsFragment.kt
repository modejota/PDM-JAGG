package com.modejota.playlistmaker.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.makeMainSelectorActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.modejota.playlistmaker.*
import com.modejota.playlistmaker.adapters.SongAdapter
import com.modejota.playlistmaker.databinding.FragmentSelectedSongsBinding
import com.modejota.playlistmaker.helpers.SharedData
import com.modejota.playlistmaker.helpers.Utilities
import com.modejota.playlistmaker.models.Song
import java.io.File

/**
 * Class to represent the fragment where selected songs are displayed.
 * It implements interface to handle the click events on the songs.
 */
class SelectedSongsFragment : Fragment(), SongAdapter.OnItemClickListener {

    /**
     * ViewBinding object for the fragment.
     */
    private var _binding: FragmentSelectedSongsBinding? = null

    /**
     * ViewBinding object to access the layout items.
     */
    private val binding get() = _binding!!

    /**
     * Reference to the ActionMenu of the Toolbar.
     */
    private var actionMenu: Menu? = null

    /**
     * List with the IDs of the selected songs.
     */
    private var selectedIndexes = mutableListOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable the menu in the fragment (not dependening on activity).
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectedSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSelectedSongs.layoutManager = LinearLayoutManager(context)
        val selectedSongs: MutableList<Song>
        val newSelectedSongs = Utilities.getSongsByIDs()
        val alreadySelectedSongs: MutableList<Song>

        // If the user has selected a playlist previously, add the already selected songs.
        if (SharedData.getPlaylistPath() != "") {
            alreadySelectedSongs = Utilities.parseM3U(File(SharedData.getPlaylistPath()))
            selectedSongs = (alreadySelectedSongs + newSelectedSongs) as MutableList<Song>
        } else {
            selectedSongs = newSelectedSongs
        }
        // No element is selected at the beginning

        val isClickedList = mutableListOf<Boolean>()
        isClickedList.addAll(Array(selectedSongs.size) { false })

        binding.rvSelectedSongs.adapter = SongAdapter(selectedSongs, isClickedList, this)

        // When confirming the selected songs, manage situations.
        binding.confirmPlaylistFloatingButton.setOnClickListener {
            val selectedAdapter = binding.rvSelectedSongs.adapter as SongAdapter
            val selectedSongsFromAdapter = selectedAdapter.getSongList()
            if (selectedSongs.isNotEmpty()) {
                PlaylistTitleDialog(onSubmitClickListener = { title ->
                    if (title.isBlank()) {
                        Toast.makeText(context, getString(R.string.please_enter_title), Toast.LENGTH_SHORT).show()
                    } else {
                        try {
                            Utilities.createFilePublic(title, selectedSongsFromAdapter)
                            Toast.makeText(context, getString(R.string.playlist_saved), Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, getString(R.string.error_saving_playlist), Toast.LENGTH_SHORT).show()
                        }
                    }
                }).show(childFragmentManager, "PlaylistTitleDialog")
            } else {
                Toast.makeText(context, getString(R.string.no_songs_selected), Toast.LENGTH_SHORT).show()
            }
        }

        // Object to manage the drag and drop functionality. (Reorder the songs)
        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN , ItemTouchHelper.LEFT) {
                 override fun onMove(
                     recyclerView: RecyclerView,
                     viewHolder: RecyclerView.ViewHolder,
                     target: RecyclerView.ViewHolder
                 ): Boolean {
                     val initial = viewHolder.adapterPosition
                     val final = target.adapterPosition
                     val adapter = binding.rvSelectedSongs.adapter as SongAdapter
                     adapter.moveSong(initial, final)
                     return true
                 }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = (binding.rvSelectedSongs.adapter as SongAdapter)
                    val position = viewHolder.adapterPosition
                    selectedIndexes.remove(adapter.getSongPosition(position).ID)
                    adapter.deleteSong(position)
                    Toast.makeText(context, getString(R.string.confirm_song_deleted), Toast.LENGTH_SHORT).show()
                    showActionMenu(selectedIndexes.isNotEmpty())
                }

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                }
        })
        touchHelper.attachToRecyclerView(binding.rvSelectedSongs)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        actionMenu = menu   // Save the menu for later use.
        inflater.inflate(R.menu.action_bar_delete, menu)
        showActionMenu(false)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.selectedSongsDelete -> { confirmDelete() }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Function to show or hide the ActionMenu.
     *
     * @param show Boolean to show or hide the ActionMenu.
     */
    private fun showActionMenu(show: Boolean) {
        actionMenu?.findItem(R.id.selectedSongsDelete)?.isVisible = show
    }

    /**
     * Function to confirm the deletion of the selected songs.
     */
    private fun confirmDelete() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(getString(R.string.delete_songs_title))
        alertDialog.setMessage(getString(R.string.delete_songs_message))
        alertDialog.setPositiveButton(getString(R.string.affirmative)) { _, _ ->
            val adapter = binding.rvSelectedSongs.adapter as SongAdapter
            selectedIndexes.forEach {
                val index = adapter.getSongList().indexOfFirst { song -> song.ID == it }
                adapter.deleteSong(index)
            }
            selectedIndexes.clear()  // After deleting, no songs are selected and no menu is shown.
            showActionMenu(false)
            Toast.makeText(context, getString(R.string.confirm_songs_deleted), Toast.LENGTH_SHORT).show()
        }
        alertDialog.setNegativeButton(getString(R.string.negative)) { _, _ -> }
        alertDialog.show()
    }

    /**
     * Function to confirm the redirection to a music app
     */
    private fun confirmRedirectToMusicApp() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(getString(R.string.redirect_title))
        alertDialog.setMessage(getString(R.string.redirect_text))
        alertDialog.setPositiveButton(getString(R.string.affirmative)) { _, _ ->
            val intent = makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.no_music_player_found), Toast.LENGTH_SHORT).show()
            }
        }
        alertDialog.setNegativeButton(getString(R.string.negative)) { _, _ -> }
        alertDialog.show()
    }

    /**
     * Function overriding from own interface. Called when the user clicks on a song in the list.
     *
     * @param position Position in the RecyclerView of the clicked song
     */
    override fun onItemClick(position: Int) {
        val adapter = binding.rvSelectedSongs.adapter as SongAdapter
        val it = adapter.getSongPosition(position)
        if (selectedIndexes.contains(it.ID)) {
            selectedIndexes.remove(it.ID)
        } else {
            selectedIndexes.add(it.ID)
        }
        adapter.changeVisibility(position) // Change the visibility of the tick for the item clicked
        showActionMenu(selectedIndexes.isNotEmpty()) // ActionMenu shown only if there are selected items
    }

}
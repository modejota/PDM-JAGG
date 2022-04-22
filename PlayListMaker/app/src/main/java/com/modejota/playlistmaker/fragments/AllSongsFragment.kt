package com.modejota.playlistmaker.fragments

import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.modejota.playlistmaker.R
import com.modejota.playlistmaker.helpers.SharedData
import com.modejota.playlistmaker.adapters.SongAdapter
import com.modejota.playlistmaker.databinding.FragmentAllSongsBinding

/**
 * Fragment to display all songs in the device's storage.
 * It implements interface to handle the click on a song.
 */
class AllSongsFragment : Fragment(), SongAdapter.OnItemClickListener {

    /**
     * ViewBinding object for the fragment
     */
    private var _binding: FragmentAllSongsBinding? = null

    /**
     * ViewBinding object's getter for the fragment
     */
    private val binding get() = _binding!!

    /**
     * Reference to the upper ActionMenu in the toolbar
     */
    private var actionMenu: Menu? = null

    /**
     * List of selected songs' IDs (to be deleted or added to the playlist in the next step)
     */
    private val selectedIndexes = mutableListOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable the menu in the fragment (not relying on the activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        // Object to manage the swipe left to delete item
        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = (binding.rvAllSongs.adapter as SongAdapter)
                val position = viewHolder.adapterPosition
                selectedIndexes.remove(adapter.getSongPosition(position).ID)
                adapter.deleteSong(position)
                Toast.makeText(context, getString(R.string.confirm_song_deleted), Toast.LENGTH_SHORT).show()
                showActionMenu(selectedIndexes.isNotEmpty())
            }
        })
        touchHelper.attachToRecyclerView(binding.rvAllSongs)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        actionMenu = menu   // Keep the reference for future use
        inflater.inflate(R.menu.action_bar_all, menu)
        showActionMenu(false)
        return super.onCreateOptionsMenu(menu, inflater)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.allSongsAdd -> {
                confirmAdd()
            }
            R.id.allSongsDelete -> {
                confirmDelete()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Initialize the RecyclerView and its adapter with the list of songs, none of them selected
     */
    private fun initRecyclerView(){
        // Nothing is selected at the beginning
        val isClickedList = mutableListOf<Boolean>()
        isClickedList.addAll(Array(SharedData.getAllSongs().size) { false })
        binding.rvAllSongs.layoutManager= LinearLayoutManager(context)
        binding.rvAllSongs.adapter= SongAdapter(SharedData.getAllSongs(), isClickedList, this)
    }

    /**
     * Function to show or hide the upper ActionMenu and its items
     * @param show Boolean to show or hide the ActionMenu
     */
    private fun showActionMenu(show: Boolean) {
        actionMenu?.let {
            it.findItem(R.id.allSongsAdd).isVisible = show
            it.findItem(R.id.allSongsDelete).isVisible = show
        }
    }

    /**
     * Function to confirm the adding of selected songs to the list of selected songs to be used in the next step
     */
    private fun confirmAdd() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(getString(R.string.add_songs_title))
        alertDialog.setMessage(getString(R.string.add_songs_message))
        alertDialog.setPositiveButton(getString(R.string.affirmative)) { _, _ ->
            SharedData.addSongsID(selectedIndexes)
            // Re-draw the view with everything non-ticked
            initRecyclerView()
            // Reset the selected indexes and hide the menu after the layout is redrawn
            binding.rvAllSongs.post {
                selectedIndexes.clear()
                showActionMenu(false)
            }
            Toast.makeText(context, getString(R.string.confirm_songs_added), Toast.LENGTH_SHORT).show()
        }
        alertDialog.setNegativeButton(getString(R.string.negative)) { _, _ -> }
        alertDialog.show()
    }

    /**
     * Function to confirm the deletion of selected songs from the list shown.
     * After deleting, songs will not be displayed and can't be added until reset.
     */
    private fun confirmDelete() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(getString(R.string.delete_songs_title))
        alertDialog.setMessage(getString(R.string.delete_songs_message))
        alertDialog.setPositiveButton(getString(R.string.affirmative)) { _, _ ->
            val adapter = binding.rvAllSongs.adapter as SongAdapter
            selectedIndexes.forEach {
                val index = adapter.getSongList().indexOfFirst { song -> song.ID == it }
                adapter.deleteSong(index)
            }
            selectedIndexes.clear() // Reset the selected indexes and hide the menu after action is performed
            showActionMenu(false)
            Toast.makeText(context, getString(R.string.confirm_songs_deleted), Toast.LENGTH_SHORT).show()
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
        val adapter = binding.rvAllSongs.adapter as SongAdapter
        val it = adapter.getSongPosition(position)
        if (selectedIndexes.contains(it.ID)) {
            selectedIndexes.remove(it.ID)
        } else {
            selectedIndexes.add(it.ID)
        }
        adapter.changeVisibility(position)  // Change the visibility of the tick for the item clicked
        showActionMenu(selectedIndexes.isNotEmpty())    // ActionMenu shown only if there are selected items
    }
}
package com.modejota.playlistmaker

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.modejota.playlistmaker.databinding.FragmentAllSongsBinding

/**
 * Fragment to display all songs in the device's storage.
 * It implements interface to handle the click on a song.
 */
class AllSongsFragment : Fragment(), SongAdapter.OnItemClickListener{

    /**
     * ViewBinding object for the fragment
     */
    private var _binding: FragmentAllSongsBinding? = null

    /**
     * ViewBinding object for the fragment
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

        // No element is selected at the beginning
        val isClickedList = mutableListOf<Boolean>()
        isClickedList.addAll(Array(SharedData.getAllSongs().size) { false })

        binding.rvAllSongs.layoutManager= LinearLayoutManager(context)
        binding.rvAllSongs.adapter= SongAdapter(SharedData.getAllSongs(), isClickedList, this)
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
            val allSongs = SharedData.getAllSongs()
            selectedIndexes.forEach {
                val index = allSongs.indexOfFirst { song -> song.ID == it }
                // To avoid IllegalStateException: cannot call this method while RecyclerView is computing a layout
                binding.rvAllSongs.post {   // Perform click to deselect the song and hide the tick
                    binding.rvAllSongs.findViewHolderForAdapterPosition(index)?.itemView?.performClick()
                }
            }
            // Reset the selected indexes and hide the menu after clicks on the items are computed
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
        val it = adapter.getSongList()[position]
        if (selectedIndexes.contains(it.ID)) {
            selectedIndexes.remove(it.ID)
        } else {
            selectedIndexes.add(it.ID)
        }
        adapter.changeVisibility(position)  // Change the visibility of the tick for the item clicked
        showActionMenu(selectedIndexes.isNotEmpty())    // ActionMenu shown only if there are selected items
    }
}
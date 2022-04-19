package com.modejota.playlistmaker

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.modejota.playlistmaker.databinding.FragmentSelectedSongsBinding
import java.io.File

class SelectedSongsFragment : Fragment(), SongAdapter.OnItemClickListener {

    private var _binding: FragmentSelectedSongsBinding? = null
    private val binding get() = _binding!!
    private var actionMenu: Menu? = null
    private var selectedIndexes = mutableListOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        if (SharedData.getPlaylistPath() != "") {
            alreadySelectedSongs = Utilities.parseM3U8(File(SharedData.getPlaylistPath()))
            selectedSongs = (alreadySelectedSongs + newSelectedSongs) as MutableList<Song>
        } else {
            selectedSongs = newSelectedSongs
        }
        val isClickedList = mutableListOf<Boolean>()
        isClickedList.addAll(Array(selectedSongs.size) { false })

        binding.rvSelectedSongs.adapter = SongAdapter(selectedSongs, isClickedList, this)

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

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
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
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
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
        actionMenu = menu
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

    private fun showActionMenu(show: Boolean) {
        actionMenu?.findItem(R.id.selectedSongsDelete)?.isVisible = show
    }

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
            selectedIndexes.clear()
            showActionMenu(false)
        }
        alertDialog.setNegativeButton(getString(R.string.negative)) { _, _ -> }
        alertDialog.show()
    }

    override fun onItemClick(position: Int) {
        val adapter = binding.rvSelectedSongs.adapter as SongAdapter
        val it = adapter.getSongList()[position]
        if (selectedIndexes.contains(it.ID)) {
            selectedIndexes.remove(it.ID)
        } else {
            selectedIndexes.add(it.ID)
        }
        adapter.changeVisibility(position)
        showActionMenu(selectedIndexes.isNotEmpty())
    }

}
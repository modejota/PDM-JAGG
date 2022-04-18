package com.modejota.playlistmaker

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.modejota.playlistmaker.databinding.FragmentAllSongsBinding

class AllSongsFragment : Fragment(), SongAdapter.OnItemClickListener{

    private var _binding: FragmentAllSongsBinding? = null
    private val binding get() = _binding!!
    private var actionMenu: Menu? = null
    private val selectedIndexes = mutableListOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        actionMenu = menu
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
    private fun showActionMenu(show: Boolean) {
        actionMenu?.let {
            it.findItem(R.id.allSongsAdd).isVisible = show
            it.findItem(R.id.allSongsDelete).isVisible = show
        }
    }

    private fun confirmAdd() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Add songs")
        alertDialog.setMessage("Are you sure you want to add this songs?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            SharedData.addSongsID(selectedIndexes)
            val allSongs = SharedData.getAllSongs()
            selectedIndexes.forEach {
                val index = allSongs.indexOfFirst { song -> song.ID == it }
                binding.rvAllSongs.post {
                    binding.rvAllSongs.findViewHolderForAdapterPosition(index)?.itemView?.performClick()
                }
            }
            binding.rvAllSongs.post {
                selectedIndexes.clear()
                showActionMenu(false)
            }

        }
        alertDialog.setNegativeButton("No") { _, _ -> }
        alertDialog.show()
    }

    private fun confirmDelete() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Delete songs")
        alertDialog.setMessage("Are you sure you want to delete this songs?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            val adapter = binding.rvAllSongs.adapter as SongAdapter
            selectedIndexes.forEach {
                val index = adapter.getSongList().indexOfFirst { song -> song.ID == it }
                adapter.deleteSong(index)
            }
            selectedIndexes.clear()
            showActionMenu(false)
        }
        alertDialog.setNegativeButton("No") { _, _ -> }
        alertDialog.show()
    }

    override fun onItemClick(position: Int) {
        val adapter = binding.rvAllSongs.adapter as SongAdapter
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
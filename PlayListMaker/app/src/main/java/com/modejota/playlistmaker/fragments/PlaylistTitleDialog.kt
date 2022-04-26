package com.modejota.playlistmaker.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.modejota.playlistmaker.databinding.DialogPlaylistTitleBinding

/**
 * Class to represent the popup dialog asking for the title of a playlist when saving one.
 * It extends an special type of Fragment that is used to show dialogs.
 *
 * @property onSubmitClickListener The listener to be called when the user clicks the submit button.
 *
 * @author José Alberto Gómez García    -   @modejota
 *
 */
class PlaylistTitleDialog(
    private val onSubmitClickListener: (String) -> Unit
): DialogFragment() {

    /**
     * ViewBinding object for the DialogFragment.
     * @property binding
     */
    private lateinit var binding : DialogPlaylistTitleBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogPlaylistTitleBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        binding.bInsertName.setOnClickListener {
            onSubmitClickListener.invoke(binding.etName.text.toString())
            dismiss()   // To close the dialog after submiting the name of the playlist.
        }

        // Create the dialog itself, with transparent background and show it.
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}
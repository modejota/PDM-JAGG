package com.modejota.playlistmaker


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.modejota.playlistmaker.databinding.DialogPlaylistTitleBinding

class PlaylistTitleDialog(
    private val onSubmitClickListener: (String) -> Unit
): DialogFragment() {

    private lateinit var binding : DialogPlaylistTitleBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogPlaylistTitleBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        binding.bAddQuantity.setOnClickListener {
            onSubmitClickListener.invoke(binding.etAmount.text.toString())
            dismiss()
        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}
package com.modejota.unitcardgame

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.modejota.unitcardgame.databinding.ShowQrdialogBinding

class ShowQrDialog(
    private val image: Bitmap,
    private val onSubmitClickListener: (Unit) -> Unit
) : DialogFragment() {

    private lateinit var binding : ShowQrdialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?) :  Dialog {
        binding = ShowQrdialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        binding.initiateGameButton.setOnClickListener {
            onSubmitClickListener.invoke(Unit)
            dismiss()
        }
        binding.qrcode.setImageBitmap(image)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }


}
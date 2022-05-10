package com.modejota.unitcardgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.modejota.unitcardgame.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SET UP THE RECYCLER VIEW, THE LAST CARD PLAYED AND THE CARD DECK BUTTON
    }
}
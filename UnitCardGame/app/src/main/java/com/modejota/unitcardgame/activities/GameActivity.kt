package com.modejota.unitcardgame.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.modejota.unitcardgame.otherstuff.CardAdapter
import com.modejota.unitcardgame.databinding.ActivityGameBinding
import com.modejota.unitcardgame.model.Card
import com.modejota.unitcardgame.model.CardType

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMyCards.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Arraylist de prueba, ya veré como consigo esto a partir del servidor
        val mazo = ArrayList<Card>()
        for (i in 0..18) {
            mazo.add(Card(i % 2, "BLUE", CardType.NUMBER))
            mazo.add(Card(i % 2, "GREEN", CardType.NUMBER))
            mazo.add(Card(i % 2, "RED", CardType.NUMBER))
            mazo.add(Card(i % 2, "YELLOW", CardType.NUMBER))
        }

        binding.rvMyCards.adapter = CardAdapter(mazo)

    }
}
package com.modejota.unitcardgame.otherstuff

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.modejota.unitcardgame.R
import com.modejota.unitcardgame.databinding.ItemCardBinding
import com.modejota.unitcardgame.model.Card

class CardAdapter(
    private val cards: MutableList<Card>,
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CardViewHolder(layoutInflater.inflate(R.layout.item_card, parent, false))
    }


    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.render(cards[position])
    }

    override fun getItemCount(): Int = cards.size


    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemCardBinding.bind(view)

        fun render(card: Card) {
            binding.imageCarta.setImageResource(R.drawable.carta_wildcards)
        }
    }

}
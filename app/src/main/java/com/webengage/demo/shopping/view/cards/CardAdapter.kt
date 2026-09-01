package com.webengage.demo.shopping.view.cards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webengage.demo.shopping.R

class CardAdapter(
    private val cards: List<Card>,
    private val onClick: (Card) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val artwork: View = itemView.findViewById(R.id.cardArtwork)
        private val artLimit: TextView = itemView.findViewById(R.id.cardArtLimit)
        private val name: TextView = itemView.findViewById(R.id.cardName)
        private val desc: TextView = itemView.findViewById(R.id.cardDesc)

        fun bind(card: Card) {
            name.text = card.name
            desc.text = card.description
            artLimit.text = card.limit

            val resId = itemView.resources.getIdentifier(
                card.imageRes, "drawable", itemView.context.packageName
            )
            if (resId != 0) {
                artwork.setBackgroundResource(resId)
            } else {
                artwork.setBackgroundResource(R.drawable.card_gradient_1)
            }

            itemView.setOnClickListener { onClick(card) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size
}

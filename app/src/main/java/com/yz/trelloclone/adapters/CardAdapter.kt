package com.yz.trelloclone.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yz.trelloclone.databinding.CardItemBinding
import com.yz.trelloclone.models.Card

class CardAdapter(private val context: Context) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var cardList = ArrayList<Card>()

    private var onClickListener: OnClickListener? = null

    class CardViewHolder(item: CardItemBinding) : RecyclerView.ViewHolder(item.root) {
        val cardItem = item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            CardItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]

        holder.cardItem.tvCardName.text = card.cardTitle

        if (card.cardColor.isNotEmpty()) {
            holder.cardItem.viewLabelColor.setBackgroundColor(Color.parseColor(card.cardColor))
            holder.cardItem.viewLabelColor.visibility = View.VISIBLE
        } else
            holder.cardItem.viewLabelColor.visibility = View.GONE

        holder.cardItem.root.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(cardPosition: Int)
    }

    fun setData(cardList: ArrayList<Card>) {
        this.cardList = cardList
    }
}
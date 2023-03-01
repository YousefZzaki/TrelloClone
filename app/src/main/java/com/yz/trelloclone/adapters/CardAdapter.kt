package com.yz.trelloclone.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yz.trelloclone.activities.BaseActivity.Companion.TAG
import com.yz.trelloclone.activities.TaskListActivity
import com.yz.trelloclone.databinding.CardItemBinding
import com.yz.trelloclone.models.Card
import com.yz.trelloclone.models.SelectedMembers

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
                onClickListener!!.onClick(holder.adapterPosition)
            }
        }
        Log.e(TAG, "Card adapter board member ${(context as TaskListActivity).boardAssignedMembersDetails.size}")
        if ((context as TaskListActivity).boardAssignedMembersDetails.size > 0) {
            val selectedMembers = ArrayList<SelectedMembers>()
            for (i in context.boardAssignedMembersDetails.indices) {
                for (j in card.assignedTo) {
                    if (context.boardAssignedMembersDetails[i].id == j) {
                        val member = SelectedMembers(
                            context.boardAssignedMembersDetails[i].id,
                            context.boardAssignedMembersDetails[i].image
                        )
                        selectedMembers.add(member)
                    }
                }
            }
            if (selectedMembers.size > 0) {
                if (selectedMembers.size == 1 && selectedMembers[0].id == card.createdBy) {
                    holder.cardItem.rvSelectedMembers.visibility = View.GONE
                } else {
                    holder.cardItem.rvSelectedMembers.visibility = View.VISIBLE

                    holder.cardItem.rvSelectedMembers.layoutManager = GridLayoutManager(context, 4)
                    val adapter = CardMemberListItemAdapter(context, false)
                    adapter.setData(selectedMembers)
                    holder.cardItem.rvSelectedMembers.adapter = adapter
                    adapter.setOnClickLister(object : CardMemberListItemAdapter.OnClickListener {
                        override fun onClick() {
                            if (onClickListener != null) {
                                onClickListener!!.onClick(holder.adapterPosition)
                            }
                        }

                    })
                }

            } else {
                holder.cardItem.rvSelectedMembers.visibility = View.GONE
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
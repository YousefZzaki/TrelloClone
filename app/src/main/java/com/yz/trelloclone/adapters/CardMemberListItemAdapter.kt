package com.yz.trelloclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yz.trelloclone.R
import com.yz.trelloclone.databinding.ItemCardSelectedMemberBinding
import com.yz.trelloclone.models.SelectedMembers

open class CardMemberListItemAdapter(
    val context: Context,
    private val assignMember: Boolean
) : RecyclerView.Adapter<CardMemberListItemAdapter.CardMemberViewHolder>() {

    private lateinit var membersList: ArrayList<SelectedMembers>

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardMemberViewHolder {
        return CardMemberViewHolder(
            ItemCardSelectedMemberBinding.inflate(
                LayoutInflater.from(
                    context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CardMemberViewHolder, position: Int) {

        val item = membersList[position]

        if (position == membersList.size - 1 && assignMember) {
            holder.cardMemberItem.ivAddMember.visibility = View.VISIBLE
            holder.cardMemberItem.ivSelectedMember.visibility = View.GONE
        } else {
            holder.cardMemberItem.ivAddMember.visibility = View.GONE
            holder.cardMemberItem.ivSelectedMember.visibility = View.VISIBLE

            Glide
                .with(context)
                .load(item.image)
                .centerCrop()
                .placeholder(R.drawable.ic_person_placeholder)
                .into(holder.cardMemberItem.ivSelectedMember)
        }

        holder.cardMemberItem.root.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick()
            }
        }
    }


    override fun getItemCount(): Int {
        return membersList.size
    }

    class CardMemberViewHolder(item: ItemCardSelectedMemberBinding) :
        RecyclerView.ViewHolder(item.root) {
        val cardMemberItem = item
    }

    interface OnClickListener {
        fun onClick()
    }

    fun setOnClickLister(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun setData(list: ArrayList<SelectedMembers>) {
        this.membersList = list
    }
}
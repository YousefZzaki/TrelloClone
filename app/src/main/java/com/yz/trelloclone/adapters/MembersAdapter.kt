package com.yz.trelloclone.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Constants
import com.yz.trelloclone.Utils.Constants.SELECTED
import com.yz.trelloclone.Utils.Constants.UN_SELECTED
import com.yz.trelloclone.activities.BaseActivity.Companion.TAG
import com.yz.trelloclone.databinding.MemberItemBinding
import com.yz.trelloclone.models.User

class MembersAdapter(val context: Context) :
    RecyclerView.Adapter<MembersAdapter.MemberViewHolder>() {

    private var membersList: ArrayList<User> = ArrayList()

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        return (MemberViewHolder(
            MemberItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ))
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val item = membersList[position]


        Glide.with(context)
            .load(item.image)
            .placeholder(R.drawable.ic_person_placeholder)
            .into(holder.memberItem.ivMemberItem)

        holder.memberItem.tvMembersName.text = item.name
        holder.memberItem.tvMembersEmail.text = item.email

        if (item.isSelected) {
            holder.memberItem.ivIsSelected.visibility = View.VISIBLE
        } else
            holder.memberItem.ivIsSelected.visibility = View.GONE

        holder.memberItem.root.setOnClickListener {
            if (onClickListener != null){
                if (item.isSelected){
                    onClickListener?.onClick(position, item, UN_SELECTED)
                }
                else{
                    onClickListener?.onClick(position, item, SELECTED)
                }
                Log.e(TAG, "isSelected ${item.isSelected}")
            }
            Log.e(TAG, "isSelected ${item.isSelected}")
        }
    }

    override fun getItemCount(): Int {
        return membersList.size
    }

    fun setData(membersList: ArrayList<User>) {
        this.membersList = membersList
    }

    fun setOnclickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, user: User, action: String)
    }

    class MemberViewHolder(item: MemberItemBinding) : RecyclerView.ViewHolder(item.root) {
        val memberItem = item
    }

}
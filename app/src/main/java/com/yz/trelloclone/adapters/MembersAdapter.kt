package com.yz.trelloclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yz.trelloclone.R
import com.yz.trelloclone.databinding.MemberItemBinding
import com.yz.trelloclone.models.User

class MembersAdapter(val context: Context): RecyclerView.Adapter<MembersAdapter.MemberViewHolder>() {

    private var membersList: ArrayList<User> = ArrayList()

    class MemberViewHolder(item: MemberItemBinding): RecyclerView.ViewHolder(item.root) {
        val memberItem = item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        return (MemberViewHolder(MemberItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)))
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val item = membersList[position]


        Glide.with(context)
            .load(item.image)
            .placeholder(R.drawable.ic_person_placeholder)
            .into(holder.memberItem.ivMemberItem)

        holder.memberItem.tvMembersName.text = item.name
        holder.memberItem.tvMembersEmail.text = item.email
    }

    override fun getItemCount(): Int {
       return membersList.size
    }

    fun setData(membersList: ArrayList<User>){
        this.membersList = membersList
    }
}
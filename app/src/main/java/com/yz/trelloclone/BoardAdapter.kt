package com.yz.trelloclone

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yz.trelloclone.databinding.ItemBoardRvBinding
import com.yz.trelloclone.models.Board

open class BoardAdapter(private val context: Context): RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    private val itemList = emptyList<Board>()

    private val onClickListener: OnClickListener? = null

    class BoardViewHolder(item: ItemBoardRvBinding): RecyclerView.ViewHolder(item.root){
        val boardImage = item.ivBoardImage
        val boardName = item.tvBoardName
        val createdBy = item.tvCreatedBy
        val createdIn = item.tvCreatedIn
        val itemLayout = item.itemLayout
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        return BoardViewHolder(ItemBoardRvBinding.inflate(LayoutInflater.from(context), parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val item = itemList[position]

        if (holder is BoardViewHolder){

            holder.boardName.text = item.name
            Glide.with(context)
                .load(item.image)
                .centerCrop()
                .into(holder.boardImage)

            holder.boardName.text = item.name
            holder.createdBy.text = "Created by: ${item.createdBy}"
            holder.createdIn.text = item.createdIn

            holder.itemLayout.setOnClickListener {
                TODO("Go to task activity")
            }
        }

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    interface OnClickListener{
        fun onClick(position: Int, board: Board)
    }
}
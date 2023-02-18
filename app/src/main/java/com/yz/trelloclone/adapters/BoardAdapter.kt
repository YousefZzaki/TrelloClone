package com.yz.trelloclone.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yz.trelloclone.R
import com.yz.trelloclone.activities.BaseActivity.Companion.TAG
import com.yz.trelloclone.databinding.ItemBoardRvBinding
import com.yz.trelloclone.models.Board

open class BoardAdapter(private val context: Context) :
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    private var itemList = emptyList<Board>()

    private var onClickListener: OnClickListener? = null

    class BoardViewHolder(item: ItemBoardRvBinding) : RecyclerView.ViewHolder(item.root) {
        val boardImage = item.ivBoardImage
        val boardName = item.tvBoardName
        val createdBy = item.tvCreatedBy
        val createdIn = item.tvCreatedIn
        val itemLayout = item.itemLayout
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        return BoardViewHolder(
            ItemBoardRvBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val item = itemList[position]

        holder.boardName.text = item.name
        if (item.image.isNotEmpty()) {
            Glide.with(context)
                .load(item.image)
                .placeholder(R.drawable.ic_board_place_holder)
                .centerCrop()
                .into(holder.boardImage)
        }
        Log.e(TAG, "Adapter user image${item.image}")

        holder.boardName.text = item.name
        holder.createdBy.text = "Created by: ${item.createdBy}"
        holder.createdIn.text = item.createdIn

        holder.itemLayout.setOnClickListener {
            this.onClickListener?.onClick(position, item)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setData(boardList: ArrayList<Board>) {
        itemList = boardList
    }

    interface OnClickListener {
        fun onClick(position: Int, board: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}
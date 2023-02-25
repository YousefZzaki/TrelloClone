package com.yz.trelloclone.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yz.trelloclone.activities.BaseActivity.Companion.TAG
import com.yz.trelloclone.databinding.ItemLabelColorBinding

class LabelColorListItemsAdapter(val context: Context, private val selectedColor: String) :
    RecyclerView.Adapter<LabelColorListItemsAdapter.ColorViewHolder>() {

    private var colorList = ArrayList<String>()

     var onClickListener: OnClickListener? = null

    class ColorViewHolder(item: ItemLabelColorBinding) : RecyclerView.ViewHolder(item.root) {
        val colorItem = item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return (ColorViewHolder(
            ItemLabelColorBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        ))
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val item = colorList[position]

        holder.colorItem.viewLabelColor.setBackgroundColor(Color.parseColor(item))

        if (item == selectedColor) {
            holder.colorItem.viewLabelColor.setBackgroundColor(Color.parseColor(selectedColor))
            holder.colorItem.ivCheckColor.visibility = View.VISIBLE
            Log.e(TAG, "Check box visible")
        } else {
            holder.colorItem.ivCheckColor.visibility = View.GONE
            Log.e(TAG, "Check box invisible")
        }

        holder.colorItem.root.setOnClickListener {
            if (onClickListener != null) {
                Log.e(TAG, "Selected color: $item")
                onClickListener?.onClick(position, item)
            }
            holder.colorItem.ivCheckColor.visibility = View.VISIBLE
            Log.e(TAG, "Check box visible")
        }
    }

    fun setData(colorList: ArrayList<String>){
        this.colorList = colorList
    }

    override fun getItemCount(): Int {
        return colorList.size
    }

    interface OnClickListener {
        fun onClick(position: Int, selectedColor: String)
    }
}
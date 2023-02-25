package com.yz.trelloclone.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.trelloclone.adapters.LabelColorListItemsAdapter
import com.yz.trelloclone.databinding.DialogColorListBinding

abstract class LabelColorListDialog(
    context: Context,
    private var list: ArrayList<String>,
    private var selectedColor: String = ""
) : Dialog(context) {

    private var adapter: LabelColorListItemsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dialog = DialogColorListBinding.inflate(LayoutInflater.from(context))

        setContentView(dialog.root)

        setupRecyclerView(dialog)
    }

    private fun setupRecyclerView(view: DialogColorListBinding) {
        view.rvColorList.layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemsAdapter(context, selectedColor)
        adapter?.setData(list)
        view.rvColorList.adapter = adapter
        adapter?.onClickListener = object : LabelColorListItemsAdapter.OnClickListener{
            override fun onClick(position: Int, selectedColor: String) {
                dismiss()
                onItemSelected(selectedColor)
            }
        }
    }

    protected abstract fun onItemSelected(color: String)
}
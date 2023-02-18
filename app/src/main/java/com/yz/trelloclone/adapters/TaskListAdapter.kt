package com.yz.trelloclone.adapters

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.yz.trelloclone.activities.BaseActivity.Companion.TAG
import com.yz.trelloclone.databinding.ItemTaskBinding
import com.yz.trelloclone.models.Task

class TaskListAdapter() : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    private var listItem: ArrayList<Task> = ArrayList()

    class TaskViewHolder(item: ItemTaskBinding) : RecyclerView.ViewHolder(item.root) {
        val tvAddList = item.tvAddTaskList
        val llTaskItem = item.llTaskItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        val item = ItemTaskBinding.inflate(LayoutInflater.from(parent.context))

        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp().toPx()), 0, (40.toDp().toPx()), 0)

        item.root.layoutParams = layoutParams


        return TaskViewHolder(item)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        if (position == listItem.size - 1) {
            holder.tvAddList.visibility = View.VISIBLE
            holder.llTaskItem.visibility = View.GONE
        } else {
            holder.tvAddList.visibility = View.GONE
            holder.llTaskItem.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    private fun Int.toDp(): Int {
        Log.e(
            TAG, "screen metric dp this: $this, " +
                    "${Resources.getSystem().displayMetrics.density}"
        )
        return (this / Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun Int.toPx(): Int {
        Log.e(TAG, "screen metric px this: $this, ${Resources.getSystem().displayMetrics.density}")
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun setData(taskList: ArrayList<Task>) {
        this.listItem = taskList
    }
}
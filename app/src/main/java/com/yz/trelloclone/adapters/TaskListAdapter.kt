package com.yz.trelloclone.adapters

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.yz.trelloclone.R
import com.yz.trelloclone.activities.BaseActivity.Companion.TAG
import com.yz.trelloclone.activities.TaskListActivity
import com.yz.trelloclone.databinding.ItemTaskBinding
import com.yz.trelloclone.models.Task
import kotlinx.coroutines.selects.whileSelect
import kotlin.contracts.contract

class TaskListAdapter(private val context: Context) :
    RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    private var listItem: ArrayList<Task> = ArrayList()

    class TaskViewHolder(item: ItemTaskBinding) : RecyclerView.ViewHolder(item.root) {
        val tvAddList = item.tvAddTaskList
        val llTaskItem = item.llTaskItem
        val tvTaskListTitle = item.tvTaskListTitle
        val cvAddList = item.cvAddTaskListName
        val ibDoneListName = item.ibDoneListName
        val ibCloseListName = item.ibCloseListName
        val etTaskListName = item.etTaskListName
        val ibEditListName = item.ibEditListName
        val llTitleView = item.llTitleView
        val cvEditTaskListName = item.cvEditTaskListName
        val ibDeleteList = item.ibDeleteList
        val ibDoneEditListName = item.ibDoneEditListName
        val ibCancelEditListName = item.ibCloseEditableView
        val etEditListName = item.etEditTaskListName
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

        val item = listItem[position]

        if (position == listItem.size - 1) {
            Log.e(TAG, "pos: $position, last item: ${listItem[listItem.size - 1]}")
            holder.tvAddList.visibility = View.VISIBLE
            holder.llTaskItem.visibility = View.GONE
        } else {
            holder.tvAddList.visibility = View.GONE
            holder.llTaskItem.visibility = View.VISIBLE
        }

        holder.tvTaskListTitle.text = item.taskTitle

        holder.tvAddList.setOnClickListener {
            holder.tvAddList.visibility = View.GONE
            holder.cvAddList.visibility = View.VISIBLE
        }
        holder.ibCloseListName.setOnClickListener {
            holder.tvAddList.visibility = View.VISIBLE
            holder.cvAddList.visibility = View.GONE
        }

        holder.ibDoneListName.setOnClickListener {
            //Add list to board in DB
            val taskName = holder.etTaskListName.text.toString()
            if (taskName.isNotEmpty()) {
                context as TaskListActivity
                context.createTaskList(taskName)
            } else {
                Toast.makeText(
                    context,
                    "Please enter task name",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        holder.ibEditListName.setOnClickListener {
            holder.llTitleView.visibility = View.GONE
            holder.cvEditTaskListName.visibility = View.VISIBLE

            holder.etEditListName.setText(item.taskTitle)
        }

        holder.ibCancelEditListName.setOnClickListener {
            holder.cvEditTaskListName.visibility = View.GONE
            holder.llTitleView.visibility = View.VISIBLE
            holder.etEditListName.text.clear()
        }

        holder.ibDoneEditListName.setOnClickListener {
            val listName = holder.etEditListName.text.toString()

            if (listName.isNotEmpty()) {
                context as TaskListActivity
                context.updateTaskName(position, listName, item)
            } else {
                Toast.makeText(
                    context,
                    "Please enter new the task title",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        holder.ibDeleteList.setOnClickListener {
            showDeleteAlertDialog(position)
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

    private fun showDeleteAlertDialog(position: Int){

        val builder = AlertDialog.Builder(context)

        builder.setTitle("Deleting item")

        builder.setMessage("Are you sure to delete this item")

        builder.setNegativeButton("No"){ _, _ ->}
        builder.setPositiveButton("Yes") {dialog, _ ->
            context as TaskListActivity
            context.deleteTask(position)
            dialog.dismiss()
        }

        builder.create().show()
    }

    fun setData(taskList: ArrayList<Task>) {
        this.listItem = taskList
    }
}
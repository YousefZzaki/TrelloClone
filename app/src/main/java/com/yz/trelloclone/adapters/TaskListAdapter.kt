package com.yz.trelloclone.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yz.trelloclone.R
import com.yz.trelloclone.activities.BaseActivity.Companion.TAG
import com.yz.trelloclone.activities.TaskListActivity
import com.yz.trelloclone.databinding.DeleteDialogBinding
import com.yz.trelloclone.databinding.ItemTaskBinding
import com.yz.trelloclone.models.Task

class TaskListAdapter(private val context: Context) :
    RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    private var listItem: ArrayList<Task> = ArrayList()

    class TaskViewHolder(item: ItemTaskBinding) : RecyclerView.ViewHolder(item.root) {

        val taskItem = item

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
            holder.taskItem.tvAddTaskList.visibility = View.VISIBLE
            holder.taskItem.llTaskItem.visibility = View.GONE
        } else {
            holder.taskItem.tvAddTaskList.visibility = View.GONE
            holder.taskItem.llTaskItem.visibility = View.VISIBLE
        }

        holder.taskItem.tvTaskListTitle.text = item.taskTitle

        holder.taskItem.tvAddTaskList.setOnClickListener {
            holder.taskItem.tvAddTaskList.visibility = View.GONE
            holder.taskItem.cvAddTaskListName.visibility = View.VISIBLE
        }
        holder.taskItem.ibCloseListName.setOnClickListener {
            holder.taskItem.tvAddTaskList.visibility = View.VISIBLE
            holder.taskItem.cvAddTaskListName.visibility = View.GONE
        }

        holder.taskItem.ibDoneListName.setOnClickListener {
            //Add list to board in DB
            val taskName = holder.taskItem.etTaskListName.text.toString()
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

        //Edit task stuff
        holder.taskItem.ibEditListName.setOnClickListener {
            holder.taskItem.llTitleView.visibility = View.GONE
            holder.taskItem.cvEditTaskListName.visibility = View.VISIBLE

            holder.taskItem.etTaskListName.setText(item.taskTitle)
        }

        holder.taskItem.ibCloseEditableView.setOnClickListener {
            holder.taskItem.cvEditTaskListName.visibility = View.GONE
            holder.taskItem.llTitleView.visibility = View.VISIBLE
            holder.taskItem.etEditTaskListName.text.clear()
        }

        holder.taskItem.ibDoneEditListName.setOnClickListener {
            val listName = holder.taskItem.etEditTaskListName.text.toString()

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

        holder.taskItem.ibDeleteList.setOnClickListener {
            showDeleteAlertDialog(position)
        }
        //-----* END*-----//

        //-----* Add card stuff *-------
        holder.taskItem.tvAddCard.setOnClickListener {

            holder.taskItem.cvAddCard.visibility = View.VISIBLE
            holder.taskItem.tvAddCard.visibility = View.GONE

        }

        holder.taskItem.ibCloseCardName.setOnClickListener {
            holder.taskItem.cvAddCard.visibility = View.GONE
            holder.taskItem.tvAddCard.visibility = View.VISIBLE
        }

        holder.taskItem.ibDoneCardName.setOnClickListener {
            holder.taskItem.cvAddCard.visibility = View.GONE
            holder.taskItem.tvAddCard.visibility = View.VISIBLE

            val cardName = holder.taskItem.etCardName.text.toString()

            context as TaskListActivity
            context.addCard(position, cardName)
        } //-----* END *-------

        val adapter =  CardAdapter(context)
        adapter.setData(item.taskCards)
        holder.taskItem.rvCardList.adapter = adapter
        holder.taskItem.rvCardList.layoutManager = LinearLayoutManager(context)
        holder.taskItem.rvCardList.setHasFixedSize(true)

        adapter.setOnClickListener(object : CardAdapter.OnClickListener{
            override fun onClick(cardPosition: Int) {
                context as TaskListActivity
                context.setCardDetails(holder.adapterPosition,cardPosition)
            }

        })


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

        val dialogLayout = DeleteDialogBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(context, R.style.Theme_Dialog)
        builder.setView(dialogLayout.root)

        val dialog = builder.create()

        dialogLayout.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogLayout.btnDelete.setOnClickListener {
            context as TaskListActivity
            context.deleteTask(position)
            dialog.dismiss()
        }

        dialog.show()
    }

    fun setData(taskList: ArrayList<Task>) {
        this.listItem = taskList
    }
}
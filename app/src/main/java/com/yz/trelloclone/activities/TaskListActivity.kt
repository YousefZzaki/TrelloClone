package com.yz.trelloclone.activities


import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Constants.DOCUMENT_ID
import com.yz.trelloclone.adapters.TaskListAdapter
import com.yz.trelloclone.databinding.ActivityTaskListBinding
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.Board
import com.yz.trelloclone.models.Task

class TaskListActivity : BaseActivity() {

    private var binding: ActivityTaskListBinding? = null
    private lateinit var boardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Get board id from main and pars it and get its board from database
        getBoardId()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding?.taskListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.taskListToolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        binding?.taskListToolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.taskListToolbar?.title = boardDetails.name
        binding?.taskListToolbar?.setTitleTextColor(
            ContextCompat.getColor(
                this,
                R.color.divider_color
            )
        )

    }

    private fun getBoardId() {
        showProgressDialog()
        val boardId: String
        if (intent.hasExtra(DOCUMENT_ID)) {
            boardId = intent.getStringExtra(DOCUMENT_ID).toString()

            Firestore().getBoardDetails(this, boardId)
        }
    }

    fun getBoardFromDB(board: Board) {

        boardDetails = board

        Log.e(TAG, "Board from db $boardDetails")

        setupToolbar()

        val task1 = Task("Task 1")
        board.taskList.add(task1)

        binding?.rvTaskList?.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL, false
            )

        binding?.rvTaskList?.setHasFixedSize(false)

        val adapter = TaskListAdapter(this)
        adapter.setData(board.taskList)
        binding?.rvTaskList?.adapter = adapter

        hideProgressDialog()
    }

    fun onAddUpdateTaskList() {
        hideProgressDialog()

        showProgressDialog()
        Firestore().getBoardDetails(this, boardDetails.documentId)
    }

    fun updateTaskName(position: Int, newTitle: String, model: Task) {
        val task = Task(newTitle, model.createdBy)
        boardDetails.taskList[position] = task
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        //Update the board in database after the new changes
        Firestore().addUpdateTaskList(this, boardDetails)
    }

    fun deleteTask(position: Int) {

        boardDetails.taskList.removeAt(position)

        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        //Update the board in database after the new changes
        Firestore().addUpdateTaskList(this, boardDetails)

    }

    fun createTaskList(taskName: String) {

        val task = Task(taskName, Firestore().getUserUID())

        Log.e(TAG, "Board from db $boardDetails")

        boardDetails.taskList.add(0, task)
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        showProgressDialog()
        Firestore().addUpdateTaskList(this, boardDetails)
    }
}
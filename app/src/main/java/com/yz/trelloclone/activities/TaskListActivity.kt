package com.yz.trelloclone.activities


import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Get board id from main and pars it and get its board from database
        getBoardId()
    }

    private fun setupToolbar(title: String){
        setSupportActionBar(binding?.taskListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.taskListToolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        binding?.taskListToolbar?.title = title
        binding?.taskListToolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    private fun getBoardId(){
        showProgressDialog()
        val boardId: String
        if (intent.hasExtra(DOCUMENT_ID)){
            boardId = intent.getStringExtra(DOCUMENT_ID).toString()

            Firestore().getBoardDetails(this, boardId)
        }
    }

    fun getBoardFromDB(board: Board){
        setupToolbar(board.name)

        val task1 = Task("Task 1")
        board.taskList.add(task1)

        binding?.rvTaskList?.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL, false)
        binding?.rvTaskList?.setHasFixedSize(false)

        val adapter = TaskListAdapter()
        adapter.setData(board.taskList)
        binding?.rvTaskList?.adapter = adapter

        hideProgressDialog()
    }
}
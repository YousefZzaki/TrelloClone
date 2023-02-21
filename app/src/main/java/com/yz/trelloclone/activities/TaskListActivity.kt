package com.yz.trelloclone.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Constants.BOARD_DETAILS
import com.yz.trelloclone.Utils.Constants.DOCUMENT_ID
import com.yz.trelloclone.adapters.TaskListAdapter
import com.yz.trelloclone.databinding.ActivityTaskListBinding
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.Board
import com.yz.trelloclone.models.Card
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
        binding?.taskListToolbar?.setNavigationIcon(R.drawable.ic_arrow_back_white)
        binding?.taskListToolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.taskListToolbar?.title = boardDetails.name
        binding?.taskListToolbar?.setTitleTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
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

        //Initial task to making first task appear
        val initTask = Task("Initial task")
        board.taskList.add(initTask)

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

    fun addCard(position: Int, cardName: String){

        val assignedTo = ArrayList<String>()
        assignedTo.add(getCurrentUserId())

        val card = Card(cardName, getCurrentUserId(), assignedTo)
        boardDetails.taskList[position].taskCards.add(card)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.members_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.members_menu -> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(BOARD_DETAILS, boardDetails)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
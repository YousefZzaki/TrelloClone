package com.yz.trelloclone.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Constants
import com.yz.trelloclone.Utils.Constants.BOARD_DETAILS
import com.yz.trelloclone.Utils.Constants.BOARD_MEMBERS_LIST
import com.yz.trelloclone.Utils.Constants.CARD_POSITION
import com.yz.trelloclone.Utils.Constants.DOCUMENT_ID
import com.yz.trelloclone.Utils.Constants.TASK_POSITION
import com.yz.trelloclone.adapters.TaskListAdapter
import com.yz.trelloclone.databinding.ActivityTaskListBinding
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.Board
import com.yz.trelloclone.models.Card
import com.yz.trelloclone.models.Task
import com.yz.trelloclone.models.User

class TaskListActivity : BaseActivity() {

    private var binding: ActivityTaskListBinding? = null
    private lateinit var boardDetails: Board
    lateinit var boardAssignedMembersDetails: ArrayList<User>

    private val membersActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                showProgressDialog()
                Firestore().getBoardDetails(this, boardDetails.documentId)
                Log.e(TAG, "Changes made")
            } else
                Log.e(TAG, "No changes made")
        }

    private val cardDetailsLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                showProgressDialog()
                Firestore().getBoardDetails(this, boardDetails.documentId)
                Log.e(TAG, "Changes made")
            } else
                Log.e(TAG, "No changes made")
        }

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

    fun getMembersDetails(membersList: ArrayList<User>){

        boardAssignedMembersDetails = membersList

        hideProgressDialog()


        //Initial task to making first task appear
        val initTask = Task("Initial task")
        boardDetails.taskList.add(initTask)

        binding?.rvTaskList?.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL, false
            )

        binding?.rvTaskList?.setHasFixedSize(false)

        val adapter = TaskListAdapter(this)
        adapter.setData(boardDetails.taskList)
        binding?.rvTaskList?.adapter = adapter

        hideProgressDialog()
    }

    private fun getBoardId() {
        showProgressDialog()
        val boardId: String
        if (intent.hasExtra(DOCUMENT_ID)) {
            boardId = intent.getStringExtra(DOCUMENT_ID).toString()

            Firestore().getBoardDetails(this, boardId)
        }

    }

    fun setCardDetails(taskPosition: Int, cardPosition: Int) {
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(BOARD_DETAILS, boardDetails)
        intent.putExtra(TASK_POSITION, taskPosition)
        intent.putExtra(CARD_POSITION, cardPosition)
        intent.putExtra(BOARD_MEMBERS_LIST, boardAssignedMembersDetails)
        cardDetailsLauncher.launch(intent)
    }

    fun getBoardFromDB(board: Board) {

        hideProgressDialog()

        boardDetails = board

        Log.e(TAG, "Board from db $boardDetails")

        setupToolbar()

        //Get board assigned members
        showProgressDialog()
        Firestore().getAssignedUsers(this, boardDetails.assignedTo)
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

    fun addCard(position: Int, cardName: String) {

        val assignedTo = ArrayList<String>()
        assignedTo.add(getCurrentUserId())

        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

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

    fun updateCardsInTaskList(taskPosition: Int, cards: ArrayList<Card>){
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)
        boardDetails.taskList[taskPosition].taskCards = cards

        showProgressDialog()
        Firestore().addUpdateTaskList(this, boardDetails)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.members_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.members_menu -> {
                val intent = Intent(this, MembersActivity::class.java)
                Log.e(TAG, "Task list board : $boardDetails")
                intent.putExtra(BOARD_DETAILS, boardDetails)
                membersActivityLauncher.launch(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
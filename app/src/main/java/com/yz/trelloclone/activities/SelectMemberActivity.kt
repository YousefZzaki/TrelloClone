package com.yz.trelloclone.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Constants
import com.yz.trelloclone.Utils.Constants.BOARD_DETAILS
import com.yz.trelloclone.Utils.Constants.BOARD_MEMBERS_LIST
import com.yz.trelloclone.adapters.MembersAdapter
import com.yz.trelloclone.databinding.ActivitySelectMemberBinding
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.Board
import com.yz.trelloclone.models.User

class SelectMemberActivity : BaseActivity() {

    private var binding: ActivitySelectMemberBinding? = null
    private lateinit var boardDetails: Board
    private var cardPosition: Int = 0
    private var taskPosition: Int = 0

   private lateinit var assignedMembers: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectMemberBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(BOARD_MEMBERS_LIST)) {
            assignedMembers = intent.getParcelableArrayListExtra(BOARD_MEMBERS_LIST)!!
        }
        if (intent.hasExtra(Constants.BOARD_DETAILS)) {
            boardDetails = intent.getParcelableExtra(Constants.BOARD_DETAILS)!!
        }
        if (intent.hasExtra(Constants.TASK_POSITION)) {
            taskPosition = intent.getIntExtra(Constants.TASK_POSITION, 0)
        }
        if (intent.hasExtra(Constants.CARD_POSITION)) {
            cardPosition = intent.getIntExtra(Constants.CARD_POSITION, 0)
        }

        setupToolBar()

        setupRecyclerView()
    }

     private fun setupRecyclerView() {


        val adapter = MembersAdapter(this)
        adapter.setData(assignedMembers)
        binding?.rvMembers?.adapter = adapter
        binding?.rvMembers?.layoutManager = LinearLayoutManager(this)
        adapter.setOnclickListener(object : MembersAdapter.OnClickListener {
            override fun onClick(position: Int, user: User, action: String) {
                if (action == Constants.SELECTED) {
                    Log.e(TAG, "action select")
                    if (!boardDetails.taskList[taskPosition].taskCards[cardPosition]
                            .assignedTo.contains(user.id)
                    ) {
                        boardDetails.taskList[taskPosition].taskCards[cardPosition]
                            .assignedTo.add(user.id)
                    }
                } else {
                    Log.e(TAG, "action unselect")
                    boardDetails.taskList[taskPosition].taskCards[cardPosition]
                        .assignedTo.remove(user.id)

                    for (i in assignedMembers.indices) {
                        if (assignedMembers[i].id == user.id) {
                            assignedMembers[i].isSelected = false
                        }
                    }
                }
                Firestore().addUpdateTaskList(this@SelectMemberActivity, boardDetails)

               setResult(RESULT_OK, Intent().putExtra("SELECTED_BOARD_MEMBER_LIST", assignedMembers).putExtra(
                   BOARD_DETAILS, boardDetails))
                finish()
            }
        })

        Log.e(TAG, "assigned members: $assignedMembers")

        setupSelections()
    }

    private fun setupSelections() {

        val cardAssignedMembers =
            boardDetails.taskList[taskPosition].taskCards[cardPosition].assignedTo

        Log.e(TAG, "card assigned members: $cardAssignedMembers")

        if (cardAssignedMembers.size > 0) {
            for (i in assignedMembers.indices) {
                for (cardMember in cardAssignedMembers) {
                    if (assignedMembers[i].id == cardMember) {
                        Log.e(TAG, "card member: $cardMember, board member: ${assignedMembers[i]}")
                        assignedMembers[i].isSelected = true
                    }
                }
            }
        } else {
            for (i in assignedMembers.indices) {
                assignedMembers[i].isSelected = false
            }
        }

        Firestore().addUpdateTaskList(this@SelectMemberActivity, boardDetails)
    }

    fun onAddUpdateTaskList() {

    }

    private fun setupToolBar() {
        setSupportActionBar(binding?.tbSelectMember)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Select member"
        binding?.tbSelectMember?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        binding?.tbSelectMember?.setNavigationIcon(R.drawable.ic_arrow_back_white)
        binding?.tbSelectMember?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}

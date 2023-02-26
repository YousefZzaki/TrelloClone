package com.yz.trelloclone.activities

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Constants
import com.yz.trelloclone.Utils.Constants.BOARD_MEMBERS_LIST
import com.yz.trelloclone.adapters.MembersAdapter
import com.yz.trelloclone.databinding.ActivitySelectMemberBinding
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

        setupRecyclerView(assignedMembers)
    }

    private fun setupRecyclerView(members: ArrayList<User>) {

        assignedMembers = members

        val adapter = MembersAdapter(this)
        adapter.setData(members)
        binding?.rvMembers?.adapter = adapter
        binding?.rvMembers?.layoutManager = LinearLayoutManager(this)
        adapter.setOnclickListener(object : MembersAdapter.OnClickListener {
            override fun onClick(position: Int, user: User, action: String) {
                TODO("Not yet implemented")
            }

        })

        setupSelections()
    }

    private fun setupSelections() {
        val cardAssignedMembers =
            boardDetails.taskList[taskPosition].taskCards[cardPosition].assignedTo

        if (cardAssignedMembers.size > 0 ){
            for (boardMember in assignedMembers){
                for (cardMember in cardAssignedMembers){
                    if (boardMember.id == cardMember){
                        boardMember.isSelected = true
                    }
                }
            }
        }
        else{
            for (boardMember in assignedMembers){
                boardMember.isSelected = false
            }
        }
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

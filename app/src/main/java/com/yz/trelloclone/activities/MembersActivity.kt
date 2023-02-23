package com.yz.trelloclone.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Constants.BOARD_DETAILS
import com.yz.trelloclone.adapters.MembersAdapter
import com.yz.trelloclone.databinding.ActivityMembersBinding
import com.yz.trelloclone.databinding.AddMemberDialogBinding
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.Board
import com.yz.trelloclone.models.User

class MembersActivity : BaseActivity() {

    private var binding: ActivityMembersBinding? = null

    private lateinit var boardDetails: Board

    private lateinit var assignedMembers: ArrayList<User>

    private var anyChangedMade = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupToolBar()

        //Getting board details from main
        getBoard()
    }

    fun memberDetails(user: User){
        boardDetails.assignedTo.add(user.id)
        Firestore().assignMemberToBoard(this, boardDetails, user)
    }

    fun onAssignMemberSuccess(user: User){

        anyChangedMade = true
        setResult(RESULT_OK)
        hideProgressDialog()
        assignedMembers.add(user)
        setupRecyclerView(assignedMembers)
    }

    private fun getBoard() {

        if (intent.hasExtra(BOARD_DETAILS)) {
            boardDetails = intent.getParcelableExtra(BOARD_DETAILS)!!
            Firestore().getAssignedUsers(this, boardDetails.assignedTo)
        }
    }

    fun setupRecyclerView(members: ArrayList<User>) {

        assignedMembers = members

        val adapter = MembersAdapter(this)
        adapter.setData(members)
        binding?.rvMembers?.adapter = adapter
        binding?.rvMembers?.layoutManager = LinearLayoutManager(this)
        hideProgressDialog()

    }

    private fun setupToolBar() {
        setSupportActionBar(binding?.tbMembers)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.members)
        binding?.tbMembers?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        binding?.tbMembers?.setNavigationIcon(R.drawable.ic_arrow_back_white)
        binding?.tbMembers?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun showAddMemberDialog() {

        val dialogBinding: AddMemberDialogBinding = AddMemberDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(this, R.style.Theme_Dialog)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialogBinding.tvAdd.setOnClickListener {
            val email = dialogBinding.etEmail.text.toString()
            if (email.isNotEmpty()) {
                //TODO add email to members
                showProgressDialog()
                Firestore().getMemberDetails(this, email)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Enter email address", Toast.LENGTH_LONG).show()
            }
        }


        dialogBinding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_member_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_member -> {
                showAddMemberDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
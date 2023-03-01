package com.yz.trelloclone.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Constants.BOARD_DETAILS
import com.yz.trelloclone.Utils.Constants.BOARD_MEMBERS_LIST
import com.yz.trelloclone.Utils.Constants.CARD_POSITION
import com.yz.trelloclone.Utils.Constants.TASK_POSITION
import com.yz.trelloclone.Utils.Constants.getColorList
import com.yz.trelloclone.adapters.CardMemberListItemAdapter
import com.yz.trelloclone.databinding.ActivityCardDetailsBinding
import com.yz.trelloclone.databinding.DeleteDialogBinding
import com.yz.trelloclone.dialogs.LabelColorListDialog
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.Board
import com.yz.trelloclone.models.Card
import com.yz.trelloclone.models.SelectedMembers
import com.yz.trelloclone.models.User
import java.text.SimpleDateFormat
import java.util.*

class CardDetailsActivity : BaseActivity() {

    private var binding: ActivityCardDetailsBinding? = null
    private lateinit var boardDetails: Board
    private var cardPosition: Int = 0
    private var taskPosition: Int = 0
    private lateinit var cardName: String
    private var selectedColor = ""
    private lateinit var boardAssignedMembersDetails: ArrayList<User>
    private var selectedDataInMillis: Long = 0

    //Calender object
    private var cal = Calendar.getInstance()

    //DateSetListener
    private var dateSetListener: DatePickerDialog.OnDateSetListener? = null

    private val selectedMembersLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (result.data!!.hasExtra(BOARD_DETAILS)) {
                    boardDetails = result.data?.getParcelableExtra(BOARD_DETAILS)!!
                    Log.e(TAG, "has extra BOARD_DETAILS")
                }
                if (result.data!!.hasExtra("SELECTED_BOARD_MEMBER_LIST")) {
                    boardAssignedMembersDetails =
                        result.data?.getParcelableArrayListExtra("SELECTED_BOARD_MEMBER_LIST")!!
                    Log.e(TAG, "has extra SELECTED_BOARD_MEMBER_LIST")
                }
                boardAssignedMembersDetails =
                    result.data?.getParcelableArrayListExtra("SELECTED_BOARD_MEMBER_LIST")!!
                setupSelectedMemberList(boardAssignedMembersDetails)
                Log.e(TAG, "Selected member updated")
                Log.e(TAG, "boardAssignedMembersDetails : $boardAssignedMembersDetails")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)


        getCardDetails()

        setupToolbar()

        setCardDetails()

        binding?.btnUpdate?.setOnClickListener {
            if (binding?.etName?.text?.isNotEmpty()!!)
                updateCardDetails()
            else
                Toast.makeText(baseContext, "Enter card name", Toast.LENGTH_LONG).show()
        }

        binding?.btnSelectColor?.setOnClickListener {
            colorListDialog()
        }

        selectedColor = boardDetails.taskList[taskPosition].taskCards[cardPosition].cardColor

        if (selectedColor.isNotEmpty()) {
            setColor()
        }

        binding?.btnSelectMember?.setOnClickListener {
            showSelectMemberActivity()
        }

        setupSelectedMemberList(boardAssignedMembersDetails)

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
        }

        selectedDataInMillis = boardDetails.taskList[taskPosition].taskCards[cardPosition].dueDate

        if (selectedDataInMillis > 0) {
            val myFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            val date = sdf.format(Date(selectedDataInMillis))
            binding?.btnSelectDate?.text = date
        }

        binding?.btnSelectDate?.setOnClickListener {
            showDatePicker()
        }

    }

    private fun updateDate() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        val date = sdf.format(cal.time)
        binding?.btnSelectDate?.text = date
        selectedDataInMillis = cal.timeInMillis
    }

    private fun showSelectMemberActivity() {
        val intent = Intent(this, SelectMemberActivity::class.java)
        intent.putExtra(BOARD_MEMBERS_LIST, boardAssignedMembersDetails)
        intent.putExtra(BOARD_DETAILS, boardDetails)
        intent.putExtra(TASK_POSITION, taskPosition)
        intent.putExtra(CARD_POSITION, cardPosition)
        selectedMembersLauncher.launch(intent)
    }

    private fun getCardDetails() {
        if (intent.hasExtra(BOARD_DETAILS)) {
            boardDetails = intent.getParcelableExtra(BOARD_DETAILS)!!
        }
        if (intent.hasExtra(TASK_POSITION)) {
            taskPosition = intent.getIntExtra(TASK_POSITION, 0)
        }
        if (intent.hasExtra(CARD_POSITION)) {
            cardPosition = intent.getIntExtra(CARD_POSITION, 0)
        }
        if (intent.hasExtra(BOARD_MEMBERS_LIST)) {
            boardAssignedMembersDetails = intent.getParcelableArrayListExtra(BOARD_MEMBERS_LIST)!!
        }

        cardName = boardDetails.taskList[taskPosition].taskCards[cardPosition].cardTitle

    }

    private fun setCardDetails() {
        binding?.etName?.setText(cardName)
        binding?.etName?.setSelection(cardName.length)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding?.tbCardDetails)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = boardDetails.taskList[taskPosition].taskCards[cardPosition].cardTitle
        binding?.tbCardDetails?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        binding?.tbCardDetails?.setNavigationIcon(R.drawable.ic_arrow_back_white)
        binding?.tbCardDetails?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun onAddUpdateTaskList() {
        hideProgressDialog()

        setResult(RESULT_OK)

        finish()
    }

    private fun updateCardDetails() {
        val card = Card(
            binding?.etName?.text?.toString()!!,
            boardDetails.taskList[taskPosition].taskCards[cardPosition].createdBy,
            boardDetails.taskList[taskPosition].taskCards[cardPosition].assignedTo,
            selectedColor,
            selectedDataInMillis
        )

        boardDetails.taskList[taskPosition].taskCards[cardPosition] = card

        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)


        showProgressDialog()

        Firestore().addUpdateTaskList(this, boardDetails)
    }

    private fun deleteCard() {

        boardDetails.taskList[taskPosition].taskCards.removeAt(cardPosition)

        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)


        showProgressDialog()

        Firestore().addUpdateTaskList(this, boardDetails)

    }

    private fun showDeleteAlertDialog() {

        val dialogLayout = DeleteDialogBinding.inflate(LayoutInflater.from(this))

        val builder = AlertDialog.Builder(this, R.style.Theme_Dialog)
        builder.setView(dialogLayout.root)

        val dialog = builder.create()

        dialogLayout.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogLayout.btnDelete.setOnClickListener {
            deleteCard()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setColor() {
        binding?.btnSelectColor?.text = ""
        binding?.btnSelectColor?.setBackgroundColor(Color.parseColor(selectedColor))
    }

    private fun colorListDialog() {
        val colorList = getColorList()

        val colorListDialog = object : LabelColorListDialog(this, colorList, selectedColor) {
            override fun onItemSelected(color: String) {
                selectedColor = color
                setColor()
            }
        }

        colorListDialog.show()
    }

    private fun setupSelectedMemberList(users: ArrayList<User>) {
        // Here we get the updated assigned members list
        // Assigned members of the Card.

        Log.e(TAG, "boardAssignedMembersDetails : $boardAssignedMembersDetails")

        boardAssignedMembersDetails = users

        val cardAssignedMembersList =
            boardDetails.taskList[taskPosition].taskCards[cardPosition].assignedTo

        // A instance of selected members list.
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        // Here we got the detail list of members and add it to the selected members list as required.
        for (i in boardAssignedMembersDetails.indices) {
            for (j in cardAssignedMembersList) {
                if (boardAssignedMembersDetails[i].id == j) {
                    val selectedMember = SelectedMembers(
                        boardAssignedMembersDetails[i].id,
                        boardAssignedMembersDetails[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if (selectedMembersList.size > 0) {

            selectedMembersList.add(SelectedMembers("", ""))

            binding?.btnSelectMember?.visibility = View.GONE
            binding?.rvSelectedMembers?.visibility = View.VISIBLE

            binding?.rvSelectedMembers?.layoutManager = GridLayoutManager(this, 6)

            val adapter = CardMemberListItemAdapter(this, true)
            adapter.setData(selectedMembersList)

            binding?.rvSelectedMembers?.adapter = adapter

            adapter.setOnClickLister(object : CardMemberListItemAdapter.OnClickListener {
                override fun onClick() {
                    showSelectMemberActivity()
                }
            })

        } else {
            binding?.rvSelectedMembers?.visibility = View.GONE
            binding?.btnSelectMember?.visibility = View.VISIBLE
        }
        Log.e(TAG, "card details assigned members: $boardAssignedMembersDetails")
        Log.e(TAG, "card details selected members: $selectedMembersList")
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_card_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_card -> {
                showDeleteAlertDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}
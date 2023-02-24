package com.yz.trelloclone.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Constants.BOARD_DETAILS
import com.yz.trelloclone.Utils.Constants.CARD_POSITION
import com.yz.trelloclone.Utils.Constants.TASK_POSITION
import com.yz.trelloclone.databinding.ActivityCardDetailsBinding
import com.yz.trelloclone.databinding.DeleteDialogBinding
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.Board
import com.yz.trelloclone.models.Card

class CardDetailsActivity : BaseActivity() {
    private var binding: ActivityCardDetailsBinding? = null
    private lateinit var boardDetails: Board
    private var cardPosition: Int = 0
    private var taskPosition: Int = 0

    private lateinit var cardName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        getCardDetails()

        setupToolbar()

        setCardDetails()

        binding?.btnUpdate?.setOnClickListener {
            updateCardDetails()
        }

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
            boardDetails.taskList[taskPosition].taskCards[cardPosition].assignedTo
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


}
package com.yz.trelloclone.activities

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Api
import com.yz.trelloclone.Utils.Constants.BOARD_DETAILS
import com.yz.trelloclone.Utils.Constants.FCM_AUTHORIZATION
import com.yz.trelloclone.Utils.Constants.FCM_BASE_URL
import com.yz.trelloclone.Utils.Constants.FCM_KEY_DATA
import com.yz.trelloclone.Utils.Constants.FCM_KEY
import com.yz.trelloclone.Utils.Constants.FCM_KEY_MESSAGE
import com.yz.trelloclone.Utils.Constants.FCM_KEY_TITLE
import com.yz.trelloclone.Utils.Constants.FCM_KEY_TO
import com.yz.trelloclone.adapters.MembersAdapter
import com.yz.trelloclone.databinding.ActivityMembersBinding
import com.yz.trelloclone.databinding.AddMemberDialogBinding
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.Board
import com.yz.trelloclone.models.User
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

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

    fun memberDetails(user: User) {
        boardDetails.assignedTo.add(user.id)
        Firestore().assignMemberToBoard(this, boardDetails, user)
    }

    fun onAssignMemberSuccess(user: User) {


        anyChangedMade = true
        setResult(RESULT_OK)
        hideProgressDialog()
        assignedMembers.add(user)
        setupRecyclerView(assignedMembers)

        SendNotificationToUserAsyncTask(boardDetails.name, user.fcmToken).execute()
    }

    private fun getBoard() {

        if (intent.hasExtra(BOARD_DETAILS)) {
            showProgressDialog()
            boardDetails = intent.getParcelableExtra(BOARD_DETAILS)!!
            Firestore().getAssignedUsers(this, boardDetails.assignedTo)
        }
    }

    private suspend fun getBitmap(imageUrl: String): Bitmap{

        val loading = ImageLoader(applicationContext)
        val imageRequest = ImageRequest.Builder(applicationContext)
            .data(imageUrl)
            .build()

        val result = (loading.execute(imageRequest) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
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

    private inner class SendNotificationToUserAsyncTask(val boardName: String, val token: String) :
        AsyncTask<Any, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
            Log.e(TAG, "onPreExecute show")
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgressDialog()
            Log.e(TAG, "onPreExecute hide")
        }

        override fun doInBackground(vararg p0: Any?): String {
            var result: String = ""

            var connection: HttpURLConnection? = null
            try {

                val url = URL(FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection

                connection.doInput = true
                connection.doOutput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"
                connection.useCaches = false

                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                connection.setRequestProperty(FCM_AUTHORIZATION, "${FCM_KEY}=${Api.FCM_SERVER_KEY}")

                val writer = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()

                dataObject.put(FCM_KEY_TITLE, "Assigned to $boardName board")
                dataObject.put(FCM_KEY_MESSAGE, "You have been assigned by ${boardDetails.createdBy}")

                jsonRequest.put(FCM_KEY_DATA, dataObject)
                jsonRequest.put(FCM_KEY_TO, token)


                writer.writeBytes(jsonRequest.toString())
                writer.flush()
                writer.close()

                val httpResult = connection.responseCode

                if (httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))

                    val builder = StringBuilder()
                    var line: String?

                    try {
                        while (reader.readLine().also{line = it } != null){
                         builder.append(line + "\n")
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                        Log.e(TAG, e.toString())
                    }finally {
                        try {
                            inputStream.close()
                        }catch (e: IOException){
                            e.printStackTrace()
                            Log.e(TAG, e.toString())
                        }
                    }

                    result = builder.toString()

                }else{
                    result = connection.responseMessage
                }

            }catch (e: IOException){
                e.printStackTrace()
                Log.e(TAG, e.toString())
            }
            catch (e: SocketTimeoutException){
                e.printStackTrace()
                Log.e(TAG, e.toString())
                result = "Timeout"
            }
            finally {
                connection?.disconnect()
            }
            return result
        }
    }
}
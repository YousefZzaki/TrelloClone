package com.yz.trelloclone.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.yz.trelloclone.Utils.Constants.ASSIGNED_TO
import com.yz.trelloclone.Utils.Constants.BOARDS
import com.yz.trelloclone.Utils.Constants.USERS
import com.yz.trelloclone.activities.*
import com.yz.trelloclone.activities.BaseActivity.Companion.TAG
import com.yz.trelloclone.models.Board
import com.yz.trelloclone.models.User

class Firestore: BaseActivity(){

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        //Create new collection in the database
        mFirestore.collection(USERS)
            .document(getUserUID()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.onRegisterSuccess()
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board){
        //Create new collection in the database
        mFirestore.collection(BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(TAG, "Board created successfully")
                Toast.makeText(activity, "Board created successfully", Toast.LENGTH_LONG).show()
                activity.onSuccessfullyBoardCreated()
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(TAG, it.message.toString())
                Toast.makeText(activity, it.message.toString(), Toast.LENGTH_LONG).show()

            }
    }

    fun getBoardList(activity: MainActivity){
        mFirestore.collection(BOARDS)
            .whereArrayContains(ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener { snapshot ->
                Log.e(TAG, "Boards : ${snapshot.documents}")
                val boardList: ArrayList<Board> = ArrayList()
                for (item in snapshot.documents){
                    val board = item.toObject(Board::class.java)!!
                    board.documentId = item.id
                    Log.e(TAG, "doc: ${item.id}")
                    Log.e(TAG, "doc: ${board.documentId}")
                    boardList.add(board)
                }
                activity.addBoardsToUI(boardList)
                Log.e(TAG, "get board${boardList[0]}")
            }.addOnFailureListener{
                activity.hideProgressDialog()
                Log.e(TAG, it.message.toString())
            }
    }

    fun getBoardDetails(activity: TaskListActivity, boardId: String){
        mFirestore.collection(BOARDS)
            .document(boardId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(TAG, "board details: ${document.toObject(Board::class.java)!!}")
                activity.getBoardFromDB(document.toObject(Board::class.java)!!)
            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
            }
    }

    fun signInUser(activity: Activity) {
        mFirestore.collection(USERS)
            .document(getUserUID()).get().addOnSuccessListener { document ->
                val loggedUser = document.toObject(User::class.java)!!
                    when (activity) {
                        is SignInActivity -> {
                            activity.onSigInSuccess(loggedUser)
                            Log.e(TAG, "Successfully signingIn")
                        }
                        is MainActivity -> {
                            activity.displayUserDataInDrawer(loggedUser)
                        }

                    }

            }.addOnFailureListener {
                BaseActivity().hideProgressDialog()
                Log.e(TAG, "Signing in fail")
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFirestore.collection(USERS)
            .document(getUserUID())
            .update(userHashMap)
            .addOnSuccessListener {
                activity.onUpdateSuccess()
                Log.i(TAG, "Data updated successfully")
                Toast.makeText(
                    activity.applicationContext,
                    "Data updated successfully",
                    Toast.LENGTH_LONG
                ).show()

            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(TAG, it.message.toString())
                Toast.makeText(
                    activity.applicationContext,
                    it.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    fun getUserUID(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUID = ""

        if (currentUser != null)
            currentUID = FirebaseAuth.getInstance().currentUser!!.uid

        return currentUID
    }

    fun getUserFromDB(activity: MyProfileActivity) {
        mFirestore.collection(USERS)
            .document(getUserUID()).get().addOnSuccessListener { document ->
                document.toObject(User::class.java)!!.let { user ->
                    activity.setUserData(user)
                }
            }
    }
}
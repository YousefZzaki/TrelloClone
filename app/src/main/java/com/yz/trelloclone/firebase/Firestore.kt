package com.yz.trelloclone.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.yz.trelloclone.Utils.Constants.ASSIGNED_TO
import com.yz.trelloclone.Utils.Constants.BOARDS
import com.yz.trelloclone.Utils.Constants.EMAIL
import com.yz.trelloclone.Utils.Constants.ID
import com.yz.trelloclone.Utils.Constants.TASK_LIST
import com.yz.trelloclone.Utils.Constants.USERS
import com.yz.trelloclone.activities.*
import com.yz.trelloclone.models.Board
import com.yz.trelloclone.models.User

class Firestore : BaseActivity() {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        //Create new collection in the database
        mFirestore.collection(USERS)
            .document(getUserUID()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.onRegisterSuccess()
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board) {
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

    fun getBoardList(activity: MainActivity) {
        mFirestore.collection(BOARDS)
            .whereArrayContains(ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener { snapshot ->
                Log.e(TAG, "Boards : ${snapshot.documents}")
                val boardList: ArrayList<Board> = ArrayList()
                for (item in snapshot.documents) {
                    val board = item.toObject(Board::class.java)!!
                    board.documentId = item.id
                    Log.e(TAG, "doc: ${item.id}")
                    Log.e(TAG, "doc: ${board.documentId}")
                    boardList.add(board)
                }
                activity.addBoardsToUI(boardList)

            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(TAG, it.message.toString())
            }
    }

    fun getBoardDetails(activity: TaskListActivity, boardId: String) {
        mFirestore.collection(BOARDS)
            .document(boardId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(TAG, "board details: ${document.toObject(Board::class.java)!!}")
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.getBoardFromDB(board)
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
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(TAG, it.message.toString())
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

    fun addUpdateTaskList(activity: Activity, board: Board) {

        val hashMap = HashMap<String, Any>()
        hashMap[TASK_LIST] = board.taskList

        mFirestore.collection(BOARDS)
            .document(board.documentId)
            .update(hashMap)
            .addOnSuccessListener {
                when (activity) {
                    is TaskListActivity -> {
                        Log.e(TAG, "Task added/updated successfully")
                        activity.onAddUpdateTaskList()
                    }
                    is CardDetailsActivity -> {
                        activity.onAddUpdateTaskList()
                    }
                }

            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to add task: $it")
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

    fun getAssignedUsers(activity: Activity, assignedTo: ArrayList<String>) {
        mFirestore.collection(USERS)
            .whereIn(ID, assignedTo)
            .get().addOnSuccessListener { result ->
                Log.e(TAG, "Assigned users: ${result.documents}")
                val users = ArrayList<User>()
                for (i in result.documents) {
                    val user = i.toObject(User::class.java)!!
                    users.add(user)
                }
                Log.e(TAG, "users $users")
                if (activity is MembersActivity)
                    activity.setupRecyclerView(users)
                else if (activity is TaskListActivity)
                    activity.getMembersDetails(users)
            }.addOnFailureListener {
                if (activity is MembersActivity)
                    activity.hideProgressDialog()
                else if (activity is TaskListActivity)
                    activity.hideProgressDialog()
                Log.e(TAG, "Failed to get members ${it.message}")
            }

    }

    fun getMemberDetails(activity: MembersActivity, email: String) {
        mFirestore.collection(USERS)
            .whereEqualTo(EMAIL, email)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val user = result.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                } else {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such user found")
                }
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(TAG, it.message.toString())
            }
    }

    fun assignMemberToBoard(
        activity: MembersActivity,
        board: Board,
        user: User
    ) {
        val assignedMemberHashMap = HashMap<String, Any>()
        assignedMemberHashMap[ASSIGNED_TO] = board.assignedTo
        mFirestore.collection(BOARDS)
            .document(board.documentId)
            .update(assignedMemberHashMap)
            .addOnSuccessListener {
                activity.onAssignMemberSuccess(user)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(TAG, it.message.toString())
            }
    }
}
package com.yz.trelloclone.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.yz.trelloclone.Utils.Constants.USERS
import com.yz.trelloclone.activities.*
import com.yz.trelloclone.activities.BaseActivity.Companion.TAG
import com.yz.trelloclone.models.User

class Firestore {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        //Create new collection for the database
        mFirestore.collection(USERS)
            .document(getUserUID()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.onRegisterSuccess()
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
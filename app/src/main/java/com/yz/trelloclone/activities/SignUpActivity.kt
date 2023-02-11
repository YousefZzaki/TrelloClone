package com.yz.trelloclone.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.yz.trelloclone.R
import com.yz.trelloclone.databinding.ActivitySignUpBinding
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.User

class SignUpActivity : BaseActivity() {

    private var binding: ActivitySignUpBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpToolbar()

        auth = FirebaseAuth.getInstance()

        binding?.btnSignUp?.setOnClickListener {
            registerUser()
        }
    }

    private fun setUpToolbar(){
        setSupportActionBar(binding?.signUpToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.signUpToolBar?.setNavigationIcon(R.drawable.ic_arrow_back)
        binding?.signUpToolBar?.setNavigationOnClickListener {
            super.onBackPressed()
        }
    }

    private fun registerUser(){
        val name = binding?.etName?.text.toString().trim()
        Log.e(TAG, name)
        val email = binding?.etEmail?.text.toString().trim()
        Log.e(TAG, email)
        val password = binding?.etPassword?.text.toString()
        Log.e(TAG, password)

        if (validateUserData(name, email, password)){
            showProgressDialog(resources.getString(R.string.registering_user))
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                task ->
                if (task.isSuccessful){
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(firebaseUser.uid, name, registeredEmail)
                    Firestore().registerUser(this, user)
                }
                else{
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
//                    showErrorSnackBar(task.exception!!.message!!)
                }
            }
        }
    }

    private fun validateUserData(name: String, email: String, password: String ): Boolean{
        if (name.isEmpty() && email.isEmpty() && password.isEmpty()){
            showErrorSnackBar("Please enter all fields")
            return false
        }

        return when{
            name.isEmpty() -> {
                showErrorSnackBar("Please enter name")
                false
            }
            email.isEmpty() -> {
                showErrorSnackBar("Please enter email")
                false
            }
            password.isEmpty() -> {
                showErrorSnackBar("Please enter password")
                false
            }

            else -> true
        }
    }


    fun onRegisterSuccess(){
        hideProgressDialog()
        auth.signOut()
        finish()
        Toast.makeText(this, "Successfully registering", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
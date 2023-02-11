package com.yz.trelloclone.activities


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.yz.trelloclone.R
import com.yz.trelloclone.databinding.ActivitySignInBinding
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.User

class SignInActivity : BaseActivity() {

    private var binding: ActivitySignInBinding? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpActionBar()

        auth = FirebaseAuth.getInstance()

        binding?.btnSignIn?.setOnClickListener {
            signInUser()
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding?.signIpToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.signIpToolBar?.setNavigationIcon(R.drawable.ic_arrow_back)
        binding?.signIpToolBar?.setNavigationOnClickListener {
            super.onBackPressed()
        }
    }

    private fun signInUser(){
        val email = binding?.etEmail?.text.toString().trim { it <= ' ' }
        val password = binding?.etPassword?.text.toString()

        if (validateUserData(email, password)){
            showProgressDialog(resources.getString(R.string.signing_in))
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                Toast.makeText(this, "Successfully registering", Toast.LENGTH_SHORT).show()
                Firestore().signInUser(this)
            }
        }
    }

    private fun validateUserData(email: String, password: String): Boolean{
        if (email.isEmpty() && password.isEmpty()){
            showErrorSnackBar("Please enter all fields")
            return false
        }

        return when{
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

    fun onSigInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        doubleClickToExit()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}
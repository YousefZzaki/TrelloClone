package com.yz.trelloclone.activities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.yz.trelloclone.R
import com.yz.trelloclone.databinding.CustomPrgressBarBinding

open class BaseActivity : AppCompatActivity() {

    companion object {
        val TAG = "TAG"
        var selectedProfileImageFileUri: Uri? = null
    }

    private lateinit var progressBar: Dialog
    private var doubleClickPressedOne = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

     fun showProgressDialog(text: String = resources
         .getString(R.string.please_wait)) {

         val binding: CustomPrgressBarBinding =
             CustomPrgressBarBinding.inflate(layoutInflater)

         binding.tvTxt.text = text
         progressBar = Dialog(this, R.style.Theme_Dialog)
         progressBar.apply {
             Dialog(this@BaseActivity)
             setContentView(binding.root)
             setCancelable(false)
             create()
             show()
         }
     }

    fun hideProgressDialog(){
        progressBar.dismiss()
    }

    fun doubleClickToExit(){
        if (doubleClickPressedOne){
            super.onBackPressed()
            return
        }

        doubleClickPressedOne = true
        Toast.makeText(this, R.string.double_click_to_exit, Toast.LENGTH_SHORT).show()
        Handler().postDelayed({doubleClickPressedOne = false}, 2000)
    }

    fun showErrorSnackBar(message: String) {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG).setBackgroundTint(ContextCompat.getColor(this, R.color.snackbar_error_color))
        snackBar.show()
    }

     fun getImageExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }


}
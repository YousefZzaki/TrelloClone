package com.yz.trelloclone.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yz.trelloclone.R
import com.yz.trelloclone.databinding.ActivityMyProfileBinding
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.User
import com.yz.trelloclone.Utils.Constants.IMAGE
import com.yz.trelloclone.Utils.Constants.MOBILE
import com.yz.trelloclone.Utils.Constants.NAME

class MyProfileActivity : BaseActivity() {

    private var binding: ActivityMyProfileBinding? = null
    private lateinit var userDetails: User
    private var selectedImageFileUri: Uri? = null
    private var profileImageURL: String = ""
    private var openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                selectedImageFileUri = result.data!!.data!!
                Log.e(TAG, "Image loc :" + selectedImageFileUri.toString())
                Glide
                    .with(this)
                    .load(selectedImageFileUri)
                    .centerCrop()
                    .into(findViewById(R.id.iv_user_image))
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Setup actionBar
        setUpActionBar()

        //Get user data to put it in profile fields
        Firestore().getUserFromDB(this)

        binding?.ivUserImage?.setOnClickListener {
            openGallery()
        }

        binding?.btnUpdate?.setOnClickListener {
            if (selectedImageFileUri != null) {
                uploadImageToStorage()
            } else
                updateUserProfileData()

            setResult(RESULT_OK)
        }
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            openGalleryLauncher.launch(pickIntent)
        } else {
            requestPermission()
        }

    }

    fun onUpdateSuccess() {
        hideProgressDialog()
        finish()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding?.myProfileToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.myProfileToolBar?.setNavigationIcon(R.drawable.ic_arrow_back)
        binding?.myProfileToolBar?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun requestPermission() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val pickIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialog()
                Toast.makeText(
                    this@MyProfileActivity,
                    "Permissions are denied",
                    Toast.LENGTH_LONG
                ).show()
            }
        }).onSameThread().check()
    }

    private fun showRationalDialog() {
        AlertDialog.Builder(this)
            .setMessage("To pickup photo from gallery you need to grant the permissions for that")
            .setPositiveButton("Ok") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val settingsUri = Uri.fromParts("package", packageName, null)
                Log.e(TAG, settingsUri.toString())
                settingsIntent.data = settingsUri
                startActivity(settingsIntent)
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    private fun uploadImageToStorage() {

        showProgressDialog()

        Log.e(TAG, selectedImageFileUri.toString())

        if (selectedImageFileUri != null) {
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference
                .child(
                    "USER_IMAGE_" + System.currentTimeMillis() +
                            "." + getImageExtension(selectedImageFileUri)
                )

            storageReference.putFile(selectedImageFileUri!!).addOnSuccessListener { snapshot ->
                Log.e(TAG, "Image uri" + snapshot?.metadata?.reference?.downloadUrl.toString())

                snapshot?.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    Log.e(TAG, "Image downloadable url $uri")
                    profileImageURL = uri.toString()
                    updateUserProfileData()
                }
                hideProgressDialog()
            }.addOnFailureListener {
                Log.e(TAG, it.message.toString())
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }

    }

    private fun getImageExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun updateUserProfileData() {

        showProgressDialog()

        val userHashMap = HashMap<String, Any>()

        var anyChangedMade = false

        if (profileImageURL.isNotEmpty() && profileImageURL != userDetails.image) {
            userHashMap[IMAGE] = profileImageURL
            anyChangedMade = true
        }

        if (binding?.etName?.text?.toString() != userDetails.name) {
            userHashMap[NAME] = binding?.etName?.text.toString()
            anyChangedMade = true
        }

        if (binding?.etMobile?.text?.toString()?.toLong() != userDetails.mobile) {
            userHashMap[MOBILE] = binding?.etMobile?.text.toString().toLong()
            Log.e(TAG, "Mob: " + binding?.etMobile?.text.toString().toLong())
            anyChangedMade = true
        }
        if (anyChangedMade)
            Firestore().updateUserProfileData(this, userHashMap)
    }

    fun setUserData(user: User) {

        userDetails = user

        val imageUri = Uri.parse(user.image)
        Log.e(TAG, imageUri.toString())
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .into(findViewById(R.id.iv_user_image))

        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if (user.mobile == 0L) {
            binding?.etMobile?.setText(user.mobile.toString())
        }
        binding?.etMobile?.setText(user.mobile.toString())

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
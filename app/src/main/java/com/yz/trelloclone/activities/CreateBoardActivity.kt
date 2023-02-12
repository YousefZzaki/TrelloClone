package com.yz.trelloclone.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Constants.NAME
import com.yz.trelloclone.databinding.ActivityCreateBoardBinding
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.Board

class CreateBoardActivity : BaseActivity() {

    private var binding: ActivityCreateBoardBinding? = null

    private var selectedImageFileUri: Uri? = null

    private var boardImageURL: String = ""

    private var galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImageFileUri = result.data?.data
                Glide.with(this)
                    .load(selectedImageFileUri)
                    .centerCrop()
                    .into(binding?.civBoard!!)
            }
        }

    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Tool bar setup
        setupToolBar()

        //Get username from main
        funGetUsername()

        binding?.civBoard?.setOnClickListener {
            val pickIntent =
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )

            galleryLauncher.launch(pickIntent)
        }

        binding?.btnCreateBoard?.setOnClickListener {
            if (selectedImageFileUri != null){
                uploadImageToStorage()
            }else{
                createBoard()
            }
        }

    }

    private fun createBoard() {
        showProgressDialog()
        val assignedTo: ArrayList<String> = ArrayList()
        assignedTo.add(getCurrentUserId())
        val board = Board(
            binding?.etBoardName?.text?.toString()!!,
            boardImageURL,
            getCurrentUserId(),
            assignedTo
        )

        Firestore().createBoard(this, board)
    }

    private fun uploadImageToStorage() {
        showProgressDialog()
        Log.e(TAG, selectedImageFileUri.toString())

        if (selectedImageFileUri != null) {
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference
                .child(
                    "BOARD_IMAGE_" + System.currentTimeMillis() +
                            "." + getImageExtension(selectedImageFileUri)
                )

            //Try to upload image to the storage
            storageReference.putFile(selectedImageFileUri!!).addOnSuccessListener { snapshot ->
                createBoard()
                val imageUri = snapshot.metadata?.reference?.downloadUrl.toString()
                Log.i(TAG, "Image uri: $imageUri")

                var imageDownloadableUrl: Uri?
                    snapshot?.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        imageDownloadableUrl = it
                    Log.i(TAG, "Image downloadable Url: $imageDownloadableUrl")
                    Toast.makeText(this, "Uploading image successfully", Toast.LENGTH_LONG).show()
                }

            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                Log.e(TAG, it.message.toString())
            }
        }
        hideProgressDialog()
    }

    fun onSuccessfullyBoardCreated() {
        hideProgressDialog()
        finish()
    }

    private fun setupToolBar() {
        setSupportActionBar(binding?.tbCreateBoard)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        binding?.tbCreateBoard?.title = resources.getString(R.string.create_board)
        binding?.tbCreateBoard?.setNavigationIcon(R.drawable.ic_arrow_back)
        binding?.tbCreateBoard?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun funGetUsername() {
        if (intent.hasExtra(NAME)) {
            userName = intent.getStringExtra(NAME).toString()
        }
    }

}
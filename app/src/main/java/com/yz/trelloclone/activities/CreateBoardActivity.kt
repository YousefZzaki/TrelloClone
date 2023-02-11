package com.yz.trelloclone.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.yz.trelloclone.R
import com.yz.trelloclone.databinding.ActivityCreateBoardBinding

class CreateBoardActivity : AppCompatActivity() {

    private var binding: ActivityCreateBoardBinding? = null

    private var selectedFileUri: Uri? = null

    private var galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                selectedFileUri = result.data?.data
                Glide.with(this)
                    .load(selectedFileUri)
                    .centerCrop()
                    .into(binding?.civBoard!!)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Tool bar setup
        setupToolBar()


        binding?.civBoard?.setOnClickListener {
            val pickIntent =
                Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            galleryLauncher.launch(pickIntent)
        }
    }


    private fun setupToolBar(){
        setSupportActionBar(binding?.tbCreateBoard)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        binding?.tbCreateBoard?.title = resources.getString(R.string.create_board)
        binding?.tbCreateBoard?.setNavigationIcon(R.drawable.ic_arrow_back)
        binding?.tbCreateBoard?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}
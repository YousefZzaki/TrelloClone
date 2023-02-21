package com.yz.trelloclone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yz.trelloclone.R
import com.yz.trelloclone.databinding.ActivityMembersBinding

class MembersActivity : AppCompatActivity() {

    private var binding : ActivityMembersBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
    }
}
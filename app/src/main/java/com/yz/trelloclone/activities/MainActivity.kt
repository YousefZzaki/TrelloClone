package com.yz.trelloclone.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Constants.NAME
import com.yz.trelloclone.databinding.ActivityMainBinding
import com.yz.trelloclone.firebase.Firestore
import com.yz.trelloclone.models.User

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding: ActivityMainBinding? = null
    private lateinit var user: User

    private var myProfileActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            //Update data and to display it in the drawer
            Firestore().signInUser(this)
            Log.e(TAG, "Data has been updating")
        }
        else
            Log.e(TAG, "Data not updated")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.navView?.setNavigationItemSelectedListener(this)

        setUpActionBar()

        setFAB()

        //Get user data to display it in drawer
        Firestore().signInUser(this)

    }

    private fun setFAB(){

        val fab = binding?.root?.findViewById<FloatingActionButton>(R.id.fab_add)

        fab?.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(NAME, user.name)
            startActivity(intent)
        }
    }

    fun displayUserDataInDrawer(user: User) {

        //Get user to use it later
        this.user = user

        //Loading data to the drawer
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .into(findViewById(R.id.iv_user_image))

        findViewById<TextView>(R.id.tv_username).text = user.name

        Log.e(TAG, user.name)

        setUserImageInToolbar()
    }

    private fun setUpActionBar() {

        val toolBar: Toolbar = findViewById(R.id.tool_bar_main)

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.setNavigationIcon(R.drawable.ic_nav_menu)
        toolBar.setNavigationOnClickListener {
            Log.e(TAG, "Navigation menu")
            toggleDrawer()
        }
    }

    private fun setUserImageInToolbar(){
        Log.e(TAG, this.user.toString())
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .into(findViewById(R.id.iv_toolbar_profile))
    }

    private fun toggleDrawer() {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START))
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        else
            binding?.drawerLayout!!.openDrawer(GravityCompat.START)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.nav_my_profile -> {
                myProfileActivityLauncher.launch(Intent(this, MyProfileActivity::class.java))
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                Log.e(TAG, "User logged out")
                Toast.makeText(this, "User logged out", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        }else{
            doubleClickToExit()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
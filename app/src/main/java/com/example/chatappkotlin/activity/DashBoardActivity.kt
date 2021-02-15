package com.example.chatappkotlin.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.chatappkotlin.R
import com.example.chatappkotlin.database.model.UserModel
import com.example.chatappkotlin.fragments.ChatFragment
import com.example.chatappkotlin.fragments.ContantFragment
import com.example.chatappkotlin.fragments.ProfileFragment
import com.example.chatmekotlin.Permission.AppPermission
import com.example.chatmekotlin.Util.AppUtil
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.orhanobut.hawk.Hawk

class DashBoardActivity : AppCompatActivity() {

    private var appPermission = AppPermission()
    var fragment: Fragment? = null
    lateinit var bottomChip: ChipNavigationBar
    var TAG: String? = null

    var userModel: UserModel? = UserModel()
    private lateinit var appUtil: AppUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        bottomChip = findViewById(R.id.bottomChip)

        appUtil = AppUtil()
        userModel = Hawk.get("CurrentUser")

        if (savedInstanceState == null) {

            supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardContainer, ChatFragment())
                .addToBackStack(null)
                .commit()

            TAG = "Chat"
            bottomChip.setItemSelected(R.id.btnChat)

            if (appPermission.isContactOk(this)) {

            } else {
                appPermission.requestContactPermission(this)
            }
        }

        bottomChip.setOnItemSelectedListener { id ->
            when (id) {
                R.id.btnChat -> {
                    fragment = ChatFragment()
                    TAG = "Chat"
                }
                R.id.btnContact -> {
                    fragment = ContantFragment()
                    TAG = "Contacts"
                }
                R.id.btnProfile -> {
                    fragment = ProfileFragment()
                    TAG = "Profile"
                }
            }

            Transaction(fragment!!)
        }

    }

    override fun onPause() {
        super.onPause()
        appUtil.updateOnlineStatus("offline")
    }

    override fun onResume() {
        super.onResume()
        if (userModel != null)
            appUtil.updateOnlineStatus("online")
    }

    private fun Transaction(fragment: Fragment) {

        val topFragment = supportFragmentManager.findFragmentById(R.id.dashboardContainer)

        if (topFragment != null) {

            supportFragmentManager.beginTransaction().remove(topFragment)

            if (supportFragmentManager.findFragmentByTag(TAG) != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.dashboardContainer, fragment!!, TAG)
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.dashboardContainer, fragment!!, TAG)
                    .addToBackStack(TAG)
                    .commit()
            }

        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardContainer, fragment!!, TAG)
                .addToBackStack(TAG)
                .commit()
        }
    }

    override fun onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {

            //Log.d("backStackEntryCount", " " + supportFragmentManager.backStackEntryCount)

            supportFragmentManager.popBackStack()

            bottomChip.visibility = View.VISIBLE

        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed()
            finish()
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 100) {

            val returnValue = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)

            hisId = clickedUser!!.uID

            val intent = Intent(this, SendMediaService::class.java)
            intent.putExtra("hisID", hisId)
            //intent.putExtra("chatID", chatId)
            intent.putStringArrayListExtra("media", returnValue)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                startForegroundService(intent)
            else
                startService(intent)
        }

    }*/

}
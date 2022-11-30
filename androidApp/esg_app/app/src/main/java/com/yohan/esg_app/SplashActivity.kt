package com.yohan.esg_app

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var mToast:Toast?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = Firebase.auth

        //start of recipe 가져오기
        var assetManager=resources.assets
        CSVReader.recipe=CSVReader.readCSV_asset(assetManager.open("recipe.csv"))
        //end of recipe 가져오기

        auth.signOut()//App에 새로 들어올 때마다 다시 로그인 해야함
        if (auth.currentUser != null) {

            Handler().postDelayed({
                Log.d("splashmymymy","메인으로 이동합니다!")
                showToast(this,"로그인된 회원입니다!")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        } else {
                Handler().postDelayed({
                    Log.d("splashmymymy","로그인이 필요합니다!")
                    showToast(this,"로그인이 필요합니다!")
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
        }

    }

    // Toast 겹치지 않고 자연스럽게 만들기 위한 코드
    override fun onStop() {
        super.onStop()
        Thread.sleep(500)
        mToast?.cancel()
    }
    fun showToast(context: Context, message:String) {

        if (mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        }
        else {
            mToast!!.setText(message);
        }
        mToast!!.show()
    }
    // End of Toast 겹치지 않고 자연스럽게 만들기 위한 코드
}

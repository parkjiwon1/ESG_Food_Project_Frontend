package com.yohan.esg_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File


class MainActivity : AppCompatActivity() {

    private var backKeyPressedTime:Long=0
    private lateinit var customProgressDialog: ProgressDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var mToast:Toast

    private val fragmentManager:FragmentManager=supportFragmentManager
    private lateinit var fragment1:Fragment1_main
    private lateinit var fragment2:Fragment3_main

    override fun onBackPressed() {

        //기존의 뒤로가기 버튼의 기능 제거
        //super.onBackPressed()

        // 2000 milliseconds= 2 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime=System.currentTimeMillis()
            //Toast.makeText(this,"\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
            showToast(this,"\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.")
            return
        }

        //2초 이내에 뒤로가기 버튼을 한번 더 클릭시 finish()(앱 종료)
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth

        mToast=Toast.makeText(this,"",Toast.LENGTH_SHORT)
        fragment1=Fragment1_main(mToast!!)
        fragment2=Fragment3_main(mToast!!)

        //수동으로 collapsingToolbarLayout에 있는 글씨 font 바꾸기
        lateinit var toolbar_layout: CollapsingToolbarLayout
        toolbar_layout = findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)

        val tf = ResourcesCompat.getFont(this, R.font.bmjua)
        toolbar_layout.setCollapsedTitleTypeface(tf);
        toolbar_layout.setExpandedTitleTypeface(tf);
        //end of 수동으로 collapsingToolbarLayout에 있는 글씨 font 바꾸기


        //상단 Toolbar 관련 이벤트 메소드
        val topAppBar = findViewById<MaterialToolbar>(R.id.toolbar)
        topAppBar.setOnClickListener {
            // Handle navigation icon press
            //Toast.makeText(this,"아직 준비중입니다!",Toast.LENGTH_SHORT).show()
            showToast(this,"사이드 메뉴입니다!")
        }

        topAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {

                R.id.home->{
                    //Toast.makeText(this,"홈으로 이동합니다.",Toast.LENGTH_SHORT).show()
                    showToast(this,"홈으로 이동합니다.")


                    finish()//인텐트 종료
                    overridePendingTransition(0,0)//인텐트 효과 없애기
                    val intent=intent//인텐트
                    startActivity(intent)//액티비티 열기
                    overridePendingTransition(0,0)//인텐트 효과 없애기

                    true
                }
                R.id.reload->{

                    //Toast.makeText(this,"새로고침!",Toast.LENGTH_SHORT).show()
                    showToast(this,"새로고침!")

                    finish()//인텐트 종료
                    overridePendingTransition(0,0)//인텐트 효과 없애기
                    val intent=intent//인텐트
                    startActivity(intent)//액티비티 열기
                    overridePendingTransition(0,0)//인텐트 효과 없애기
                    true
                }
                R.id.information->{
                    //Toast.makeText(this,"아직 준비중입니다!",Toast.LENGTH_SHORT).show()
                    //showToast(this,"아직 준비중입니다!")

                    val mDialogView= LayoutInflater.from(this).inflate(R.layout.custom_dialog,null)
                    val mBuilder= AlertDialog.Builder(this)
                        .setView(mDialogView)
                        .setTitle("서버 URL 입력창.")

                    val mAlertDialog=mBuilder.show()

                    val urlInfo=mAlertDialog.findViewById<TextView>(R.id.urlInfo)
                    urlInfo!!.setText("baseUrl: "+CSVReader.baseUrl+"\n"+"innerUrl: "+CSVReader.innerUrl)

                    val saveBtn = mAlertDialog.findViewById<Button>(R.id.saveBtn)

                    saveBtn?.setOnClickListener {
                        val ip=mAlertDialog.findViewById<EditText>(R.id.ip)
                        val port=mAlertDialog.findViewById<EditText>(R.id.port)
                        val innerUrl=mAlertDialog.findViewById<EditText>(R.id.innerUrl)

                        val url="http://"+ip!!.text.toString()+":"+port!!.text.toString()+"/"
                        CSVReader.baseUrl=url
                        CSVReader.innerUrl=innerUrl!!.text.toString()

                        Log.d("adcdefg",url)
                        Log.d("adcdefg",CSVReader.baseUrl)


                        showToast(this,"URL 설정 완료!")
                        mAlertDialog.dismiss()
                    }

                    val defaultButton=mAlertDialog.findViewById<Button>(R.id.defaultButton)
                    defaultButton!!.setOnClickListener {

                        CSVReader.baseUrl=CSVReader.defaultUrl
                        CSVReader.innerUrl=CSVReader.defaultInnerUrl

                        showToast(this,"URL 초기화 완료!")
                        mAlertDialog.dismiss()

                    }

                    true
                }
                R.id.logout->{
                    //Toast.makeText(this,"로그아웃 되었습니다!",Toast.LENGTH_SHORT).show()
                    showToast(this,"로그아웃 되었습니다!")

                    auth.signOut()
//                    val intent= Intent(this,LoginActivity::class.java)
//                    startActivity(intent)

                    Handler().postDelayed({
                        ActivityCompat.finishAffinity(this)
                        System.exit(0);
                    },500)

                    true
                }
                else-> false
            }

        }
        //end of 상단 Toolbar 관련 이벤트 메소드


        //하단 NavBar 관련 이벤트 메소드

        val transaction=fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout,fragment1).commitAllowingStateLoss()

        val bottomNavView=findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavView.setOnNavigationItemSelectedListener(ItemSelectedListener())

    //end of 하단 NavBar 관련 이벤트 메소드

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

    //하단바 fragment 전환을 위한 inner클래스.
    inner class ItemSelectedListener : BottomNavigationView.OnNavigationItemSelectedListener{
        override fun onNavigationItemSelected(item: MenuItem): Boolean {

            val transaction:FragmentTransaction= fragmentManager.beginTransaction()
            when (item.itemId) {

                R.id.page_1 -> {
                    transaction.replace(R.id.frame_layout, fragment1).commitAllowingStateLoss()
                }
                R.id.page_2 ->{
                    //Toast.makeText(this,"물체검출화면으로 이동합니다.",Toast.LENGTH_SHORT).show()
                    showToast(baseContext,"물체검출화면으로 이동합니다.")
                    val intent=Intent(baseContext,DetectActivity::class.java)
                    startActivity(intent)//액티비티 열기
                }
                R.id.page_3->{
                    transaction.replace(R.id.frame_layout,fragment2).commitAllowingStateLoss()
                }
            }
            return true;
        }
    }

}



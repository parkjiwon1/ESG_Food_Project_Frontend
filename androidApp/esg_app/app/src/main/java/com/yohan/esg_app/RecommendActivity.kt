package com.yohan.esg_app

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RecommendActivity : AppCompatActivity() {

    private var mToast:Toast?=null
    private lateinit var auth: FirebaseAuth
    private var list= mutableListOf<Recipe>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)

        Log.d("abcdef","w1")

        val tmpList=intent.getStringArrayListExtra("list")
        val ingredients=findViewById<TextView>(R.id.ingredient)
        ingredients.setText(intent.getStringExtra("ingredients"))

        for (idx in tmpList!!) {
               list.add(CSVReader.recipe!!.get(idx.toInt()));
        }

        Log.d("abcdef","w2")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        auth = Firebase.auth

        Log.d("abcdef","w3")
        var adapter=CustomAdapter_recommend(baseContext,list)

        recyclerView.adapter=adapter
        recyclerView.layoutManager= LinearLayoutManager(baseContext)

        Log.d("abcdef","w4")
//        val divider = MaterialDividerItemDecoration(baseContext, LinearLayoutManager.VERTICAL /*or LinearLayoutManager.HORIZONTAL*/)
//        divider.isLastItemDecorated=false

        //recyclerView.addItemDecoration(divider)


        adapter.webButtonClick= object : CustomAdapter_recommend.WebButtonClick {
            override fun onClick(view: View, url: String) {
                val intent= Intent(baseContext,WebViewActivity::class.java)
                intent.putExtra("url",url)
                startActivity(intent)
            }
        }
        adapter.bookmarkClick= object : CustomAdapter_recommend.BookmarkClick {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(view: View, recipeIdx: String) {
                val myBookmark= Firebase.database.getReference("bookmark_ref").child(auth.currentUser!!.uid)
                myBookmark.child(recipeIdx).setValue(recipeIdx)
                showToast(baseContext,"북마크 되었습니다!")
                list.removeIf { it.idx==recipeIdx }
                adapter.notifyDataSetChanged()
            }
        }
        Log.d("abcdef","w5")
        val backButton=findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish();
        }

    }

    // Toast 겹치지 않고 자연스럽게 만들기 위한 코드
    override fun onStop() {
        super.onStop()

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
package com.yohan.esg_app

import android.content.Context
import android.content.Intent
import android.graphics.ColorSpace.Model
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import com.yohan.esg_app.CSVReader.recipe

class Fragment3_main(var mToast:Toast) : Fragment() {

    private lateinit var auth: FirebaseAuth
    var list= mutableListOf<Recipe>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_fragment3_main, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        auth = Firebase.auth


        var adapter=CustomAdapter_bookmark(view.context,list)
        recyclerView.adapter=adapter
        recyclerView.layoutManager= LinearLayoutManager(context)

        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL /*or LinearLayoutManager.HORIZONTAL*/)
        divider.isLastItemDecorated=false

        recyclerView.addItemDecoration(divider)

        adapter.webButtonClick= object : CustomAdapter_bookmark.WebButtonClick {
            override fun onClick(view: View, url: String) {
                val intent= Intent(context,WebViewActivity::class.java)
                intent.putExtra("url",url)
                startActivity(intent)
            }
        }
        adapter.cancelButtonClick= object : CustomAdapter_bookmark.CancelButtonClick {
            override fun onClick(view: View, recipeIdx: String) {
                val myBookmark=Firebase.database.getReference("bookmark_ref").child(auth.currentUser!!.uid)
                myBookmark.child(recipeIdx).removeValue()
                showToast(requireContext(),"북마크 취소 되었습니다!")

            }
        }

        val database= Firebase.database
        val myBookmarkRef=database.getReference("bookmark_ref")


            .child(auth.currentUser?.uid.toString())
            .addValueEventListener(object: ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    list.clear()
                    for (dataModel in snapshot.children) {
                        val temp=dataModel.getValue(String::class.java)!!.toInt()


                        list.add(CSVReader.recipe!!.get(dataModel.getValue(String::class.java)!!.toInt()))
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Bookmark","dbError")
                }
            })

        return view

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
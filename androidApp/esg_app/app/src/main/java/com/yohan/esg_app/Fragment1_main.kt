package com.yohan.esg_app

import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yohan.esg_app.CSVReader.recipe


class Fragment1_main(var mToast:Toast) : Fragment() {

    private lateinit var auth: FirebaseAuth
    val list=recipe!!.subList(0,30)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_fragment1_main, container, false)

        auth = Firebase.auth

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val recipe= CSVReader.recipe

        Log.d("frag1mymymy",Firebase.auth.uid.toString())

        var adapter=CustomAdapter(view.context,list)

        adapter.button1Click= object : CustomAdapter.Button1Click {
            override fun onClick(view: View, position: Int) {

                val intent= Intent(context,WebViewActivity::class.java)
                intent.putExtra("url",list.get(position).url)
                startActivity(intent)
            }
        }
        adapter.button2Click= object : CustomAdapter.Button2Click {
            override fun onClick(view: View, recipeIdx: String) {

                var dataBase=Firebase.database
                Log.d("a",recipeIdx)
//                val myBookmarks=dataBase.getReference("bookmark_ref").child(auth.currentUser!!.uid)
//                myBookmarks.push().setValue(recipeIdx)
                Log.d("frag1mymymy",auth.currentUser?.uid.toString())
                val myBookmarks=dataBase.getReference("bookmark_ref").child(auth.currentUser!!.uid)
                myBookmarks.child(recipeIdx).setValue(recipeIdx)
                showToast(requireContext(),"북마크 되었습니다!")
            }

        }

        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(view.context)

        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL /*or LinearLayoutManager.HORIZONTAL*/)
        divider.isLastItemDecorated=false

        recyclerView.addItemDecoration(divider)


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
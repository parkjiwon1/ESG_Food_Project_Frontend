package com.yohan.esg_app

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.util.Log
import android.view.ContentInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class CustomAdapter(val context: Context, val dataSet: List<Recipe>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    interface Button1Click {
        fun onClick(view:View,position:Int)
    }
    var button1Click:Button1Click?=null

    interface Button2Click {
        fun onClick(view:View,recipeIdx:String)
    }
    var button2Click:Button2Click?=null

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    //dataSet 각각의 Array<String>은 idx,url,imageUrl,title,content 순서.
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView1: TextView
        val textView2:TextView
        val imageView:ImageView
        //val button1:Button
        //val button2:Button
        init {
            // Define click listener for the ViewHolder's View.
            textView1 = view.findViewById(R.id.item1)
            textView2=view.findViewById<TextView>(R.id.item2)
            imageView=view.findViewById<ImageView>(R.id.imageView)
            //button1=view.findViewById<Button>(R.id.button1)
            //button2=view.findViewById<Button>(R.id.button2)
        }

        fun bindItem(position:Int) {

            textView1.text=dataSet.get(position).title
            val content=dataSet.get(position).content
            if(content.length<60)textView2.text=content+"..."
            else textView2.text=content.substring(0,60)+"..."
            //GlideApp.with(context).load(dataSet.get(position).get(2)).into(imageView)
            Glide.with(context).load(dataSet.get(position).imageUrl).placeholder(R.drawable.prepare).error(R.drawable.prepare).into(imageView)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (button1Click  != null) {
            val button1=viewHolder.view.findViewById<Button>(R.id.button1)
            button1.setOnClickListener {
                button1Click!!.onClick(viewHolder.view,position)

            }
        }
        Log.d("Number",position.toString())
        if (button2Click != null) {
            val button2=viewHolder.view.findViewById<Button>(R.id.button2)
            button2.setOnClickListener {
                button2Click!!.onClick(viewHolder.view,dataSet.get(position).idx)
            }
        }
        viewHolder.bindItem(position)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}

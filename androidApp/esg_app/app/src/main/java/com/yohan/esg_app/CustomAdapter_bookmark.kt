package com.yohan.esg_app

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CustomAdapter_bookmark(val context:Context,val dataSet: List<Recipe>) :
    RecyclerView.Adapter<CustomAdapter_bookmark.ViewHolder>() {

    interface WebButtonClick {
        fun onClick(view:View,url:String)
    }
    interface CancelButtonClick {
        fun onClick(view:View,recipeIdx:String)
    }
    var webButtonClick:WebButtonClick?=null
    var cancelButtonClick:CancelButtonClick?=null
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView1: TextView
        val textView2:TextView
        val imageView: ImageView
        init {
            // Define click listener for the ViewHolder's View.
            textView1 = view.findViewById(R.id.text1)
            textView2=view.findViewById(R.id.text2)
            imageView=view.findViewById(R.id.imageView)
        }
        fun bindItem(position:Int) {

            textView1.text=dataSet.get(position).title
            val content=dataSet.get(position).content
            if(content.length<30)textView2.text=content+"..."
            else textView2.text=content.substring(0,30)+"..."
            //GlideApp.with(context).load(dataSet.get(position).get(2)).into(imageView)
            Glide.with(context).load(dataSet.get(position).imageUrl).placeholder(R.drawable.prepare).error(R.drawable.prepare).into(imageView)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_bookmark, viewGroup, false)

        return ViewHolder(view)
    }



    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        if (webButtonClick != null) {
            val webButton = viewHolder.itemView.findViewById<Button>(R.id.toWeb)
            webButton.setOnClickListener {
                webButtonClick!!.onClick(viewHolder.itemView, dataSet.get(position).url)
            }
        }
        if (cancelButtonClick != null) {
            val cancelBookmark = viewHolder.itemView.findViewById<Button>(R.id.cancelBookmark)
            cancelBookmark.setOnClickListener {
                cancelButtonClick!!.onClick(viewHolder.itemView, dataSet.get(position).idx)
            }
            viewHolder.bindItem(position)

        }
    }
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}

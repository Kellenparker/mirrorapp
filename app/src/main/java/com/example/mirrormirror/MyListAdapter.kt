package com.example.mirrormirror

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import java.lang.IndexOutOfBoundsException

class MyListAdapter(private val context: Activity, private val title: MutableList<String>, private val description: MutableList<String>, private val imgid: MutableList<String>)
    : ArrayAdapter<String>(context, R.layout.custom_list, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, false)

        val titleText = rowView.findViewById(R.id.title) as TextView
        val imageView = rowView.findViewById(R.id.icon) as ImageView
        val subtitleText = rowView.findViewById(R.id.description) as TextView

        try {
            titleText.text = title[position]
            var imgUrl = imgid[position]
            if (imgUrl.isEmpty()){
            }else{
                Picasso.with(this.context).load(imgUrl).into(imageView)
            }
            subtitleText.text = description[position]
        }catch (e: IndexOutOfBoundsException){
        }
        return rowView
    }
}
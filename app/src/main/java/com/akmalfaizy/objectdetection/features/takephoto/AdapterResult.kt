package com.akmalfaizy.objectdetection.features.takephoto

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akmalfaizy.objectdetection.R

class AdapterResult(private val context: ResultTakePhoto, private val listDataResult: ArrayList<DataClassResult>) : RecyclerView.Adapter<AdapterResult.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterResult.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_result_rv, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listDataResult[position]

        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.displayed_link))
            context.startActivity(intent)
        }
        holder.tvdisplayed_link.text = currentItem.displayed_link
        holder.tvtitle.text = currentItem.title
        holder.tvsnippet.text = currentItem.snippet

    }
    override fun getItemCount(): Int {
        return listDataResult.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var tvtitle: TextView = itemView.findViewById(R.id.ResultTitle)
        var tvdisplayed_link: TextView = itemView.findViewById(R.id.ResultDisplayedLink)
        var tvsnippet: TextView = itemView.findViewById(R.id.ResultSnippet)
    }
}
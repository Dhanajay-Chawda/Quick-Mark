package com.example.smart_attendence_system.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_attendence_system.DataClass.User4
import com.example.smart_attendence_system.R

class MyAdapter4 (private val userList: ArrayList<User4>):
    RecyclerView.Adapter<MyAdapter4.MyViewHolder>() {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvName: TextView =itemView.findViewById(R.id.Date)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemViwe = LayoutInflater.from(parent.context).inflate(R.layout.list_item3,parent,false)

        return MyViewHolder(itemViwe)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvName.text = userList[position].name


    }

}
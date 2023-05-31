package com.example.smart_attendence_system.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_attendence_system.R
import com.example.smart_attendence_system.DataClass.User2


class MyAdapter2(private val userList: ArrayList<User2>):
    RecyclerView.Adapter<MyAdapter2.MyViewHolder>() {
    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val tvName: TextView =itemView.findViewById(R.id.Name)
//        val tvID: TextView =itemView.findViewById(R.id.ID)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemViwe = LayoutInflater.from(parent.context).inflate(R.layout.list_item2,parent,false)

        return MyViewHolder(itemViwe)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvName.text = userList[position].name
//        holder.tvID.text = userList[position].ID

    }


}
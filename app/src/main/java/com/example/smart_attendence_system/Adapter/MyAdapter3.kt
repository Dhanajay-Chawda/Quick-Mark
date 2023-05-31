package com.example.smart_attendence_system.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_attendence_system.Class_Info
import com.example.smart_attendence_system.R
import com.example.smart_attendence_system.Student_List
import com.example.smart_attendence_system.DataClass.User3
import com.example.smart_attendence_system.PresentStudentList

class MyAdapter3 (private val userList: ArrayList<User3> , private val ourid: String?):
    RecyclerView.Adapter<MyAdapter3.MyViewHolder>() {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvDate: TextView =itemView.findViewById(R.id.Date)
//        val tvDay: TextView =itemView.findViewById(R.id.Day)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemViwe = LayoutInflater.from(parent.context).inflate(R.layout.list_item3,parent,false)

        return MyViewHolder(itemViwe)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvDate.text = userList[position].presentid
//        holder.tvDay.text = userList[position].Day


        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PresentStudentList::class.java)
            val user = userList[position]
            intent.putExtra("id69", user.presentid)
            intent.putExtra("ourid",ourid)
            holder.itemView.context.startActivity(intent)
        }

    }

}
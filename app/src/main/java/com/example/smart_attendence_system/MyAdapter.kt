package com.example.smart_attendence_system

import User
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MyAdapter(private val userList: ArrayList<User>):
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(itemViwe: View):RecyclerView.ViewHolder(itemViwe){
        val tvClass:TextView=itemView.findViewById(R.id.clas_name)
        val tvSection:TextView=itemView.findViewById(R.id.section)
        val tvSubject:TextView=itemView.findViewById(R.id.subject)



        init {


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemViwe = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)

        return MyViewHolder(itemViwe)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvClass.text = userList[position].class_name
        holder.tvSection.text = userList[position].section
        holder.tvSubject.text = userList[position].subject

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, Add_Student::class.java)
            val user = userList[position]
//            Log.d("tmp123", userList[position].classid.toString());
            intent.putExtra("id", user.classid)
            holder.itemView.context.startActivity(intent)
        }

    }



    override fun getItemCount(): Int {
        return userList.size
    }

}
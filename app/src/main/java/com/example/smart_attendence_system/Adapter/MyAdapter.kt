package com.example.smart_attendence_system.Adapter

import com.example.smart_attendence_system.DataClass.User
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_attendence_system.Class_Info
import com.example.smart_attendence_system.R


class MyAdapter(private val userList: ArrayList<User>):
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(itemViwe: View):RecyclerView.ViewHolder(itemViwe){
        val tvClass:TextView=itemView.findViewById(R.id.clas_name)
        val tvSection:TextView=itemView.findViewById(R.id.section)
        val tvSubject:TextView=itemView.findViewById(R.id.subject)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemViwe = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)

        return MyViewHolder(itemViwe)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        // Set the default text for Section and Subject TextViews
        holder.tvSection.text = "Section:- "
        holder.tvSubject.text = "Subject:- "

        // Set the actual data from the fetched data
        holder.tvClass.text = userList[position].class_name
        userList[position].section?.let { holder.tvSection.append(it) }
        userList[position].subject?.let { holder.tvSubject.append(it) }

       /* holder.tvClass.text = userList[position].class_name
        holder.tvSection.text = userList[position].section
        holder.tvSubject.text = userList[position].subject*/


        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, Class_Info::class.java)
            val user = userList[position]
            intent.putExtra("id", user.classid)
//            Log.d("TAG", "classid value: " + user.classid);
            holder.itemView.context.startActivity(intent)
        }


//        holder.itemView.setOnClickListener {
//            val context = holder.itemView.context
//            val user = userList[position]
//
//            // Create an intent to send data to the other activity
//            val addStudent = Intent(holder.itemView.context, Add_Student::class.java)
//            addStudent.putExtra("id", user.classid)
//
//            // Create an intent to open the Class_Info activity
//            val classInfoIntent = Intent(context, Class_Info::class.java)
//            classInfoIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//
//            // Start both activities with the intents
//           holder.itemView.context.startActivity(addStudent)
//            context.startActivity(classInfoIntent)
//        }

    }



    override fun getItemCount(): Int {
        return userList.size
    }

}
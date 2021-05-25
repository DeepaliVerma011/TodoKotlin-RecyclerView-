package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.item_todo.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class todoAdapter(val list: List<TodoModel>):RecyclerView.Adapter<todoAdapter.TodoViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(LayoutInflater.from(
            parent.context).inflate(R.layout.item_todo,parent,false ))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
       holder.bind(list[position])
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }
    class TodoViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bind(todoModel: TodoModel) {
                  with(itemView){
                      val colors=resources.getIntArray(R.array.random_color)
                      val randomColor=colors[Random.nextInt(colors.size)]
                      viewcolorTag.setBackgroundColor(randomColor)
                      txtshowtitle.text=todoModel.title
                      txtshowSubtitle.text=todoModel.description
                      txtshowcategory.text=todoModel.category
                      updateTime(todoModel.time)
                      updateDate(todoModel.date)
                  }
        }
        private fun updateTime(time: Long) {
            val myformat="h:mm a"
            val sdf= SimpleDateFormat(myformat)
           itemView.showtime.text=sdf.format(Date(time))

        }
        private fun updateDate(date: Long) {
            val myformat="EEE, d MMM yyyy"
            val sdf=SimpleDateFormat(myformat)
            itemView.showdate.text=sdf.format(Date(date))

        }

    }
}
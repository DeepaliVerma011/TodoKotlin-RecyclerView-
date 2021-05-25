package com.example.todoapp

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Math.floor


class MainActivity: AppCompatActivity() {
    private val list = arrayListOf<TodoModel>()
    var adapter=todoAdapter(list)

    val db by lazy{
       AppDatabase.getDatabase(this)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        todoRv.apply {
            layoutManager=LinearLayoutManager(this@MainActivity)
            adapter=this@MainActivity.adapter
        }
        displayTodo("")
        initSwipe()
        db.todoDao().getTask().observe(this, {
            if(it.isNullOrEmpty()){
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }
            else{
                list.clear()
                adapter.notifyDataSetChanged()
            }
        })
    }
    private fun initSwipe() {
        val simpleItemTouchCallback = object : SimpleCallback(
            0,
            LEFT or RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                if (direction == LEFT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().DeleteTask(adapter.getItemId(position))
                    }
                } else if (direction == RIGHT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().finishTask(adapter.getItemId(position))
                    }
                }
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView

                    val paint = Paint()
                    val icon: Bitmap?


                    if (dX > 0) {

                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_baseline_check_24)

                        paint.color = Color.parseColor("#388E3C")

                        canvas.drawRect(
                            itemView.left.toFloat(), itemView.top.toFloat(),
                            itemView.left.toFloat() + dX, itemView.bottom.toFloat(), paint
                        )

                        canvas.drawBitmap(
                            icon,
                            itemView.left.toFloat(),
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )


                    } else {
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_baseline_delete_24)

                        paint.color = Color.parseColor("#D32F2F")

                        canvas.drawRect(
                            itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                        )

                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat() - icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )
                    }

                        viewHolder.itemView.translationX = dX


                } else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

            }


        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(todoRv)
    }

    fun displayTodo(newText: String = "") {
        db.todoDao().getTask().observe(this, Observer {
            if(it.isNotEmpty()){
                list.clear()
                list.addAll(
                    it.filter { todo ->
                        todo.title.contains(newText,true)
                    }
                )
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        val item= menu?.findItem(R.id.search)
        val searchView= item?.actionView as SearchView
        item.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                displayTodo()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                displayTodo()
                return true
            }

        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
              if(!newText.isNullOrEmpty()){
                  displayTodo(newText)

              }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.history->{
                startActivity(Intent(this,HistoryActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }
    fun openNewTask(view: View) {
        startActivity(Intent(this, TaskActivity::class.java))
    }


}
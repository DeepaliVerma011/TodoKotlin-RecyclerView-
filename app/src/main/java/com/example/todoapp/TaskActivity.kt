package com.example.todoapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

const val DB_NAME="todo.db"

class TaskActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mycalender:Calendar

    lateinit var dateSetListener:DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    var finalDate = 0L
    var finalTime = 0L


    private val labels= arrayListOf("Personal","Business","Insurance","Shopping","Banking")

    val db by lazy{
       AppDatabase.getDatabase(this)


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        etdate.setOnClickListener(this)
        etTime.setOnClickListener(this)
        savebtn.setOnClickListener(this)

        setUpSpinner()


    }

    private fun setUpSpinner() {
        val adapter=ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_dropdown_item,labels)
        labels.sort()
        spinnerCategory.adapter=adapter
    }

    override fun onClick(v: View) {
        when (v.id){
            R.id.etdate->{
                setListener()
            }
            R.id.etTime->{
                setTimeListener()
            }
            R.id.savebtn->{
                saveTodo()
            }

        }
    }

    private fun saveTodo() {
        val category = spinnerCategory.selectedItem.toString()
        val title = titleInPlay.editText?.text.toString()
        val description = taskInPlay.editText?.text.toString()

        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                return@withContext db.todoDao().insertTask(
                    TodoModel(
                        title,
                        description,
                        category,
                        finalDate,
                        finalTime
                    )
                )
            }
            finish()
        }
    }


    private fun setTimeListener() {
        mycalender= Calendar.getInstance()
        timeSetListener=TimePickerDialog.OnTimeSetListener{ _: TimePicker, hourofday: Int, min: Int ->
            mycalender.set(Calendar.HOUR_OF_DAY,hourofday)
            mycalender.set(Calendar.MINUTE,min)
            updateTime()
        }
        val timePickerDialog=TimePickerDialog(
            this,
            timeSetListener,
            mycalender.get(Calendar.HOUR_OF_DAY),
            mycalender.get(Calendar.MINUTE),false

        )
//min date>current date

        timePickerDialog.show()
    }

    private fun updateTime() {
        val myformat="h:mm a"
        val sdf=SimpleDateFormat(myformat)
        etTime.setText(sdf.format(mycalender.time))

    }

    private fun setListener() {
        mycalender= Calendar.getInstance()
        dateSetListener=DatePickerDialog.OnDateSetListener{ _: DatePicker, year: Int,month: Int,dayOfMonth: Int ->
                mycalender.set(Calendar.YEAR,year)
            mycalender.set(Calendar.MONTH,month)
            mycalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDate()
        }
        val datePickerDialog=DatePickerDialog(
            this,
            dateSetListener,
            mycalender.get(Calendar.YEAR),
            mycalender.get(Calendar.MONTH),
            mycalender.get(Calendar.DAY_OF_MONTH),
        )
//min date>current date
        datePickerDialog.datePicker.minDate=System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        val myformat="EEE, d MMM yyyy"
        val sdf=SimpleDateFormat(myformat)
        etdate.setText(sdf.format(mycalender.time))
        timeInptLay.visibility = View.VISIBLE

    }
}



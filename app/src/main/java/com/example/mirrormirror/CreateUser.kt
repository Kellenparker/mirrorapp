package com.example.mirrormirror

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class CreateUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        val createBtn = findViewById<Button>(R.id.create)
        val agePick = findViewById<NumberPicker>(R.id.age1)
        val male = findViewById<RadioButton>(R.id.male)
        val female = findViewById<RadioButton>(R.id.female)
        val nonbinary = findViewById<RadioButton>(R.id.nonbinary)
        var age = 0
        var genderVar = 0

        if (agePick != null) {
            agePick.minValue = 0
            agePick.maxValue = 120
            agePick.wrapSelectorWheel = true
            agePick.setOnValueChangedListener { picker, oldVal, newVal ->
                age = newVal
            }
        }
        createBtn.setOnClickListener {
            if (male.isChecked){
                genderVar = 0
            }else if (female.isChecked){
                genderVar = 1
            }else if (nonbinary.isChecked){
                genderVar = 2
            }

            val resultIntent = Intent()
            resultIntent.putExtra("age", age)
            resultIntent.putExtra("gender", genderVar)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

    }
}
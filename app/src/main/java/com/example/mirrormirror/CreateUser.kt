package com.example.mirrormirror

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import org.w3c.dom.Text

class CreateUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        val createBtn = findViewById<Button>(R.id.create)
        val agePick = findViewById<NumberPicker>(R.id.age1)
        val male = findViewById<RadioButton>(R.id.male)
        val female = findViewById<RadioButton>(R.id.female)
        val nonbinary = findViewById<RadioButton>(R.id.nonbinary)
        val fname = findViewById<EditText>(R.id.fname1)
        val lname = findViewById<EditText>(R.id.lname1)
        val errorMsg = findViewById<TextView>(R.id.errorText)
        val radioGroup = findViewById<RadioGroup>(R.id.gender1)
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
            if (fname.text.isEmpty()){
                errorMsg.setText("Please enter your first name.")
            } else if (lname.text.isEmpty()){
                errorMsg.setText("Please enter your last name.")
            }else if (radioGroup.checkedRadioButtonId == -1){
                errorMsg.setText("Please enter your gender.")
            }else{
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
}
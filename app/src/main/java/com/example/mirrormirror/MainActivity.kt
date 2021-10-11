package com.example.mirrormirror

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timeBtn = findViewById<Button>(R.id.timeBtn)
        val capBtn = findViewById<Button>(R.id.capBtn)

        val database = Firebase.database
        val timeRef = database.getReference("time/")
        var tBool = true
        timeRef.setValue(true);

        timeBtn.setOnClickListener {
            if(tBool) {
                timeRef.setValue(!tBool)
                tBool = false
            }else{
                timeRef.setValue(!tBool)
                tBool = true
            }
        }

        val capRef = database.getReference("camera/capture")

        capBtn.setOnClickListener {
            capRef.setValue(true)
        }
    }
}
package com.example.mirrormirror

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class MainActivity : AppCompatActivity() {
    private val SECOND_ACTIVITY_REQUEST_CODE = 0
    val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this,CreateUser::class.java)
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE)

        val timeBtn = findViewById<Button>(R.id.timeBtn)
        val capBtn = findViewById<Button>(R.id.capBtn)
        val listLinks = findViewById<ListView>(R.id.linksListView)
        val timeRef = database.getReference("modules/time/disabled")
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

        val capRef = database.getReference("scan/camera")
        var reference = ""
        lateinit var mdatabase: DatabaseReference
        mdatabase = Firebase.database.reference

        mdatabase.child("scan/links/link0/linkUrl").get().addOnSuccessListener {
            reference = it.value.toString()
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        capBtn.setOnClickListener {
            capRef.setValue(true)
            val listItems = mutableListOf("")
            listItems.add(reference)
            val adapter:ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,listItems)
            listLinks.adapter = adapter
            listLinks.setOnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position) as String
                val openURL = Intent(android.content.Intent.ACTION_VIEW)
                openURL.data = Uri.parse("https://www.amazon.com" + selectedItem)
                startActivity(openURL)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val returnString = data!!.getIntExtra("age", 0)
                val returnString2 = data!!.getIntExtra("gender", 0)
                val ageRef = database.getReference("user/age/")
                val genderRef = database.getReference("user/gender/")
                ageRef.setValue(returnString)
                genderRef.setValue(returnString2)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mirror_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this,SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
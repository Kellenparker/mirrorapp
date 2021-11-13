package com.example.mirrormirror

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ListView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
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
        val loadingIcon = findViewById<ImageView>(R.id.loading)
        val continueButton = findViewById<Button>(R.id.continueBtn)
        val timeRef = database.getReference("modules/time/disabled")
        val stageRef2 = database.getReference("scan/stage")
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

        val capRef = database.getReference("scan/camera/capture")
        var dataReference = database.getReference()
        lateinit var mdatabase: DatabaseReference
        mdatabase = Firebase.database.reference



        capBtn.setOnClickListener {
            var reference = ""
            var reference2 = ""
            var reference3 = ""
            var listItems = mutableListOf<String>()
            val listItems2 = mutableListOf<String>()
            val listItems3 = mutableListOf<String>()
            stageRef2.setValue(1)
            capRef.setValue(true)
            loadingIcon.setImageResource(R.drawable.centerwhite)
            continueButton.isVisible = true
            capBtn.isVisible = false
            loadingIcon.isVisible = true
            listLinks.isVisible = false

            continueButton.setOnClickListener() {
                stageRef2.setValue(2)
                Glide.with(this).load(R.drawable.loadingicon2).into(loadingIcon)
                loadingIcon.isVisible = true
                listLinks.isVisible = false
                continueButton.isVisible = false
                capBtn.isVisible = true
                dataReference.child("scan/stage")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val stageRef = dataSnapshot.getValue().toString()
                            if (stageRef == "3") {
                                loadingIcon.isVisible = false
                                listLinks.isVisible = true
                                for (i in 0..45) {
                                    mdatabase.child("scan/links/link$i/linkUrl").get()
                                        .addOnSuccessListener {
                                            reference = it.value.toString()
                                            listItems.add(reference)
                                            mdatabase.child("scan/links/link$i/linkTitle").get()
                                                .addOnSuccessListener {
                                                    reference2 = it.value.toString()
                                                    listItems2.add(reference2)
                                                    mdatabase.child("scan/links/link$i/linkImg")
                                                        .get().addOnSuccessListener {
                                                        reference3 = it.value.toString()
                                                        listItems3.add(reference3)
                                                        mdatabase.child("scan/stage").get()
                                                            .addOnSuccessListener() {
                                                                val myListAdapter = MyListAdapter(
                                                                    this@MainActivity,
                                                                    listItems,
                                                                    listItems2,
                                                                    listItems3
                                                                )
                                                                listLinks.adapter = myListAdapter
                                                            }
                                                    }
                                                }
                                        }
                                }
                                listLinks.setOnItemClickListener { parent, _, position, _ ->
                                    val selectedItem = parent.getItemAtPosition(position) as String
                                    val openURL = Intent(android.content.Intent.ACTION_VIEW)
                                    openURL.data =
                                        Uri.parse("https://www.amazon.com" + selectedItem)
                                    startActivity(openURL)
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            //This doesn't do anything but will cause errors if you delete it
                        }
                    })
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
package com.example.mirrormirror

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private val SECOND_ACTIVITY_REQUEST_CODE = 0
    val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        //For saving user data
        val sharedPreference = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        //If user is not already signed in, make them sign in or create an account
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null){
            if (sharedPreference.getString("first_name", "") == "") {
                val intent = Intent(this, CreateUser::class.java)
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE)
            }
        }


        //Scanning feature with links
        val capBtn = findViewById<Button>(R.id.capBtn)
        val listLinks = findViewById<ListView>(R.id.linksListView)
        val loadingIcon = findViewById<ImageView>(R.id.loading)
        val continueButton = findViewById<Button>(R.id.continueBtn)
        val voiceCommandsButton = findViewById<Button>(R.id.voiceCommandsBtn)
        val voiceCommands = findViewById<TextView>(R.id.voiceCommands)
        val noBtn = findViewById<Button>(R.id.noBtn)
        val yesBtn = findViewById<Button>(R.id.yesBtn)
        val startOver = findViewById<TextView>(R.id.startOver)
        val wait = findViewById<TextView>(R.id.pleaseWait)
        val backButton = findViewById<Button>(R.id.backButton)
        val stageRef2 = database.getReference("scan/stage")

        val capRef = database.getReference("scan/camera/capture")
        var dataReference = database.getReference()
        lateinit var mdatabase: DatabaseReference
        mdatabase = Firebase.database.reference

        voiceCommandsButton.setOnClickListener {
            if (listLinks.isVisible) {
                voiceCommands.isVisible = true
                listLinks.isVisible = false
            }else if (loadingIcon.isVisible){
                voiceCommands.isVisible = true
                loadingIcon.isVisible = false
            }else if(voiceCommands.isVisible){
                voiceCommands.isVisible = false
                listLinks.isVisible = true
            }else if(!voiceCommands.isVisible){
                voiceCommands.isVisible = true
            }

        }

        capBtn.setOnClickListener {
            stageRef2.setValue(1)
            loadingIcon.setImageResource(R.drawable.centerwhite)
            continueButton.isVisible = true
            capBtn.isVisible = false
            loadingIcon.isVisible = true
            listLinks.isVisible = false
            noBtn.isVisible = false
            yesBtn.isVisible = false
            startOver.isVisible = false
            voiceCommandsButton.isVisible = false
            voiceCommands.isVisible = false
            wait.isVisible = false
            backButton.isVisible = true

            backButton.setOnClickListener{
                stageRef2.setValue(0)
                startOver.isVisible = false
                wait.isVisible = false
                capBtn.isVisible = true
                continueButton.isVisible = false
                noBtn.isVisible = false
                yesBtn.isVisible = false
                voiceCommandsButton.isVisible = true
                voiceCommands.isVisible = false
                loadingIcon.isVisible = false
                listLinks.isVisible = true
                backButton.isVisible = false
            }


            continueButton.setOnClickListener() {
                stageRef2.setValue(2)
                capRef.setValue(true)
                loadingIcon.isVisible = false
                listLinks.isVisible = false
                continueButton.isVisible = false
                capBtn.isVisible = false
                noBtn.isVisible = true
                yesBtn.isVisible = true
                startOver.isVisible = true
                wait.isVisible = false
                backButton.isVisible = false
                voiceCommandsButton.isVisible = false
                voiceCommands.isVisible = false
                Thread.sleep(3500)
            }

            noBtn.setOnClickListener{
                stageRef2.setValue(1)
                continueButton.isVisible = true
                capBtn.isVisible = false
                loadingIcon.isVisible = true
                listLinks.isVisible = false
                noBtn.isVisible = false
                yesBtn.isVisible = false
                startOver.isVisible = false
                voiceCommandsButton.isVisible = false
                voiceCommands.isVisible = false
                wait.isVisible = false
                backButton.isVisible = true
            }

            yesBtn.setOnClickListener{
                stageRef2.setValue(3)
                Glide.with(this).load(R.drawable.loadingicon2).into(loadingIcon)
                loadingIcon.isVisible = true
                listLinks.isVisible = false
                continueButton.isVisible = false
                capBtn.isVisible = false
                startOver.isVisible = false
                noBtn.isVisible = false
                yesBtn.isVisible = false
                wait.isVisible = true
                backButton.isVisible = false
                voiceCommandsButton.isVisible = false
                voiceCommands.isVisible = false
            }
        }

        dataReference.child("scan/stage")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var reference = ""
                    var reference2 = ""
                    var reference3 = ""
                    var listItems = mutableListOf<String>()
                    val listItems2 = mutableListOf<String>()
                    val listItems3 = mutableListOf<String>()

                    val stageRef = dataSnapshot.getValue().toString()

                    if (stageRef == "4") {
                        voiceCommandsButton.isVisible = true
                        voiceCommands.isVisible = false
                        loadingIcon.isVisible = false
                        startOver.isVisible = false
                        noBtn.isVisible = false
                        yesBtn.isVisible = false
                        listLinks.isVisible = true
                        capBtn.isVisible = true
                        for (i in 1..45) {
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
                                                            val myListAdapter =
                                                                MyListAdapter(
                                                                    this@MainActivity,
                                                                    listItems,
                                                                    listItems2,
                                                                    listItems3
                                                                )
                                                            listLinks.adapter =
                                                                myListAdapter
                                                        }
                                                }
                                        }
                                }
                        }
                        listLinks.setOnItemClickListener { parent, _, position, _ ->
                            val selectedItem = parent.getItemAtPosition(position) as String
                            val openURL = Intent(android.content.Intent.ACTION_VIEW)
                            openURL.data = Uri.parse(selectedItem)
                            startActivity(openURL)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    //This doesn't do anything but will cause errors if you delete it
                }
            })

        // Set Dark mode based on user preferences
        if (sharedPreference.getString("darkMode", "false") == "false") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val sharedPreference = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            //Make sure everything went ok from the create user page and save their name and uid
            if (resultCode == Activity.RESULT_OK) {
                var editor = sharedPreference.edit()
                val returnFirstName = data?.getStringExtra("firstName")
                val returnUserId = data?.getStringExtra("userId")
                editor.putString("first_name", returnFirstName.toString())
                editor.putString("userId", returnUserId.toString())
                editor.commit()

                //Load user profile onto the mirror.
                val db = Firebase.firestore
                val docRef = db.collection("users")
                    .document(sharedPreference.getString("userId", "").toString())
                docRef.get().addOnSuccessListener { document ->
                    var calendar = document.data?.get("calendar").toString()
                    var motivation = document.data?.get("motivation").toString()
                    var news = document.data?.get("news").toString()
                    var notes = document.data?.get("notes").toString()
                    var traffic = document.data?.get("traffic").toString()
                    var weather = document.data?.get("weather").toString()
                    var text = document.data?.get("text").toString()
                    var time = document.data?.get("time").toString()
                    var darkMode = document.data?.get("darkMode").toString()
                    var age = document.data?.get("age").toString().toInt()
                    var gender = document.data?.get("gender").toString().toInt()
                    var firstname = document.data?.get("fname").toString()
                    var source = document.data?.get("source").toString()
                    var destination = document.data?.get("destination").toString()
                    var arrivalHour = document.data?.get("arrivalHour").toString().toInt()
                    var arrivalMin = document.data?.get("arrivalMin").toString().toInt()


//                    val sharedPreference = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                    editor.putString("calendar", calendar)
                    editor.putString("motivation", motivation)
                    editor.putString("news", news)
                    editor.putString("notes", notes)
                    editor.putString("traffic", traffic)
                    editor.putString("weather", weather)
                    editor.putString("notesText", text)
                    editor.putString("time", time)
                    editor.putString("darkMode", darkMode)
                    editor.putInt("age", age)
                    editor.putInt("gender", gender)
                    editor.putString("source", source)
                    editor.putString("destination", destination)
                    editor.putInt("arrivalHour", arrivalHour)
                    editor.putInt("arrivalMin", arrivalMin)
                    while (!editor.commit()) {
                        Thread.sleep(1000)
                    }
                    // Set Dark mode based on user preferences
                    if (sharedPreference.getString("darkMode", "false") == "false") {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }

                    //Update notes text, time, age, gender, name, traffic
                    val textRef = database.getReference("modules/notes/text")
                    textRef.setValue(sharedPreference.getString("notesText", ""))

                    val timeRef = database.getReference("modules/time/disabled")
                    if (sharedPreference.getString("time", "") == "false") {
                        timeRef.setValue(true)
                    } else {
                        timeRef.setValue(false)
                    }

                    val ageRef = database.getReference("user/age")
                    ageRef.setValue(sharedPreference.getInt("age", 0))

                    val genderRef = database.getReference("user/gender")
                    genderRef.setValue(sharedPreference.getInt("gender", 0))

                    val nameRef = database.getReference("user/name")
                    nameRef.setValue(firstname)

                    val sourceRef = database.getReference("modules/traffic/source")
                    sourceRef.setValue(source)

                    val destinationRef = database.getReference("modules/traffic/destination")
                    destinationRef.setValue(destination)

                    val arrivalHourRef = database.getReference("modules/traffic/hour")
                    arrivalHourRef.setValue(arrivalHour)

                    val arrivalMinRef = database.getReference("modules/traffic/min")
                    arrivalMinRef.setValue(arrivalMin)

                    //Update realtime database module locations from firestore
                    val calendarLoc = sharedPreference.getString("calendar", "")
                    if (calendarLoc == "" || calendarLoc == "iconTray") {
                        updateModule("calendar", -1)
                    }
                    val motivLoc = sharedPreference.getString("motivation", "")
                    if (motivLoc == "" || motivLoc == "iconTray") {
                        updateModule("motivation", -1)
                    }

                    val notesLoc = sharedPreference.getString("notes", "")
                    if (notesLoc == "" || notesLoc == "iconTray") {
                        updateModule("notes", -1)
                    }
                    val newsLoc = sharedPreference.getString("news", "")
                    if (newsLoc == "" || newsLoc == "iconTray") {
                        updateModule("news", -1)
                    }
                    val trafficLoc = sharedPreference.getString("traffic", "")
                    if (trafficLoc == "" || trafficLoc == "iconTray") {
                        updateModule("traffic", -1)
                    }
                    val weatherLoc = sharedPreference.getString("weather", "")
                    if (weatherLoc == "" || weatherLoc == "iconTray") {
                        updateModule("weather", -1)
                    }

                    if (sharedPreference.getString("calendar", "") == "topLeft") {
                        updateModule("calendar", 1)
                    } else if (sharedPreference.getString("calendar", "") == "topRight") {
                        updateModule("calendar", 5)
                    } else if (sharedPreference.getString("calendar", "") == "middleLeft") {
                        updateModule("calendar", 2)
                    } else if (sharedPreference.getString("calendar", "") == "middleRight") {
                        updateModule("calendar", 6)
                    } else if (sharedPreference.getString("calendar", "") == "bottomLeft") {
                        updateModule("calendar", 3)
                    } else if (sharedPreference.getString("calendar", "") == "bottomRight") {
                        updateModule("calendar", 7)
                    }

                    if (sharedPreference.getString("motivation", "") == "topLeft") {
                        updateModule("motivation", 1)
                    } else if (sharedPreference.getString("motivation", "") == "topRight") {
                        updateModule("motivation", 5)
                    } else if (sharedPreference.getString("motivation", "") == "middleLeft") {
                        updateModule("motivation", 2)
                    } else if (sharedPreference.getString("motivation", "") == "middleRight") {
                        updateModule("motivation", 6)
                    } else if (sharedPreference.getString("motivation", "") == "bottomLeft") {
                        updateModule("motivation", 3)
                    } else if (sharedPreference.getString("motivation", "") == "bottomRight") {
                        updateModule("motivation", 7)
                    }

                    if (sharedPreference.getString("notes", "") == "topLeft") {
                        updateModule("notes", 1)
                    } else if (sharedPreference.getString("notes", "") == "topRight") {
                        updateModule("notes", 5)
                    } else if (sharedPreference.getString("notes", "") == "middleLeft") {
                        updateModule("notes", 2)
                    } else if (sharedPreference.getString("notes", "") == "middleRight") {
                        updateModule("notes", 6)
                    } else if (sharedPreference.getString("notes", "") == "bottomLeft") {
                        updateModule("notes", 3)
                    } else if (sharedPreference.getString("notes", "") == "bottomRight") {
                        updateModule("notes", 7)
                    }
                    if (sharedPreference.getString("news", "") == "topLeft") {
                        updateModule("news", 1)
                    } else if (sharedPreference.getString("news", "") == "topRight") {
                        updateModule("news", 5)
                    } else if (sharedPreference.getString("news", "") == "middleLeft") {
                        updateModule("news", 2)
                    } else if (sharedPreference.getString("news", "") == "middleRight") {
                        updateModule("news", 6)
                    } else if (sharedPreference.getString("news", "") == "bottomLeft") {
                        updateModule("news", 3)
                    } else if (sharedPreference.getString("news", "") == "bottomRight") {
                        updateModule("news", 7)
                    }
                    if (sharedPreference.getString("traffic", "") == "topLeft") {
                        updateModule("traffic", 1)
                    } else if (sharedPreference.getString("traffic", "") == "topRight") {
                        updateModule("traffic", 5)
                    } else if (sharedPreference.getString("traffic", "") == "middleLeft") {
                        updateModule("traffic", 2)
                    } else if (sharedPreference.getString("traffic", "") == "middleRight") {
                        updateModule("traffic", 6)
                    } else if (sharedPreference.getString("traffic", "") == "bottomLeft") {
                        updateModule("traffic", 3)
                    } else if (sharedPreference.getString("traffic", "") == "bottomRight") {
                        updateModule("traffic", 7)
                    }

                    if (sharedPreference.getString("weather", "") == "topLeft") {
                        updateModule("weather", 1)
                    } else if (sharedPreference.getString("weather", "") == "topRight") {
                        updateModule("weather", 5)
                    } else if (sharedPreference.getString("weather", "") == "middleLeft") {
                        updateModule("weather", 2)
                    } else if (sharedPreference.getString("weather", "") == "middleRight") {
                        updateModule("weather", 6)
                    } else if (sharedPreference.getString("weather", "") == "bottomLeft") {
                        updateModule("weather", 3)
                    } else if (sharedPreference.getString("weather", "") == "bottomRight") {
                        updateModule("weather", 7)
                    }
                }.addOnFailureListener { exception ->
                    Log.d("Error", "get failed with ", exception)
                }
            }
        }
    }

    //Function that allows for updates between realtime database and Firestore
    fun updateModule(module: String, location: Int) {
        val calLocRef = database.getReference("modules/calendar/location")
        val calRef = database.getReference("modules/calendar/disabled")
        val motivLocRef = database.getReference("modules/motivation/location")
        val motivRef = database.getReference("modules/motivation/disabled")
        val newsLocRef = database.getReference("modules/news/location")
        val newsRef = database.getReference("modules/news/disabled")
        val notesLocRef = database.getReference("modules/notes/location")
        val notesRef = database.getReference("modules/notes/disabled")
        val trafficLocRef = database.getReference("modules/traffic/location")
        val trafficRef = database.getReference("modules/traffic/disabled")
        val weatherLocRef = database.getReference("modules/weather/location")
        val weatherRef = database.getReference("modules/weather/disabled")

        if (location == -1) {
            if (module == "calendar") {
                calRef.setValue(true)
                calLocRef.setValue(location)
            } else if (module == "motivation") {
                motivRef.setValue(true)
                motivLocRef.setValue(location)
            } else if (module == "news") {
                newsRef.setValue(true)
                newsLocRef.setValue(location)
            } else if (module == "notes") {
                notesRef.setValue(true)
                notesLocRef.setValue(location)
            } else if (module == "traffic") {
                trafficRef.setValue(true)
                trafficLocRef.setValue(location)
            } else if (module == "weather") {
                weatherRef.setValue(true)
                weatherLocRef.setValue(location)
            }
        } else {
            if (module == "calendar") {
                calRef.setValue(false)
                calLocRef.setValue(location)
            } else if (module == "motivation") {
                motivRef.setValue(false)
                motivLocRef.setValue(location)
            } else if (module == "news") {
                newsRef.setValue(false)
                newsLocRef.setValue(location)
            } else if (module == "notes") {
                notesRef.setValue(false)
                notesLocRef.setValue(location)
            } else if (module == "traffic") {
                trafficRef.setValue(false)
                trafficLocRef.setValue(location)
            } else if (module == "weather") {
                weatherRef.setValue(false)
                weatherLocRef.setValue(location)
            }
        }
    }

    //Create menu with settings
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mirror_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onBackPressed() {
//        Do nothing
    }
}
package com.example.mirrormirror

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.mirrormirror.databinding.FragmentFirstBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    val database = Firebase.database

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(null)
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback() {
            // Disable android back button
        }
        return binding.root
    }

    //Handle drag events
    val dragListener = View.OnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val sharedPreference =
                    requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                Log.d("Event", event.toString())
                Log.d("view", view.toString())
                val item = event.clipData.getItemAt(0)
                val dragData = item.text.toString()
                view.invalidate()
                val v = event.localState as View
                val owner = v.parent as ViewGroup
                val destination = view as LinearLayout
                val db = Firebase.firestore

                if (destination.childCount < 2) {
                    var destTag = destination.getTag().toString()
                    var itemTag = v.getTag().toString()
                    editor.putString(itemTag, destTag)
                    editor.commit()

                    // update location in database *******************************
                    val docRef = db.collection("users")
                        .document(sharedPreference.getString("userId", "").toString())
                    docRef
                        .update(itemTag, destTag)
                        .addOnSuccessListener {
                            Log.d(
                                "Success",
                                "DocumentSnapshot successfully updated!"
                            )
                        }
                        .addOnFailureListener { e -> Log.w("Error", "Error updating document", e) }

                    owner.removeView(v)
                    destination.addView(v)
                    var dest = destination.getTag()

                    if (dest == "iconTray") {
                        updateModule(dragData, -1)
                    } else if (dest == "topLeft") {
                        updateModule(dragData, 1)
                    } else if (dest == "topRight") {
                        updateModule(dragData, 5)
                    } else if (dest == "middleLeft") {
                        updateModule(dragData, 2)
                    } else if (dest == "middleRight") {
                        updateModule(dragData, 6)
                    } else if (dest == "bottomLeft") {
                        updateModule(dragData, 3)
                    } else if (dest == "bottomRight") {
                        updateModule(dragData, 7)
                    }

                } else if (destination.getTag() == "iconTray") {
                    var destTag = destination.getTag().toString()
                    var itemTag = v.getTag().toString()
                    editor.putString(itemTag, destTag)
                    editor.commit()
                    //update location in firestore
                    val docRef = db.collection("users")
                        .document(sharedPreference.getString("userId", "").toString())
                    docRef
                        .update(itemTag, destTag)
                        .addOnSuccessListener {
                            Log.d(
                                "Success",
                                "DocumentSnapshot successfully updated!"
                            )
                        }
                        .addOnFailureListener { e -> Log.w("Error", "Error updating document", e) }
                    owner.removeView(v)
                    destination.addView(v)
                    updateModule(dragData, -1)
                }
                v.visibility = View.VISIBLE
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else -> false
        }
    }

    // Function that updates the  modules in real time database
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

    // When the settings view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
        // Initialize variables for saving information when app is closed.
        val sharedPreference = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

            // Button control
            binding.customizeBtn.setOnClickListener {
                binding.instructionsLabel.visibility = View.VISIBLE
                binding.masterLayout.visibility = View.VISIBLE
                binding.notesText.visibility = View.GONE
                binding.toggleTimeLabel.visibility = View.VISIBLE
                binding.toggleTimeSwitch.visibility = View.VISIBLE
                binding.darkModeLabel.visibility = View.GONE
                binding.darkModeSwitch.visibility = View.GONE
                binding.customizeBtn.visibility = View.GONE
                binding.notesBtn.visibility = View.GONE
                binding.preferencesBtn.visibility = View.GONE
                binding.logoutLayout.visibility = View.GONE
                binding.backToSettingsLayout.visibility = View.VISIBLE
                binding.backToScanLayout.visibility = View.GONE
                binding.logoutLayout.visibility = View.GONE
                binding.ageGenderLayout.visibility = View.GONE
                binding.startingLabel.visibility = View.GONE
                binding.starting.visibility = View.GONE
                binding.destinationLabel.visibility = View.GONE
                binding.destination.visibility = View.GONE
                binding.update.visibility = View.GONE
            }
            binding.notesBtn.setOnClickListener {
                binding.instructionsLabel.visibility = View.GONE
                binding.masterLayout.visibility = View.GONE
                binding.notesText.visibility = View.VISIBLE
                binding.toggleTimeLabel.visibility = View.GONE
                binding.toggleTimeSwitch.visibility = View.GONE
                binding.darkModeLabel.visibility = View.GONE
                binding.darkModeSwitch.visibility = View.GONE
                binding.customizeBtn.visibility = View.GONE
                binding.notesBtn.visibility = View.GONE
                binding.preferencesBtn.visibility = View.GONE
                binding.logoutLayout.visibility = View.GONE
                binding.backToSettingsLayout.visibility = View.VISIBLE
                binding.backToScanLayout.visibility = View.GONE
                binding.logoutLayout.visibility = View.GONE
                binding.ageGenderLayout.visibility = View.GONE
                binding.startingLabel.visibility = View.GONE
                binding.starting.visibility = View.GONE
                binding.destinationLabel.visibility = View.GONE
                binding.destination.visibility = View.GONE
                binding.update.visibility = View.GONE
            }
            binding.preferencesBtn.setOnClickListener {
                binding.instructionsLabel.visibility = View.GONE
                binding.masterLayout.visibility = View.GONE
                binding.notesText.visibility = View.GONE
                binding.toggleTimeLabel.visibility = View.GONE
                binding.toggleTimeSwitch.visibility = View.GONE
                binding.darkModeLabel.visibility = View.VISIBLE
                binding.darkModeSwitch.visibility = View.VISIBLE
                binding.customizeBtn.visibility = View.GONE
                binding.notesBtn.visibility = View.GONE
                binding.preferencesBtn.visibility = View.GONE
                binding.logoutLayout.visibility = View.GONE
                binding.backToSettingsLayout.visibility = View.VISIBLE
                binding.backToScanLayout.visibility = View.GONE
                binding.ageGenderLayout.visibility = View.VISIBLE
                binding.logoutLayout.visibility = View.VISIBLE
                binding.startingLabel.visibility = View.VISIBLE
                binding.starting.visibility = View.VISIBLE
                binding.destinationLabel.visibility = View.VISIBLE
                binding.destination.visibility = View.VISIBLE
                binding.update.visibility = View.VISIBLE
            }
            binding.backToScan.setOnClickListener {
                requireActivity().finish()
            }


            // Drag and drop initialization
            var iconView = binding.iconLayout as ViewGroup
            var topLeftView = binding.topLeftLayout as ViewGroup
            var topRightView = binding.topRightLayout as ViewGroup
            var middleLeftView = binding.middleLeftLayout as ViewGroup
            var middleRightView = binding.middleRightLayout as ViewGroup
            var bottomLeftView = binding.bottomLeftLayout as ViewGroup
            var bottomRightView = binding.bottomRightLayout as ViewGroup

            topLeftView.removeAllViews()
            topRightView.removeAllViews()
            middleLeftView.removeAllViews()
            middleRightView.removeAllViews()
            bottomLeftView.removeAllViews()
            bottomRightView.removeAllViews()

            topLeftView.addView(binding.topLeftLabel)
            topRightView.addView(binding.topRightLabel)
            middleLeftView.addView(binding.middleLeftLabel)
            middleRightView.addView(binding.middleRightLabel)
            bottomLeftView.addView(binding.bottomLeftLabel)
            bottomRightView.addView(binding.bottomRightLabel)


            // get location in database, set to shared preferences
            val db = Firebase.firestore
            val docRef =
                db.collection("users").document(sharedPreference.getString("userId", "").toString())
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
                var source = document.data?.get("source").toString()
                var destination = document.data?.get("destination").toString()

                editor.putString("calendar", calendar)
                editor.putString("motivation", motivation)
                editor.putString("news", news)
                editor.putString("notes", notes)
                editor.putString("traffic", traffic)
                editor.putString("weather", weather)
                editor.putString("notesText", text)
                editor.putString("time", time)
                editor.putString("darkMode", darkMode)
                editor.putString("source", source)
                editor.putString("destination", destination)
                while (!editor.commit()) {
                    Thread.sleep(1000)
                }
                if (isAdded){
                    binding.notesText.setText(sharedPreference.getString("notesText", ""))
                    binding.helloLabel.setText("Hello, " + sharedPreference.getString("first_name", ""))
                    binding.starting.setText(sharedPreference.getString("source", ""))
                    binding.destination.setText(sharedPreference.getString("destination", ""))
                }

                // Load drag and drop preferences
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


                if(isAdded) {
                    if (sharedPreference.getString("calendar", "") == "topLeft") {
                        iconView.removeView(binding.dragCalendar)
                        topLeftView.addView(binding.dragCalendar)
                        updateModule("calendar", 1)
                    } else if (sharedPreference.getString("calendar", "") == "topRight") {
                        iconView.removeView(binding.dragCalendar)
                        topRightView.addView(binding.dragCalendar)
                        updateModule("calendar", 5)
                    } else if (sharedPreference.getString("calendar", "") == "middleLeft") {
                        iconView.removeView(binding.dragCalendar)
                        middleLeftView.addView(binding.dragCalendar)
                        updateModule("calendar", 2)
                    } else if (sharedPreference.getString("calendar", "") == "middleRight") {
                        iconView.removeView(binding.dragCalendar)
                        middleRightView.addView(binding.dragCalendar)
                        updateModule("calendar", 6)
                    } else if (sharedPreference.getString("calendar", "") == "bottomLeft") {
                        iconView.removeView(binding.dragCalendar)
                        bottomLeftView.addView(binding.dragCalendar)
                        updateModule("calendar", 3)
                    } else if (sharedPreference.getString("calendar", "") == "bottomRight") {
                        iconView.removeView(binding.dragCalendar)
                        bottomRightView.addView(binding.dragCalendar)
                        updateModule("calendar", 7)
                    }

                    if (sharedPreference.getString("motivation", "") == "topLeft") {
                        iconView.removeView(binding.dragMotivation)
                        topLeftView.addView(binding.dragMotivation)
                        updateModule("motivation", 1)
                    } else if (sharedPreference.getString("motivation", "") == "topRight") {
                        iconView.removeView(binding.dragMotivation)
                        topRightView.addView(binding.dragMotivation)
                        updateModule("motivation", 5)
                    } else if (sharedPreference.getString("motivation", "") == "middleLeft") {
                        iconView.removeView(binding.dragMotivation)
                        middleLeftView.addView(binding.dragMotivation)
                        updateModule("motivation", 2)
                    } else if (sharedPreference.getString("motivation", "") == "middleRight") {
                        iconView.removeView(binding.dragMotivation)
                        middleRightView.addView(binding.dragMotivation)
                        updateModule("motivation", 6)
                    } else if (sharedPreference.getString("motivation", "") == "bottomLeft") {
                        iconView.removeView(binding.dragMotivation)
                        bottomLeftView.addView(binding.dragMotivation)
                        updateModule("motivation", 3)
                    } else if (sharedPreference.getString("motivation", "") == "bottomRight") {
                        iconView.removeView(binding.dragMotivation)
                        bottomRightView.addView(binding.dragMotivation)
                        updateModule("motivation", 7)
                    }

                    if (sharedPreference.getString("notes", "") == "topLeft") {
                        iconView.removeView(binding.dragNotes)
                        topLeftView.addView(binding.dragNotes)
                        updateModule("notes", 1)
                    } else if (sharedPreference.getString("notes", "") == "topRight") {
                        iconView.removeView(binding.dragNotes)
                        topRightView.addView(binding.dragNotes)
                        updateModule("notes", 5)
                    } else if (sharedPreference.getString("notes", "") == "middleLeft") {
                        iconView.removeView(binding.dragNotes)
                        middleLeftView.addView(binding.dragNotes)
                        updateModule("notes", 2)
                    } else if (sharedPreference.getString("notes", "") == "middleRight") {
                        iconView.removeView(binding.dragNotes)
                        middleRightView.addView(binding.dragNotes)
                        updateModule("notes", 6)
                    } else if (sharedPreference.getString("notes", "") == "bottomLeft") {
                        iconView.removeView(binding.dragNotes)
                        bottomLeftView.addView(binding.dragNotes)
                        updateModule("notes", 3)
                    } else if (sharedPreference.getString("notes", "") == "bottomRight") {
                        iconView.removeView(binding.dragNotes)
                        bottomRightView.addView(binding.dragNotes)
                        updateModule("notes", 7)
                    }
                    if (sharedPreference.getString("news", "") == "topLeft") {
                        iconView.removeView(binding.dragNews)
                        topLeftView.addView(binding.dragNews)
                        updateModule("news", 1)
                    } else if (sharedPreference.getString("news", "") == "topRight") {
                        iconView.removeView(binding.dragNews)
                        topRightView.addView(binding.dragNews)
                        updateModule("news", 5)
                    } else if (sharedPreference.getString("news", "") == "middleLeft") {
                        iconView.removeView(binding.dragNews)
                        middleLeftView.addView(binding.dragNews)
                        updateModule("news", 2)
                    } else if (sharedPreference.getString("news", "") == "middleRight") {
                        iconView.removeView(binding.dragNews)
                        middleRightView.addView(binding.dragNews)
                        updateModule("news", 6)
                    } else if (sharedPreference.getString("news", "") == "bottomLeft") {
                        iconView.removeView(binding.dragNews)
                        bottomLeftView.addView(binding.dragNews)
                        updateModule("news", 3)
                    } else if (sharedPreference.getString("news", "") == "bottomRight") {
                        iconView.removeView(binding.dragNews)
                        bottomRightView.addView(binding.dragNews)
                        updateModule("news", 7)
                    }
                    if (sharedPreference.getString("traffic", "") == "topLeft") {
                        iconView.removeView(binding.dragTraffic)
                        topLeftView.addView(binding.dragTraffic)
                        updateModule("traffic", 1)
                    } else if (sharedPreference.getString("traffic", "") == "topRight") {
                        iconView.removeView(binding.dragTraffic)
                        topRightView.addView(binding.dragTraffic)
                        updateModule("traffic", 5)
                    } else if (sharedPreference.getString("traffic", "") == "middleLeft") {
                        iconView.removeView(binding.dragTraffic)
                        middleLeftView.addView(binding.dragTraffic)
                        updateModule("traffic", 2)
                    } else if (sharedPreference.getString("traffic", "") == "middleRight") {
                        iconView.removeView(binding.dragTraffic)
                        middleRightView.addView(binding.dragTraffic)
                        updateModule("traffic", 6)
                    } else if (sharedPreference.getString("traffic", "") == "bottomLeft") {
                        iconView.removeView(binding.dragTraffic)
                        bottomLeftView.addView(binding.dragTraffic)
                        updateModule("traffic", 3)
                    } else if (sharedPreference.getString("traffic", "") == "bottomRight") {
                        iconView.removeView(binding.dragTraffic)
                        bottomRightView.addView(binding.dragTraffic)
                        updateModule("traffic", 7)
                    }

                    if (sharedPreference.getString("weather", "") == "topLeft") {
                        iconView.removeView(binding.dragWeather)
                        topLeftView.addView(binding.dragWeather)
                        updateModule("weather", 1)
                    } else if (sharedPreference.getString("weather", "") == "topRight") {
                        iconView.removeView(binding.dragWeather)
                        topRightView.addView(binding.dragWeather)
                        updateModule("weather", 5)
                    } else if (sharedPreference.getString("weather", "") == "middleLeft") {
                        iconView.removeView(binding.dragWeather)
                        middleLeftView.addView(binding.dragWeather)
                        updateModule("weather", 2)
                    } else if (sharedPreference.getString("weather", "") == "middleRight") {
                        iconView.removeView(binding.dragWeather)
                        middleRightView.addView(binding.dragWeather)
                        updateModule("weather", 6)
                    } else if (sharedPreference.getString("weather", "") == "bottomLeft") {
                        iconView.removeView(binding.dragWeather)
                        bottomLeftView.addView(binding.dragWeather)
                        updateModule("weather", 3)
                    } else if (sharedPreference.getString("weather", "") == "bottomRight") {
                        iconView.removeView(binding.dragWeather)
                        bottomRightView.addView(binding.dragWeather)
                        updateModule("weather", 7)
                    }
                }

                // Conditions for dragging items
                if (isAdded) {
                    binding.iconLayout.setOnDragListener(dragListener)
                    binding.topLeftLayout.setOnDragListener(dragListener)
                    binding.topRightLayout.setOnDragListener(dragListener)
                    binding.middleLeftLayout.setOnDragListener(dragListener)
                    binding.middleRightLayout.setOnDragListener(dragListener)
                    binding.bottomLeftLayout.setOnDragListener(dragListener)
                    binding.bottomRightLayout.setOnDragListener(dragListener)

                    binding.dragCalendar.setOnLongClickListener{
                        val clipText = "calendar"
                        val item = ClipData.Item(clipText)
                        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                        val data = ClipData(clipText, mimeTypes, item)
                        val dragShadowBuilder = View.DragShadowBuilder(it)
                        it.startDragAndDrop(data, dragShadowBuilder, it, 0)
                        true
                    }
                    binding.dragMotivation.setOnLongClickListener {
                        val clipText = "motivation"
                        val item = ClipData.Item(clipText)
                        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                        val data = ClipData(clipText, mimeTypes, item)
                        val dragShadowBuilder = View.DragShadowBuilder(it)
                        it.startDragAndDrop(data, dragShadowBuilder, it, 0)
                        true
                    }
                    binding.dragNews.setOnLongClickListener {
                        val clipText = "news"
                        val item = ClipData.Item(clipText)
                        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                        val data = ClipData(clipText, mimeTypes, item)
                        val dragShadowBuilder = View.DragShadowBuilder(it)
                        it.startDragAndDrop(data, dragShadowBuilder, it, 0)
                        true
                    }
                    binding.dragNotes.setOnLongClickListener {
                        val clipText = "notes"
                        val item = ClipData.Item(clipText)
                        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                        val data = ClipData(clipText, mimeTypes, item)
                        val dragShadowBuilder = View.DragShadowBuilder(it)
                        it.startDragAndDrop(data, dragShadowBuilder, it, 0)
                        true
                    }
                    binding.dragTraffic.setOnLongClickListener {
                        val clipText = "traffic"
                        val item = ClipData.Item(clipText)
                        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                        val data = ClipData(clipText, mimeTypes, item)
                        val dragShadowBuilder = View.DragShadowBuilder(it)
                        it.startDragAndDrop(data, dragShadowBuilder, it, 0)
                        true
                    }

                    binding.dragWeather.setOnLongClickListener {
                        val clipText = "weather"
                        val item = ClipData.Item(clipText)
                        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                        val data = ClipData(clipText, mimeTypes, item)
                        val dragShadowBuilder = View.DragShadowBuilder(it)
                        it.startDragAndDrop(data, dragShadowBuilder, it, 0)
                        true
                    }

                    // For age and gender in user preferences
                    binding.age1.minValue = 0
                    binding.age1.maxValue = 120
                    binding.age1.wrapSelectorWheel = true
                    binding.age1.value = sharedPreference.getInt("age", 0)
                    val ageRef = database.getReference("user/age")
                    val genderRef = database.getReference("user/gender")
                    binding.age1.setOnValueChangedListener { picker, oldVal, newVal ->
                        editor.putInt("age", newVal)
                        editor.commit()
                        ageRef.setValue(newVal)
                        docRef.update("age", newVal)
                    }

                    if (sharedPreference.getInt("gender", 0) == 0) {
                        binding.male.isChecked = true
                    } else if (sharedPreference.getInt("gender", 0) == 1) {
                        binding.female.isChecked = true
                    } else if (sharedPreference.getInt("gender", 0) == 2) {
                        binding.nonbinary.isChecked = true
                    }
                    binding.gender1.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
                        val radio: RadioButton = group.findViewById(checkedId)
                        if (radio.text.toString() == "Male") {
                            editor.putInt("gender", 0)
                            genderRef.setValue(0)
                            docRef.update("gender", 0)
                        } else if (radio.text.toString() == "Female") {
                            editor.putInt("gender", 1)
                            genderRef.setValue(1)
                            docRef.update("gender", 1)
                        } else if (radio.text.toString() == "Other") {
                            editor.putInt("gender", 2)
                            genderRef.setValue(2)
                            docRef.update("gender", 2)
                        }
                        editor.commit()
                    })

                    // Update traffic
                    val sourceRef = database.getReference("modules/traffic/source")
                    val destRef = database.getReference("modules/traffic/destination")
                    binding.update.setOnClickListener{
                        var tempSource = binding.starting.text.toString()
                        var tempDest = binding.destination.text.toString()
                        editor.putString("source", tempSource)
                        editor.putString("destination", tempDest)
                        sourceRef.setValue(tempSource)
                        destRef.setValue(tempDest)
                        docRef.update("source", tempSource)
                        docRef.update("destination", tempDest)
                        editor.commit()
                    }

                    //      Notes
                    val notesRef = database.getReference("modules/notes/text")
                    binding.notesText.addTextChangedListener(object : TextWatcher {
                        var textBeforeEdit = ""
                        override fun afterTextChanged(s: Editable) {
                            val str = s.toString()
                            val spanStart = s.getSpanStart(1)
                            val spanEnd = s.getSpanEnd(1)
                            var lastNL = str.lastIndexOf('\n', spanStart)
                            var nextNL: Int
                            while (lastNL < spanEnd) {
                                nextNL = str.indexOf('\n', lastNL + 1)
                                if (nextNL == -1) nextNL = str.length
                                if (nextNL - lastNL > 20 + 1) {
                                    s.replace(spanStart, spanEnd, textBeforeEdit)
                                    break
                                }
                                lastNL = nextNL
                            }
                            s.removeSpan(1)

                        }

                        override fun beforeTextChanged(
                            s: CharSequence,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                            textBeforeEdit = s.toString().substring(start, start + count)
                        }

                        override fun onTextChanged(
                            s: CharSequence,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            binding.notesText.getText()
                                .setSpan(1, start, start + count, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            var newLineChar = "\n"
                            val editTextRowCount: Int =
                                binding.notesText.getText().count { newLineChar.contains(it) }
                            if (editTextRowCount > 9) {
                                val lastBreakIndex = binding.notesText.getText().lastIndexOf("\n")
                                val newText =
                                    binding.notesText.getText().toString()
                                        .substring(0, lastBreakIndex)
                                binding.notesText.setText("")
                                binding.notesText.append(newText)
                            }
                            notesRef.setValue(binding.notesText.text.toString())
                        }
                    })

                    if (sharedPreference.getString("darkMode", "") == "false") {
                        binding.darkModeSwitch.isChecked = false
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    } else {
                        binding.darkModeSwitch.isChecked = true
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    binding.darkModeSwitch.setOnClickListener() {
                        if (binding.darkModeSwitch.isChecked) {
                            docRef.update("darkMode", "true").addOnSuccessListener {
                                Log.d("Success", "success")
                            }.addOnFailureListener {
                                Log.d("Fail", "fail")
                            }
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        } else {
                            docRef.update("darkMode", "false").addOnSuccessListener {
                                Log.d("Success", "success")
                            }.addOnFailureListener {
                                Log.d("Fail", "fail")
                            }
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                    }

                    //Toggle Time
                    val timeRef = database.getReference("modules/time/disabled")
                    if (sharedPreference.getString("time", "") == "false") {
                        binding.toggleTimeSwitch.isChecked = false
                        timeRef.setValue(true)
                        docRef.update("time", "false")

                    } else {
                        binding.toggleTimeSwitch.isChecked = true
                        timeRef.setValue(false)
                        docRef.update("time", "true")
                    }
                    binding.toggleTimeSwitch.setOnClickListener() {
                        if (binding.toggleTimeSwitch.isChecked) {
                            timeRef.setValue(false)
                            docRef.update("time", "true")
                        } else {
                            timeRef.setValue(true)
                            docRef.update("time", "false")
                        }
                    }
                    // Log out
                    binding.logOut.setOnClickListener() {
                        val nameRef = database.getReference("user/name")
                        editor.putString("first_name", "")
                        editor.commit()
                        updateModule("calendar", -1)
                        updateModule("motivation", -1)
                        updateModule("news", -1)
                        updateModule("notes", -1)
                        updateModule("traffic", -1)
                        updateModule("weather", -1)
                        nameRef.setValue("")
                        requireActivity().finish()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
//                        Runtime.getRuntime().exit(0)
                    }

                    //Back to settings with updating notes
                    binding.backToSettings.setOnClickListener {
                        binding.instructionsLabel.visibility = View.GONE
                        binding.masterLayout.visibility = View.GONE
                        binding.notesText.visibility = View.GONE
                        binding.toggleTimeLabel.visibility = View.GONE
                        binding.toggleTimeSwitch.visibility = View.GONE
                        binding.darkModeLabel.visibility = View.GONE
                        binding.darkModeSwitch.visibility = View.GONE
                        binding.customizeBtn.visibility = View.VISIBLE
                        binding.notesBtn.visibility = View.VISIBLE
                        binding.preferencesBtn.visibility = View.VISIBLE
                        binding.logoutLayout.visibility = View.GONE
                        binding.backToSettingsLayout.visibility = View.GONE
                        binding.backToScanLayout.visibility = View.VISIBLE
                        binding.logoutLayout.visibility = View.GONE
                        binding.ageGenderLayout.visibility = View.GONE
                        binding.startingLabel.visibility = View.GONE
                        binding.starting.visibility = View.GONE
                        binding.destinationLabel.visibility = View.GONE
                        binding.destination.visibility = View.GONE
                        binding.update.visibility = View.GONE
                        docRef.update("text", binding.notesText.text.toString())
                    }
                }

            }.addOnFailureListener { exception ->
                Log.d("Error", "get failed with ", exception)
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.mirrormirror

import android.R.attr
import android.app.Activity
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.mirrormirror.databinding.FragmentFirstBinding
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.core.view.get
import androidx.core.view.isVisible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.log
import android.R.attr.delay





/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    val database = Firebase.database
    var dataReference = database.getReference()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        val sharedPreference = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        var iconView = binding.iconLayout as ViewGroup
        var topLeftView = binding.topLeftLayout as ViewGroup
        var topRightView = binding.topRightLayout as ViewGroup
        var middleLeftView = binding.middleLeftLayout as ViewGroup
        var middleRightView = binding.middleRightLayout as ViewGroup
        var bottomLeftView = binding.bottomLeftLayout as ViewGroup
        var bottomRightView = binding.bottomRightLayout as ViewGroup

//        iconView.removeAllViews()
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


        if (sharedPreference.getString("calendar", "") == "") {
            updateModule("calendar", -1)
        }
        if (sharedPreference.getString("motivation", "") == "") {
            updateModule("motivation", -1)
        }
        if (sharedPreference.getString("notes", "") == "") {
            updateModule("notes", -1)
        }
        if (sharedPreference.getString("news", "") == "") {
            updateModule("news", -1)
        }
        if (sharedPreference.getString("traffic", "") == "") {
            updateModule("traffic", -1)
        }
        if (sharedPreference.getString("weather", "") == "") {
            updateModule("weather", -1)
        }


        if (sharedPreference.getString("calendar", "") == "topLeft") {
            iconView.removeView(binding.dragCalendar)
            topLeftView.addView(binding.dragCalendar)
        } else if (sharedPreference.getString("calendar", "") == "topRight"){
            iconView.removeView(binding.dragCalendar)
            topRightView.addView(binding.dragCalendar)
        } else if (sharedPreference.getString("calendar", "") == "middleLeft"){
            iconView.removeView(binding.dragCalendar)
            middleLeftView.addView(binding.dragCalendar)
        } else if (sharedPreference.getString("calendar", "") == "middleRight"){
            iconView.removeView(binding.dragCalendar)
            middleRightView.addView(binding.dragCalendar)
        } else if (sharedPreference.getString("calendar", "") == "bottomLeft"){
            iconView.removeView(binding.dragCalendar)
            bottomLeftView.addView(binding.dragCalendar)
        } else if (sharedPreference.getString("calendar", "") == "bottomRight"){
            iconView.removeView(binding.dragCalendar)
            bottomRightView.addView(binding.dragCalendar)
        }

        if (sharedPreference.getString("motivation", "") == "topLeft") {
            iconView.removeView(binding.dragMotivation)
            topLeftView.addView(binding.dragMotivation)
        } else if (sharedPreference.getString("motivation", "") == "topRight"){
            iconView.removeView(binding.dragMotivation)
            topRightView.addView(binding.dragMotivation)
        } else if (sharedPreference.getString("motivation", "") == "middleLeft"){
            iconView.removeView(binding.dragMotivation)
            middleLeftView.addView(binding.dragMotivation)
        } else if (sharedPreference.getString("motivation", "") == "middleRight"){
            iconView.removeView(binding.dragMotivation)
            middleRightView.addView(binding.dragMotivation)
        } else if (sharedPreference.getString("motivation", "") == "bottomLeft"){
            iconView.removeView(binding.dragMotivation)
            bottomLeftView.addView(binding.dragMotivation)
        } else if (sharedPreference.getString("motivation", "") == "bottomRight"){
            iconView.removeView(binding.dragMotivation)
            bottomRightView.addView(binding.dragMotivation)
        }

        if (sharedPreference.getString("notes", "") == "topLeft") {
            iconView.removeView(binding.dragNotes)
            topLeftView.addView(binding.dragNotes)
        } else if (sharedPreference.getString("notes", "") == "topRight"){
            iconView.removeView(binding.dragNotes)
            topRightView.addView(binding.dragNotes)
        } else if (sharedPreference.getString("notes", "") == "middleLeft"){
            iconView.removeView(binding.dragNotes)
            middleLeftView.addView(binding.dragNotes)
        } else if (sharedPreference.getString("notes", "") == "middleRight"){
            iconView.removeView(binding.dragNotes)
            middleRightView.addView(binding.dragNotes)
        } else if (sharedPreference.getString("notes", "") == "bottomLeft"){
            iconView.removeView(binding.dragNotes)
            bottomLeftView.addView(binding.dragNotes)
        } else if (sharedPreference.getString("notes", "") == "bottomRight"){
            iconView.removeView(binding.dragNotes)
            bottomRightView.addView(binding.dragNotes)
        }
        if (sharedPreference.getString("news", "") == "topLeft") {
            iconView.removeView(binding.dragNews)
            topLeftView.addView(binding.dragNews)
        } else if (sharedPreference.getString("news", "") == "topRight"){
            iconView.removeView(binding.dragNews)
            topRightView.addView(binding.dragNews)
        } else if (sharedPreference.getString("news", "") == "middleLeft"){
            iconView.removeView(binding.dragNews)
            middleLeftView.addView(binding.dragNews)
        } else if (sharedPreference.getString("news", "") == "middleRight"){
            iconView.removeView(binding.dragNews)
            middleRightView.addView(binding.dragNews)
        } else if (sharedPreference.getString("news", "") == "bottomLeft"){
            iconView.removeView(binding.dragNews)
            bottomLeftView.addView(binding.dragNews)
        } else if (sharedPreference.getString("news", "") == "bottomRight"){
            iconView.removeView(binding.dragNews)
            bottomRightView.addView(binding.dragNews)
        }
        if (sharedPreference.getString("traffic", "") == "topLeft") {
            iconView.removeView(binding.dragTraffic)
            topLeftView.addView(binding.dragTraffic)
        } else if (sharedPreference.getString("traffic", "") == "topRight"){
            iconView.removeView(binding.dragTraffic)
            topRightView.addView(binding.dragTraffic)
        } else if (sharedPreference.getString("traffic", "") == "middleLeft"){
            iconView.removeView(binding.dragTraffic)
            middleLeftView.addView(binding.dragTraffic)
        } else if (sharedPreference.getString("traffic", "") == "middleRight"){
            iconView.removeView(binding.dragTraffic)
            middleRightView.addView(binding.dragTraffic)
        } else if (sharedPreference.getString("traffic", "") == "bottomLeft"){
            iconView.removeView(binding.dragTraffic)
            bottomLeftView.addView(binding.dragTraffic)
        } else if (sharedPreference.getString("traffic", "") == "bottomRight"){
            iconView.removeView(binding.dragTraffic)
            bottomRightView.addView(binding.dragTraffic)
        }

        if (sharedPreference.getString("weather", "") == "topLeft") {
            iconView.removeView(binding.dragWeather)
            topLeftView.addView(binding.dragWeather)
        } else if (sharedPreference.getString("weather", "") == "topRight"){
            iconView.removeView(binding.dragWeather)
            topRightView.addView(binding.dragWeather)
        } else if (sharedPreference.getString("weather", "") == "middleLeft"){
            iconView.removeView(binding.dragWeather)
            middleLeftView.addView(binding.dragWeather)
        } else if (sharedPreference.getString("weather", "") == "middleRight"){
            iconView.removeView(binding.dragWeather)
            middleRightView.addView(binding.dragWeather)
        } else if (sharedPreference.getString("weather", "") == "bottomLeft"){
            iconView.removeView(binding.dragWeather)
            bottomLeftView.addView(binding.dragWeather)
        } else if (sharedPreference.getString("weather", "") == "bottomRight"){
            iconView.removeView(binding.dragWeather)
            bottomRightView.addView(binding.dragWeather)
        }


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
            it.startDragAndDrop(data,dragShadowBuilder,it,0)
            true
        }
        binding.dragMotivation.setOnLongClickListener{
            val clipText = "motivation"
            val item = ClipData.Item(clipText)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clipText, mimeTypes, item)
            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data,dragShadowBuilder,it,0)
            true
        }
        binding.dragNews.setOnLongClickListener{
            val clipText = "news"
            val item = ClipData.Item(clipText)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clipText, mimeTypes, item)
            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data,dragShadowBuilder,it,0)
            true
        }
        binding.dragNotes.setOnLongClickListener{
            val clipText = "notes"
            val item = ClipData.Item(clipText)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clipText, mimeTypes, item)
            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data,dragShadowBuilder,it,0)
            true
        }
        binding.dragTraffic.setOnLongClickListener{
            val clipText = "traffic"
            val item = ClipData.Item(clipText)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clipText, mimeTypes, item)
            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data,dragShadowBuilder,it,0)
            true
        }

        binding.dragWeather.setOnLongClickListener{
            val clipText = "weather"
            val item = ClipData.Item(clipText)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clipText, mimeTypes, item)
            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data,dragShadowBuilder,it,0)
            true
        }
        return binding.root
    }
    val dragListener = View.OnDragListener{ view, event ->
        when(event.action){
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DRAG_EXITED ->{
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val sharedPreference = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                Log.d("Event", event.toString())
                Log.d("view", view.toString())
                val item = event.clipData.getItemAt(0)
                val dragData = item.text.toString()
                view.invalidate()
                val v = event.localState as View
                val owner = v.parent as ViewGroup
                val destination = view as LinearLayout

                if (destination.childCount < 2){
                    var destTag = destination.getTag().toString()
                    var itemTag = v.getTag().toString()
                    editor.putString(itemTag, destTag)
                    editor.commit()
                    owner.removeView(v)
                    destination.addView(v)
                    var dest = destination.getTag()

                    if (dest == "iconTray"){
                        updateModule(dragData, -1)
                    }else if (dest == "topLeft"){
                        updateModule(dragData, 1)
                    }else if (dest == "topRight"){
                        updateModule(dragData, 5)
                    }else if (dest == "middleLeft") {
                        updateModule(dragData, 2)
                    }else if (dest == "middleRight") {
                        updateModule(dragData, 6)
                    }else if (dest == "bottomLeft") {
                        updateModule(dragData, 3)
                    }else if (dest == "bottomRight") {
                        updateModule(dragData, 7)
                    }

                } else if (destination.getTag() == "iconTray"){
                    var destTag = destination.getTag().toString()
                    var itemTag = v.getTag().toString()
                    editor.putString(itemTag, destTag)
                    editor.commit()
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

    fun updateModule(module: String, location: Int){
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

        if (location == -1){
            if (module == "calendar"){
                calRef.setValue(true)
            }else if (module == "motivation"){
                motivRef.setValue(true)
            }else if (module == "news"){
                newsRef.setValue(true)
            }else if (module == "notes"){
                notesRef.setValue(true)
            }else if (module == "traffic"){
                trafficRef.setValue(true)
            }else if (module == "weather"){
                weatherRef.setValue(true)
            }
        } else {
            if (module == "calendar"){
                calRef.setValue(false)
                calLocRef.setValue(location)
            }else if (module == "motivation"){
                motivRef.setValue(false)
                motivLocRef.setValue(location)
            }else if (module == "news"){
                newsRef.setValue(false)
                newsLocRef.setValue(location)
            }else if (module == "notes"){
                notesRef.setValue(false)
                notesLocRef.setValue(location)
            }else if (module == "traffic"){
                trafficRef.setValue(false)
                trafficLocRef.setValue(location)
            }else if (module == "weather"){
                weatherRef.setValue(false)
                weatherLocRef.setValue(location)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreference = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

//      Notes
        binding.notesText.setText(sharedPreference.getString("notesText", ""))
        val notesRef = database.getReference("modules/notes/text")
        binding.notesText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                notesRef.setValue(binding.notesText.text.toString())
                editor.putString("notesText", binding.notesText.text.toString())
                editor.commit()
            }
        })

        binding.helloLabel.setText("Hello, " + sharedPreference.getString("first_name", ""))
        if (sharedPreference.getInt("dark_mode", 0) == 0) {
            binding.darkModeSwitch.isChecked = false
        }else{
            binding.darkModeSwitch.isChecked = true
        }
        binding.darkModeSwitch.setOnClickListener(){
            if (binding.darkModeSwitch.isChecked){
                editor.putInt("dark_mode", 1)
                editor.commit()
                triggerRestart(requireActivity())
            }else{
                editor.putInt("dark_mode", 0)
                editor.commit()
                triggerRestart(requireActivity())
            }
        }

        //Toggle Time
        val timeRef = database.getReference("modules/time/disabled")
        dataReference.child("modules/time/disabled")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val timeRef = dataSnapshot.getValue()
                    if ( timeRef == false){
                        binding.toggleTimeSwitch.isChecked = true
                    }else{
                        binding.toggleTimeSwitch.isChecked = false
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    //This doesn't do anything but will cause errors if you delete it
                }
            })

        binding.toggleTimeSwitch.setOnClickListener(){
            if (binding.toggleTimeSwitch.isChecked){
                timeRef.setValue(false)
            }else{
                timeRef.setValue(true)
            }
        }


        binding.logOut.setOnClickListener(){
            editor.putString("first_name", "")
            editor.commit()
            triggerRestart(requireActivity())
        }
    }
    fun triggerRestart(context: Activity) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            (context as Activity).finish()
        }
        Runtime.getRuntime().exit(0)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
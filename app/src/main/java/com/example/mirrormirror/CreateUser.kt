package com.example.mirrormirror

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        val alreadyUser = findViewById<Button>(R.id.alreadyUser)
        val signIn = findViewById<Button>(R.id.signIn)
        val createAccountBtn = findViewById<Button>(R.id.createAccountButton)
        val createUserLabel = findViewById<TextView>(R.id.createUserLabel)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val nameLayout = findViewById<LinearLayout>(R.id.nameLayout)
        val ageGenderLayout = findViewById<LinearLayout>(R.id.ageGenderLayout)
        val createBackBtn = findViewById<Button>(R.id.createBackBtn)
        val signInBackBtn = findViewById<Button>(R.id.signInBackBtn)
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
        var firstName = ""
        var lastName = ""
        var emailAddress = ""
        var userPassword = ""
        var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        var passPattern =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#^@$!%*?*&+=])[A-Za-z\\d@\$!%*?&#^=+]{6,}\$"
        val database = Firebase.database
        var auth: FirebaseAuth
        auth = Firebase.auth
        val db = Firebase.firestore

        createAccountBtn.setOnClickListener {
            errorMsg.setText("")
            createUserLabel.setText("Create User")
            alreadyUser.visibility = View.INVISIBLE
            createAccountBtn.visibility = View.INVISIBLE
            signIn.visibility = View.INVISIBLE
            email.visibility = View.VISIBLE
            password.visibility = View.VISIBLE
            nameLayout.visibility = View.VISIBLE
            ageGenderLayout.visibility = View.VISIBLE
            createBtn.visibility = View.VISIBLE
            createBackBtn.visibility = View.VISIBLE
            signIn.visibility = View.INVISIBLE
        }

        createBackBtn.setOnClickListener {
            errorMsg.setText("")
            createUserLabel.setText("Welcome")
            alreadyUser.visibility = View.VISIBLE
            createAccountBtn.visibility = View.VISIBLE
            email.visibility = View.GONE
            password.visibility = View.GONE
            nameLayout.visibility = View.GONE
            ageGenderLayout.visibility = View.GONE
            createBtn.visibility = View.GONE
            createBackBtn.visibility = View.GONE
            signIn.visibility = View.GONE
        }

        alreadyUser.setOnClickListener {
            errorMsg.setText("")
            createUserLabel.setText("Sign In")
            email.visibility = View.VISIBLE
            password.visibility = View.VISIBLE
            nameLayout.visibility = View.GONE
            ageGenderLayout.visibility = View.GONE
            alreadyUser.visibility = View.GONE
            createAccountBtn.visibility = View.GONE
            signIn.visibility = View.VISIBLE
            signInBackBtn.visibility = View.VISIBLE
        }
        signInBackBtn.setOnClickListener {
            errorMsg.setText("")
            createUserLabel.setText("Welcome")
            alreadyUser.visibility = View.VISIBLE
            createAccountBtn.visibility = View.VISIBLE
            email.visibility = View.GONE
            password.visibility = View.GONE
            nameLayout.visibility = View.GONE
            ageGenderLayout.visibility = View.GONE
            createBtn.visibility = View.GONE
            createBackBtn.visibility = View.GONE
            signIn.visibility = View.GONE
            signInBackBtn.visibility = View.GONE
        }

        if (agePick != null) {
            agePick.minValue = 0
            agePick.maxValue = 120
            agePick.wrapSelectorWheel = true
            agePick.setOnValueChangedListener { picker, oldVal, newVal ->
                age = newVal
            }
        }

        signIn.setOnClickListener {
            errorMsg.setText("")
            if (email.text.isEmpty()) {
                errorMsg.setText("Please enter your email address.")
            } else if (!email.text.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {
                errorMsg.setText("Invalid email address. Please try again.")
            } else if (password.text.isEmpty()) {
                errorMsg.setText("Please enter a password.")
            } else {
                val resultIntent = Intent()
                emailAddress = email.text.toString()
                userPassword = password.text.toString()
                auth.signInWithEmailAndPassword(emailAddress, userPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Success", "signInWithEmail:success")
                            val user = auth.currentUser
                            val uid = user?.uid.toString()
                            val docRef = db.collection("users").document(uid)
                            //Get existing name and user id
                            docRef.get().addOnSuccessListener { document ->
                                if (document != null) {
                                    firstName = document.data?.get("fname").toString()
                                    resultIntent.putExtra("firstName", firstName)
                                    resultIntent.putExtra("userId", uid)
                                    setResult(Activity.RESULT_OK, resultIntent)
                                    finish()
                                } else {
                                    Log.d("Error", "No such document")
                                }
                            }.addOnFailureListener { exception ->
                                Log.d("Error", "get failed with ", exception)
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Error", "signInWithEmail:failure", task.exception)
                            errorMsg.setText("Email address or password is incorrect. Please try again.")
                        }
                    }
            }
        }
        createBtn.setOnClickListener {
            errorMsg.setText("")
            if (email.text.isEmpty()) {
                errorMsg.setText("Please enter your email address.")
            } else if (!email.text.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {
                errorMsg.setText("Invalid email address. Please try again.")
            } else if (password.text.isEmpty()) {
                errorMsg.setText("Please enter a password.")
            } else if (!password.text.toString().trim { it <= ' ' }
                    .matches(passPattern.toRegex())) {
                errorMsg.setText("Password must contain at least 6 characters, an upper and lower case letter, a number, and a special character.")
            } else if (fname.text.isEmpty()) {
                errorMsg.setText("Please enter your first name.")
            } else if (lname.text.isEmpty()) {
                errorMsg.setText("Please enter your last name.")
            } else if (radioGroup.checkedRadioButtonId == -1) {
                errorMsg.setText("Please enter your gender.")
            } else {
                if (male.isChecked) {
                    genderVar = 0
                } else if (female.isChecked) {
                    genderVar = 1
                } else if (nonbinary.isChecked) {
                    genderVar = 2
                }

                val resultIntent = Intent()
                firstName = fname.text.toString()
                lastName = lname.text.toString()
                emailAddress = email.text.toString()
                userPassword = password.text.toString()
                resultIntent.putExtra("firstName", firstName)
                auth.createUserWithEmailAndPassword(emailAddress, userPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Success", "createUserWithEmail:success")
                            val user = auth.currentUser
                            val uid = user?.uid.toString()
                            // Create user in data base with the following values
                            val userData = hashMapOf(
                                "fname" to firstName,
                                "lname" to lastName,
                                "emailAddress" to emailAddress,
                                "age" to age,
                                "gender" to genderVar,
                                "uid" to uid,
                                "calendar" to "",
                                "motivation" to "",
                                "news" to "",
                                "notes" to "",
                                "traffic" to "",
                                "weather" to "",
                                "text" to "Hello " + firstName + "!\nThank you for using\nMirror² \uD83D\uDE04",
                                "time" to "true",
                                "darkMode" to "false",
                                "source" to "1600 Pennsylvania Avenue NW, Washington, DC 20500",
                                "destination" to "First St SE, Washington, DC 20004",
                                "arrivalHour" to 8,
                                "arrivalMin" to 0
                            )
                            db.collection("users").document(uid).set(userData)
                                .addOnSuccessListener {
                                    Log.d("Success", "DocumentSnapshot successfully written!")
                                    val ageRef = database.getReference("user/age/")
                                    val genderRef = database.getReference("user/gender/")
                                    ageRef.setValue(age)
                                    genderRef.setValue(genderVar)
                                }.addOnFailureListener { e ->
                                    Log.w("Error", "Error writing document", e)
                                }
                            resultIntent.putExtra("userId", uid)
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Error", "createUserWithEmail:failure", task.exception)
                            errorMsg.setText("Email address is already in use or invalid.")
                        }
                    }
            }
        }
    }

    override fun onBackPressed() {
//        Do nothing
    }
}
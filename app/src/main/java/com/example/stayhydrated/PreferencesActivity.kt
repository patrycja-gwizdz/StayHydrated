package com.example.stayhydrated

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PreferencesActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferences)
        val spinnerWaterGoal = findViewById<Spinner>(R.id.spinnerWaterGoal)
        val waterGoals = resources.getStringArray(R.array.water_goals)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, waterGoals)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWaterGoal.adapter = adapter

        val buttonSetWaterGoal = findViewById<Button>(R.id.buttonSetWaterGoal)
        buttonSetWaterGoal.setOnClickListener {
            val selectedWaterGoal = spinnerWaterGoal.selectedItem.toString()
            val waterGoal = selectedWaterGoal.split(" ")[0].toFloat()

            val sharedPreferences = getSharedPreferences("stayHydratedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putFloat("waterGoal", waterGoal)
            editor.apply()
            Toast.makeText(this, "Twój cel to $waterGoal litrów wody", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

}
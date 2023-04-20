package com.example.stayhydrated
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stayhydrated.MainActivity

class PreferencesActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferences)

        val spinnerWaterGoal = findViewById<Spinner>(R.id.spinnerWaterGoal)
        val waterGoals = resources.getStringArray(R.array.water_goals)
        val waterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, waterGoals)
        waterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWaterGoal.adapter = waterAdapter

        val spinnerWaterAmount = findViewById<Spinner>(R.id.spinnerWater)
        val waterAmounts = resources.getStringArray(R.array.water)
        val waterAmountAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, waterAmounts)
        waterAmountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWaterAmount.adapter = waterAmountAdapter

        val spinnerAlarm = findViewById<Spinner>(R.id.spinnerAlarm)
        val alarms = resources.getStringArray(R.array.alarm)
        val alarmAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, alarms)
        alarmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAlarm.adapter = alarmAdapter

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
        val buttonSetWater = findViewById<Button>(R.id.buttonSetWater)
        buttonSetWater.setOnClickListener {
            val sharedPreferences = getSharedPreferences("stayHydratedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val selectedWaterAmount = spinnerWaterAmount.selectedItem.toString()
            val waterAmount = selectedWaterAmount.split(" ml")[0].toInt()
            editor.putInt("waterml", waterAmount)
            editor.apply()
            Toast.makeText(this, "Twoja ilość wody to $waterAmount ml", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonSetAlarm = findViewById<Button>(R.id.buttonSetAlarm)
        buttonSetAlarm.setOnClickListener {
            val sharedPreferences = getSharedPreferences("stayHydratedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val selectedAlarm = spinnerAlarm.selectedItem.toString()
            val alarmTime = selectedAlarm.split(" minut")[0].toInt()
            editor.putInt("alarmTime", alarmTime)
            editor.apply()
            Toast.makeText(this, "Częstotliwość alarmu to $alarmTime minut", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }



    }
}

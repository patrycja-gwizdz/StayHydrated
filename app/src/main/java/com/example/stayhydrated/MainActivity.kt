package com.example.stayhydrated

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import java.util.concurrent.TimeUnit
class MainActivity : AppCompatActivity() {

    private lateinit var addButton: Button
    private lateinit var waterDrankTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var waterGoalInfoTextView: TextView
    private lateinit var preferencesButton: Button
    private lateinit var waterGoalTextView: TextView
    private var waterDrank = 0.0f
    private var waterGoal = 0.0f
    private lateinit var alarmManager: AlarmManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        addButton = findViewById(R.id.add_button)
        waterDrankTextView = findViewById(R.id.water_drank_text_view)
        progressBar = findViewById(R.id.progressBar)
        waterGoalInfoTextView = findViewById(R.id.water_goal_info_text_view)
        preferencesButton = findViewById(R.id.preferences_button)
        waterGoalTextView = findViewById(R.id.waterGoalTextView)

        // Pobranie wartości z SharedPreferences
        val sharedPreferences = getSharedPreferences("stayHydratedPrefs", Context.MODE_PRIVATE)
        waterGoal = sharedPreferences.getFloat("waterGoal", 0.0f)
        waterDrank = sharedPreferences.getFloat("waterDrank", 0.0f)

        // Wyświetlenie informacji o celu picia wody
        waterGoalTextView.text = "Twój cel picia wody na dziś to: $waterGoal litra"

        // Obliczenie procentowego postępu i aktualizacja ProgressBar oraz TextView
        val progressPercent = ((waterDrank / waterGoal) * 100).toInt()
        progressBar.progress = progressPercent
        waterDrankTextView.text = "Wypito już: ${waterDrank}l"
        waterGoalInfoTextView.text = "Zostało do celu: ${(waterGoal - waterDrank)}l"

        // Dodanie akcji dla przycisku dodającego wodę
        addButton.setOnClickListener {

            // pobranie wartości z pliku SharedPreferences
            val sharedPreferences = getSharedPreferences("stayHydratedPrefs", Context.MODE_PRIVATE)
            val savedDate = sharedPreferences.getLong("lastDate", 0L)
            val currentDate = System.currentTimeMillis()
            var waterDrank = sharedPreferences.getFloat("waterDrank", 0.0f)
            val waterGoal = sharedPreferences.getFloat("waterGoal", 0.0f)

            // sprawdzenie, czy to jest nowy dzień
            if (currentDate - savedDate >= TimeUnit.DAYS.toMillis(1)) {
                // resetowanie ilości wypitej wody do zera
                waterDrank = 0.0f
                // zapisanie daty do SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putLong("lastDate", currentDate)
                editor.apply()
            }

            // dodanie 0.25 litra do ilości wypitej wody
            waterDrank += 0.25f

            // zapisanie aktualnej ilości wypitej wody do SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putFloat("waterDrank", waterDrank)
            editor.apply()

            // obliczenie procentowego postępu
            val progressPercent = ((waterDrank / waterGoal) * 100).toInt()

            // aktualizacja widoków w ProgressBar i TextView
            progressBar.progress = progressPercent
            waterDrankTextView.text = "Wypito już: ${waterDrank}l"
            waterGoalInfoTextView.text = "Zostało do celu: ${(waterGoal - waterDrank)}l"
        }

        // Dodanie akcji dla przycisku ustawień
        preferencesButton.setOnClickListener {
            val intent = Intent(this, PreferencesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // pobranie daty ostatniego zapisu
        val sharedPreferences = getSharedPreferences("stayHydratedPrefs", Context.MODE_PRIVATE)
        val savedDate = sharedPreferences.getLong("lastDate", 0L)
        val currentDate = System.currentTimeMillis()

        // jeśli ostatni zapis był w poprzednim dniu, zresetuj wartości wypitej wody
        if (currentDate - savedDate > TimeUnit.DAYS.toMillis(1)) {
            val editor = sharedPreferences.edit()
            editor.putFloat("waterDrank", 0.0f)
            editor.putLong("lastDate", currentDate)
            editor.apply()

            // ustaw wartości w widokach
            waterDrankTextView.text = "Wypiłeś już: 0.0l"
            progressBar.progress = 0
            waterGoalInfoTextView.text = "Zostało do celu: ${waterGoal}l"
        }
    }


}
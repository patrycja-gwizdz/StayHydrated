package com.example.stayhydrated

import com.example.stayhydrated.NotificationActivity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {




    private lateinit var addButton: Button
    private lateinit var waterDrankTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var waterGoalInfoTextView: TextView
    private lateinit var preferencesButton: Button
    private lateinit var waterGoalTextView: TextView
    private lateinit var waterDrankPercentage: TextView

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var calendar: Calendar

    private var alarmTime = 0
    private var waterAmount = 0
    private var waterDrank = 0.0f
    private var waterGoal = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        addButton = findViewById(R.id.add_button)
        waterDrankTextView = findViewById(R.id.water_drank_text_view)
        progressBar = findViewById(R.id.progressBar)
        waterGoalInfoTextView = findViewById(R.id.water_goal_info_text_view)
        preferencesButton = findViewById(R.id.preferences_button)
        waterGoalTextView = findViewById(R.id.waterGoalTextView)
        waterDrankPercentage = findViewById(R.id.water_drank_percentage)

        // Pobranie wartości z SharedPreferences
        val sharedPreferences = getSharedPreferences("stayHydratedPrefs", Context.MODE_PRIVATE)
        waterGoal = sharedPreferences.getFloat("waterGoal", 0.0f)
        waterDrank = sharedPreferences.getFloat("waterDrank", 0.0f)
        waterAmount = sharedPreferences.getInt("waterml",0)
        alarmTime = sharedPreferences.getInt("alarmTime",0)


        println(waterGoal)
        // Wyświetlenie informacji o celu picia wody
        if ((waterGoal != 0.0f)&&(waterAmount != 0)&&(alarmTime != 0))
        {
                waterGoalTextView.text = "Twój cel picia wody na dziś to: $waterGoal litra"
                // Obliczenie procentowego postępu i aktualizacja ProgressBar oraz TextView
                val progressPercent = ((waterDrank / waterGoal) * 100).toInt()
                progressBar.progress = progressPercent
                waterDrankTextView.text = "Wypito już: ${String.format("%.1f", (waterDrank))}l"
                waterGoalInfoTextView.text = "Zostało do celu: ${String.format("%.1f", (waterGoal - waterDrank))}l"
                waterDrankPercentage.text = "$progressPercent%"
                addButton.setText("Wypito ${waterAmount}ml")

                if (progressPercent > 99) {
                    val imageOrder = findViewById<ImageView>(R.id.imageView3)
                    imageOrder.visibility = View.VISIBLE
                    waterGoalInfoTextView.text = "Osiągnięto dzienny cel!"

                }


                // Dodanie akcji dla przycisku dodającego wodę
                addButton.setOnClickListener {
                    addWater()

                }
        }
        else{
            progressBar.visibility = View.GONE
            addButton.visibility = View.GONE
            waterGoalTextView.text = "Nie ustawiono celu lub ilości wody lub interwału powiadomień. Aby korzystać z aplikacji, to najpierw ustaw wszystkie potrzebne funkcje!"


        }



        val bckButton = findViewById<Button>(R.id.buttonNoti)

        bckButton.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }



        // Dodanie akcji dla przycisku ustawień
        preferencesButton.setOnClickListener {
            val intent = Intent(this, PreferencesActivity::class.java)
            startActivity(intent)
        }
    }

        private fun createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name: CharSequence = "stayhydratedReminderChannel"
                val description = "Channel for Alarm Manager"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel("stayhydrated", name, importance)
                channel.description = description

                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)

        }



    }

    override fun onResume() {
        super.onResume()
        checkIfNewDay()
    }

    private fun addWater() {
        // Pobranie wartości z SharedPreferences
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

        val sumWaterAmount = waterAmount.toFloat()/1000
        waterDrank += sumWaterAmount

        // zapisanie aktualnej ilości wypitej wody do SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putFloat("waterDrank", waterDrank)
        editor.apply()

        // Obliczenie procentowego postępu i aktualizacja ProgressBar oraz TextView
        val progressPercent = ((waterDrank / waterGoal) * 100).toInt()
        progressBar.progress = progressPercent
        waterDrankTextView.text = "Wypito już: ${ String.format("%.1f", (waterDrank))}l"
        waterGoalInfoTextView.text = "Zostało do celu: ${ String.format("%.1f", (waterGoal - waterDrank))}l"
        waterDrankPercentage.text = "$progressPercent%"

        if(progressPercent>99){
            val imageOrder = findViewById<ImageView>(R.id.imageView3)
            imageOrder.visibility = View.VISIBLE
            Toast.makeText(this,"Brawo! Osiągnięto dzienny cel!",Toast.LENGTH_SHORT).show()
            waterGoalInfoTextView.text = "Osiągnięto dzienny cel!"

        }
        else {
            calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + alarmTime)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            setAlarm()
        }




    }

    private fun setAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent (this,AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)


        println(calendar.timeInMillis)
        Toast.makeText(this,"Wypito wodę, ustawiono przypomnienie!",Toast.LENGTH_SHORT).show()

    }

    private fun checkIfNewDay() {

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
            waterDrankPercentage.visibility=View.GONE

            val imageOrder = findViewById<ImageView>(R.id.imageView3)
            imageOrder.visibility = View.GONE


        }
    }

}
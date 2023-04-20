package com.example.stayhydrated
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.stayhydrated.MainActivity
import com.example.stayhydrated.R
import org.w3c.dom.Text

class NotificationActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var myImageView: ImageView
    private var shakeThreshold = 700f
    private var lastUpdate: Long = 0
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f
    private var lastAngle = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        val bckButton = findViewById<Button>(R.id.buttonBack)
        bckButton.visibility = Button.INVISIBLE

        bckButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        myImageView = findViewById(R.id.imageView3)

    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            val timeDifference = currentTime - lastUpdate
            if (timeDifference > 100) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                // Obliczanie przesunięcia
                val deltaX = x - lastX
                val deltaY = y - lastY
                val deltaZ = z - lastZ

                // Przesunięcie obrazka
                myImageView.translationX -= deltaX * 50.0f
                myImageView.translationY += deltaY * 50.0f

                // Obliczanie kąta obrotu
                val angleX = Math.toDegrees(Math.atan2(deltaY.toDouble(), deltaZ.toDouble())).toFloat()
                val angleY = Math.toDegrees(Math.atan2(deltaX.toDouble(), deltaZ.toDouble())).toFloat()
                val angle = (lastAngle - angleY + angleX) % 360

                // Obrót obrazka
                myImageView.rotation = angle

                val acceleration = Math.abs(x + y + z - lastX - lastY - lastZ) / timeDifference * 10000
                if (acceleration > shakeThreshold) {
                    val textInfo = findViewById<TextView>(R.id.textInfo)
                    textInfo.visibility = TextView.INVISIBLE
                    val bckButton = findViewById<Button>(R.id.buttonBack)
                    bckButton.visibility = Button.VISIBLE
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                lastX = x
                lastY = y
                lastZ = z
                lastAngle = angle
                lastUpdate = currentTime
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // not needed for this example
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onBackPressed() {
        // tu możemy umieścić kod, który zostanie wykonany, gdy użytkownik spróbuje wyjść z widoku
        // w tym przypadku nie robimy nic, żeby zablokować możliwość wyjścia
    }
}

package com.gergerapex1.phonemaus

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import okhttp3.WebSocket

class ForegroundService : Service() {
    // Websocket Stuff
    private var websocketConnection: WebsocketConnection = WebsocketConnection()
    private var websocket: WebSocket? = null

    // Sensor Manager
    private var mSensorManager: SensorManager? = null

    // Sensors
    private var mAccelerator: Sensor? = null
    private var mGyroscope: Sensor? = null
    private var mLinearAccleration: Sensor? = null
    private var mRotationVector: Sensor? = null

    override fun onCreate() {
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // getSensor
        mAccelerator = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGyroscope = mSensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mLinearAccleration = mSensorManager!!.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        mRotationVector = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        // REGISTER LISTENER
        info("Created a websocket, Registered Sensor Listeners.")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val channelId = createNotificationChannel("service_phonetopc", "ForegroundService")
        val pendingIntent: PendingIntent =
            Intent(this, ForegroundService::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Connection To PC")
            .setContentText("Application connected to PC.")
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setContentIntent(pendingIntent)
            .setTicker("ticker?")
            .build()
        val port = intent.getStringExtra("PORT")
        val ip = intent.getStringExtra("IP")
        val code = intent.getStringExtra("CODE")

        info("${port}, ${ip}, $code, ${port != null && ip != null && code != null}")
        websocket = websocketConnection.createConnection(ip!!, port!!, code!!, this)
        registerListeners()
        startForeground(1, notification)
        return START_STICKY
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(notificationChannel)
        return channelId
    }
    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val sensorType = event.sensor.stringType
            val data = event.values.joinToString(",")
            websocket?.send("$sensorType,$data")
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Handle changes in sensor accuracy
        }
    }
    private fun registerListeners() {
        mSensorManager?.registerListener(
            sensorEventListener,
            mAccelerator,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        mSensorManager?.registerListener(
            sensorEventListener,
            mGyroscope,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        mSensorManager?.registerListener(
            sensorEventListener,
            mLinearAccleration,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        mSensorManager?.registerListener(
            sensorEventListener,
            mRotationVector,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        info("Registered Listeners")
    }

    override fun onDestroy() {
        mSensorManager?.unregisterListener(sensorEventListener, mAccelerator)
        mSensorManager?.unregisterListener(sensorEventListener, mGyroscope)
        mSensorManager?.unregisterListener(sensorEventListener, mLinearAccleration)
        mSensorManager?.unregisterListener(sensorEventListener, mRotationVector)
        websocketConnection.closeWebsocket()
        info("onDestroy() executed. Unregistered listeners and closed Websocket connection.")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun info(text: String?) {
        Log.i("phonetopc.SensorService", "$text")
    }
    fun selfDestruct() {
        stopSelf()
    }
}

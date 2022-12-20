package com.gergerapex1.phonemaus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity() {
    var started = false
    var myButton: Button? = null
    // Port and IP
    var portText: EditText? = null
    var ipText: EditText? = null
    var codeText: EditText? = null
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myButton = findViewById<View>(R.id.service_button) as Button?
        portText = findViewById<EditText>(R.id.port)
        ipText = findViewById(R.id.ip)
        codeText = findViewById(R.id.pair_code)

        myButton?.setOnClickListener {
            started = if (started) {
                val serviceIntent = Intent(applicationContext, ForegroundService::class.java)
                stopService(serviceIntent)
                myButton!!.setText(R.string.start_service)
                false
            } else {
                val serviceIntent = Intent(applicationContext, ForegroundService::class.java)
                serviceIntent.putExtra("PORT", portText!!.text.toString())
                serviceIntent.putExtra("IP", ipText!!.text.toString())
                serviceIntent.putExtra("CODE", codeText!!.text.toString())
                startForegroundService(serviceIntent)
                myButton!!.setText(R.string.stop_service)
                true
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(applicationContext, ForegroundService::class.java))
    }
}
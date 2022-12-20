package com.gergerapex1.phonemaus

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.viewbinding.BuildConfig
import com.google.gson.Gson
import okhttp3.*
import java.util.concurrent.TimeUnit


class WebsocketConnection {
    private var webSocket: WebSocket? = null
    private var httpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(5000, TimeUnit.MILLISECONDS)
        .writeTimeout(5000, TimeUnit.MILLISECONDS)
        .connectTimeout(5000, TimeUnit.MILLISECONDS)
        .build()
    var url: String = "ws://192.168.203.202:8080"
    //private var URL: String = "ws://localhost:6964"
    private fun createSocket(ip:String, port:String, code: String, service: ForegroundService) {
        val authentication = Authentication(ip, port, service)
        val token: String? = authentication.authenticate(code)
        info("TOKEN: $token")
        info("URI: $url")
        val webSocketRequest: Request = Request.Builder()
            .url(url)
            .header("Authorization", "$token")
            .build()
        class OnOpenListener : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.i("WebsocketEntry", text)
            }
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                println("CLOSE: $code $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                t.printStackTrace()
            }
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.i("WebsocketConnection", "Successfully opened a connection to $url")
                Log.i("WebsocketConnection", "Sending build and device info to server")
                sendInfo()
                Log.i("WebsocketConnection", "Sent build and device info")
            }
        }
        webSocket = httpClient.newWebSocket(webSocketRequest, OnOpenListener())
        httpClient.dispatcher.executorService.shutdown()
        sendInfo()
        Log.i("WebsocketConnection", "Created a websocket class")
    }
    fun createConnection(ip:String = "192.168.1.4", port:String = "8080", code: String, service: ForegroundService): WebSocket? {
        url = "ws://$ip:$port/connectWebSocket/"
        createSocket(ip, port, code,service)
        Log.i("WebsocketConnection", "Connected.")
        return webSocket
    }

    fun closeWebsocket() {
        info("${webSocket?.queueSize().toString()}")
        webSocket?.close(1001, "ok")
        webSocket?.cancel()
        info("Websocket Clo sed.")
    }
    private fun sendInfo() {
        val infoText: String = Gson().toJson(FirstSendInfo()).toString()
        webSocket?.send(infoText)
    }
    private fun info(text: String?) {
        Log.i("phonetopc.WebsocketConnection", "$text")
    }
}

@SuppressLint("HardwareIds")
class FirstSendInfo {
    //private var appVersion: Int = BuildConfig
    //private var appBuild: String = BuildConfig
    private var deviceModel: String = Build.MODEL
    private var deviceManufacture: String = Build.MANUFACTURER
    private var deviceBrand: String = Build.BRAND
}
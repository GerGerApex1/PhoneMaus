@file:Suppress("DEPRECATION")

package com.gergerapex1.phonemaus

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.*


@Suppress("DEPRECATION")
class Authentication(_ip: String = "192.168.1.4", port: String = "8080", _service: ForegroundService) {

    var jsonObject: JsonObject = JsonObject()
    val deviceInfo: String = Gson().toJson(FirstSendInfo()).toString()
    var mediaType = "application/json; charset=utf-8".toMediaType()
    val ip = _ip
    val requestURL: String = "http://$_ip:$port/pairClient"
    val executor: ExecutorService = Executors.newSingleThreadExecutor()
    val service = _service
    fun authenticate(code: String): String? {
        if (!checkAddressAvailability()) return null
        val response: Response?
        var wsToken: String = null.toString()
        generateJsonObject(code)

        // Use an Executor to execute the HTTP request concurrently
        val request = Request.Builder()
            .url(requestURL)
            .post(jsonObject.toString().toRequestBody(mediaType))
            .build()
        val httpClient = OkHttpClient.Builder()
            .readTimeout(10000, TimeUnit.MILLISECONDS)
            .build()
        val callableTask: Callable<Response> = Callable<Response> {
            try {
                httpClient.newCall(request).execute()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
        val result = executor.submit(callableTask)
        // Use the result from the future to get the response
        try {
            response = result.get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return null
        } catch (e: ExecutionException) {
            e.printStackTrace()
            return null
        }
        try {
            val responseBody = response?.body?.string()
            val responseBodyObject = JsonParser.parseString(responseBody).asJsonObject
            if (responseBodyObject.get("status").asString == "OK") {
                wsToken = responseBodyObject.get("websocketToken").asString
            } else {
                wsToken = null.toString()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        executor.shutdown()
        return wsToken
    }


    private fun generateJsonObject(code: String) {
        jsonObject.addProperty("code", code)
        jsonObject.addProperty("deviceInfo", deviceInfo)
    }

    private fun checkAddressAvailability(): Boolean {
        val future = FutureTask {
            try {
                val address = InetAddress.getByName(ip)
                return@FutureTask address.isReachable(3000)
            } catch (e: UnknownHostException) {
                Log.e("checkAddressAvailability", "Invalid IP address or hostname")
                service.selfDestruct()
                return@FutureTask false
            }
        }
        executor.submit(future)
        return future.get()
    }


    private fun info(text: String?) {
        Log.i("phonetopc.Authentication", "$text")
    }
}
package com.trollmaster.pro.data

import com.trollmaster.pro.data.model.RelayData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class RelayRepository(private var relayUrl: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val jsonType = "application/json; charset=utf-8".toMediaType()

    fun updateUrl(url: String) {
        relayUrl = url
    }

    suspend fun fetchData(): Result<RelayData> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(relayUrl)
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }
            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
            val json = JSONObject(body)
            Result.success(RelayData.fromJson(json))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendCommand(
        username: String,
        action: String,
        params: Map<String, String>,
        vipHash: String?,
        admHash: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // First fetch current data to preserve it
            val currentRequest = Request.Builder().url(relayUrl).get().build()
            val currentResponse = client.newCall(currentRequest).execute()
            val currentJson = if (currentResponse.isSuccessful) {
                val body = currentResponse.body?.string()
                if (body != null) JSONObject(body) else JSONObject()
            } else {
                JSONObject()
            }

            // Build command object
            val cmdObj = JSONObject().apply {
                put("action", action)
                val paramsJson = JSONObject()
                params.forEach { (k, v) -> paramsJson.put(k, v) }
                put("params", paramsJson)
                put("ts", System.currentTimeMillis() / 1000)
                if (vipHash != null) put("vip_auth", vipHash)
                if (admHash != null) put("adm_auth", admHash)
            }

            // Merge command into current data
            currentJson.put("cmd_$username", cmdObj)

            val body = currentJson.toString().toRequestBody(jsonType)
            val request = Request.Builder()
                .url(relayUrl)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendComboStep(
        username: String,
        action: String,
        params: Map<String, String>,
        vipHash: String?,
        admHash: String?
    ): Result<Unit> = sendCommand(username, action, params, vipHash, admHash)
}

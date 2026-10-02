package com.example.data.sync

import com.example.data.model.RepairRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseSyncService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Tests connection to Supabase instance
     */
    suspend fun testConnection(url: String, anonKey: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = url.trim().removeSuffix("/")
            val request = Request.Builder()
                .url("$cleanUrl/rest/v1/repairs?select=id&limit=1")
                .header("apikey", anonKey.trim())
                .header("Authorization", "Bearer ${anonKey.trim()}")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success("Connexion réussie à Supabase !")
                } else {
                    val code = response.code
                    val errorBody = response.body?.string() ?: ""
                    if (code == 404 || errorBody.contains("relation \"public.repairs\" does not exist")) {
                        Result.failure(Exception("Table 'repairs' introuvable dans Supabase. Veuillez exécuter le script SQL."))
                    } else if (code == 401 || code == 403) {
                        Result.failure(Exception("Clé Anon Key ou URL invalide (Erreur $code)."))
                    } else {
                        Result.failure(Exception("Erreur Supabase ($code): $errorBody"))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Uploads local repairs to Supabase (Upsert / Merge duplicates on id)
     */
    suspend fun uploadRepairs(
        url: String,
        anonKey: String,
        repairs: List<RepairRecord>
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            if (repairs.isEmpty()) return@withContext Result.success(0)

            val cleanUrl = url.trim().removeSuffix("/")
            val jsonArray = JSONArray()

            for (r in repairs) {
                val obj = JSONObject().apply {
                    put("id", r.id)
                    put("code", r.code)
                    put("code_number", r.codeNumber)
                    put("brand", r.brand)
                    put("model", r.model)
                    put("color", r.color)
                    put("problem", r.problem)
                    put("price", r.price)
                    put("client_name", r.clientName)
                    put("client_phone", r.clientPhone)
                    put("received_at", r.receivedAt)
                    put("status", r.status)
                    put("delivered_at", r.deliveredAt ?: JSONObject.NULL)
                    put("notes", r.notes)
                }
                jsonArray.put(obj)
            }

            val requestBody = jsonArray.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url("$cleanUrl/rest/v1/repairs")
                .header("apikey", anonKey.trim())
                .header("Authorization", "Bearer ${anonKey.trim()}")
                .header("Prefer", "resolution=merge-duplicates")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful || response.code == 201) {
                    Result.success(repairs.size)
                } else {
                    val errorMsg = response.body?.string() ?: ""
                    Result.failure(Exception("Erreur Supabase (${response.code}): $errorMsg"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Downloads all repairs from Supabase
     */
    suspend fun fetchRepairs(
        url: String,
        anonKey: String
    ): Result<List<RepairRecord>> = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = url.trim().removeSuffix("/")
            val request = Request.Builder()
                .url("$cleanUrl/rest/v1/repairs?select=*&order=code_number.asc")
                .header("apikey", anonKey.trim())
                .header("Authorization", "Bearer ${anonKey.trim()}")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: "[]"
                    val jsonArray = JSONArray(body)
                    val list = mutableListOf<RepairRecord>()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val record = RepairRecord(
                            id = obj.optString("id", "${obj.optString("code")}_${obj.optString("received_at")}"),
                            code = obj.optString("code", "001"),
                            codeNumber = obj.optInt("code_number", 1),
                            brand = obj.optString("brand", "Samsung"),
                            model = obj.optString("model", "Phone"),
                            color = obj.optString("color", "Noir"),
                            problem = obj.optString("problem", "En attente"),
                            price = obj.optDouble("price", 0.0),
                            clientName = obj.optString("client_name", "Client"),
                            clientPhone = obj.optString("client_phone", "0600000000"),
                            receivedAt = obj.optString("received_at", ""),
                            status = obj.optString("status", "waiting"),
                            deliveredAt = if (obj.has("delivered_at") && !obj.isNull("delivered_at")) obj.getString("delivered_at") else null,
                            notes = obj.optString("notes", "")
                        )
                        list.add(record)
                    }
                    Result.success(list)
                } else {
                    val errorMsg = response.body?.string() ?: ""
                    Result.failure(Exception("Erreur Supabase (${response.code}): $errorMsg"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val SUPABASE_TABLE_SQL = """-- Script SQL pour créer la table dans Supabase SQL Editor
create table if not exists public.repairs (
  id text primary key,
  code text not null,
  code_number integer not null,
  brand text not null,
  model text not null,
  color text default '',
  problem text not null,
  price numeric not null default 0,
  client_name text not null,
  client_phone text not null,
  received_at text not null,
  status text not null default 'waiting',
  delivered_at text,
  notes text default '',
  created_at timestamp with time zone default timezone('utc'::text, now())
);

-- Activation de la sécurité RLS
alter table public.repairs enable row level security;

-- Politique autorisant l'accès complet pour l'application d'atelier
create policy "Allow shop app access"
  on public.repairs
  for all
  using (true)
  with check (true);
"""
    }
}

package com.example.data.repository

import com.example.data.db.RepairDao
import com.example.data.model.RepairRecord
import com.example.data.model.RepairStatus
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

interface IRepairRepository {
    fun getAllRepairs(): Flow<List<RepairRecord>>
    fun getInShopRepairs(): Flow<List<RepairRecord>>
    fun getDeliveredRepairs(): Flow<List<RepairRecord>>
    fun searchRepairs(query: String): Flow<List<RepairRecord>>
    suspend fun getNextSequentialCode(): Pair<String, Int>
    suspend fun getRepairByCode(code: String): RepairRecord?
    fun getRepairById(id: String): Flow<RepairRecord?>
    suspend fun insertRepair(record: RepairRecord)
    suspend fun updateRepair(record: RepairRecord)
    suspend fun updateStatus(id: String, newStatus: RepairStatus)
    suspend fun deleteRepair(record: RepairRecord)
    suspend fun exportToJson(records: List<RepairRecord>): String
    suspend fun importFromJson(jsonString: String): Int
}

class RoomRepairRepository(
    private val dao: RepairDao
) : IRepairRepository {

    override fun getAllRepairs(): Flow<List<RepairRecord>> = dao.getAllRepairs()

    override fun getInShopRepairs(): Flow<List<RepairRecord>> {
        val inShopStatuses = listOf(
            RepairStatus.WAITING.id,
            RepairStatus.REPAIRING.id,
            RepairStatus.READY.id
        )
        return dao.getRepairsByStatuses(inShopStatuses)
    }

    override fun getDeliveredRepairs(): Flow<List<RepairRecord>> {
        return dao.getRepairsByStatuses(listOf(RepairStatus.DELIVERED.id))
    }

    override fun searchRepairs(query: String): Flow<List<RepairRecord>> {
        return dao.searchRepairs(query)
    }

    override suspend fun getNextSequentialCode(): Pair<String, Int> {
        val maxNumber = dao.getMaxCodeNumber() ?: 0
        val nextNumber = maxNumber + 1
        val codeString = String.format("%03d", nextNumber)
        return Pair(codeString, nextNumber)
    }

    override suspend fun getRepairByCode(code: String): RepairRecord? {
        val cleaned = code.trim().removePrefix("#")
        return dao.getRepairByCode(cleaned)
    }

    override fun getRepairById(id: String): Flow<RepairRecord?> {
        return dao.getRepairById(id)
    }

    override suspend fun insertRepair(record: RepairRecord) {
        dao.insert(record)
    }

    override suspend fun updateRepair(record: RepairRecord) {
        dao.update(record)
    }

    override suspend fun updateStatus(id: String, newStatus: RepairStatus) {
        // Will be called by viewmodel with full record or handled in memory
    }

    override suspend fun deleteRepair(record: RepairRecord) {
        dao.delete(record)
    }

    override suspend fun exportToJson(records: List<RepairRecord>): String {
        val array = JSONArray()
        records.forEach { record ->
            val obj = JSONObject().apply {
                put("id", record.id)
                put("code", record.code)
                put("codeNumber", record.codeNumber)
                put("brand", record.brand)
                put("model", record.model)
                put("color", record.color)
                put("problem", record.problem)
                put("price", record.price)
                put("clientName", record.clientName)
                put("clientPhone", record.clientPhone)
                put("receivedAt", record.receivedAt)
                put("deliveredAt", record.deliveredAt ?: JSONObject.NULL)
                put("status", record.status)
                put("notes", record.notes)
            }
            array.put(obj)
        }
        return array.toString(2)
    }

    override suspend fun importFromJson(jsonString: String): Int {
        val array = JSONArray(jsonString)
        val list = mutableListOf<RepairRecord>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val record = RepairRecord(
                id = obj.optString("id"),
                code = obj.optString("code"),
                codeNumber = obj.optInt("codeNumber", 1),
                brand = obj.optString("brand"),
                model = obj.optString("model"),
                color = obj.optString("color"),
                problem = obj.optString("problem"),
                price = obj.optDouble("price", 0.0),
                clientName = obj.optString("clientName"),
                clientPhone = obj.optString("clientPhone"),
                receivedAt = obj.optString("receivedAt"),
                deliveredAt = if (obj.isNull("deliveredAt")) null else obj.optString("deliveredAt"),
                status = obj.optString("status", RepairStatus.WAITING.id),
                notes = obj.optString("notes", "")
            )
            list.add(record)
        }
        if (list.isNotEmpty()) {
            dao.insertAll(list)
        }
        return list.size
    }
}

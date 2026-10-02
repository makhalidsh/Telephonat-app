package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "repairs")
data class RepairRecord(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val code: String,
    val codeNumber: Int,
    val brand: String,
    val model: String,
    val color: String,
    val problem: String,
    val price: Double,
    val clientName: String,
    val clientPhone: String,
    val receivedAt: String,
    val deliveredAt: String? = null,
    val status: String = RepairStatus.WAITING.id,
    val notes: String = ""
) {
    val statusEnum: RepairStatus
        get() = RepairStatus.fromId(status)

    fun qrPayload(): String {
        val cleanPhone = clientPhone.replace(" ", "")
        val cleanClient = clientName.replace("|", " ")
        val cleanModel = "$brand $model".replace("|", " ")
        return "ID:$code|TEL:$cleanPhone|CLIENT:$cleanClient|DEV:$cleanModel|PRICE:${price.toInt()}DH"
    }
}

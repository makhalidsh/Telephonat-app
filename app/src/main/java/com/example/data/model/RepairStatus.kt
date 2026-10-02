package com.example.data.model

enum class RepairStatus(val id: String) {
    WAITING("waiting"),
    REPAIRING("repairing"),
    READY("ready"),
    DELIVERED("delivered");

    companion object {
        fun fromId(id: String): RepairStatus {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: WAITING
        }
    }
}

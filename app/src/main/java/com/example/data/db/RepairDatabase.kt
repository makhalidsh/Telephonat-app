package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.RepairRecord
import com.example.data.model.RepairStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Database(entities = [RepairRecord::class], version = 1, exportSchema = false)
abstract class RepairDatabase : RoomDatabase() {
    abstract fun repairDao(): RepairDao

    companion object {
        @Volatile
        private var INSTANCE: RepairDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RepairDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RepairDatabase::class.java,
                    "repair_register.db"
                )
                    .addCallback(RepairDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class RepairDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialRecords(database.repairDao())
                    }
                }
            }
        }

        private suspend fun populateInitialRecords(dao: RepairDao) {
            val now = System.currentTimeMillis()
            val hourMs = 3600_000L
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            val samples = listOf(
                RepairRecord(
                    code = "001",
                    codeNumber = 1,
                    brand = "Samsung",
                    model = "Galaxy A54 5G",
                    color = "Noir",
                    problem = "Ecran cassé (Afficheur AMOLED)",
                    price = 450.0,
                    clientName = "Youssef El Amrani",
                    clientPhone = "0661234567",
                    receivedAt = isoFormat.format(Date(now - 4 * hourMs)),
                    status = RepairStatus.WAITING.id,
                    notes = "Code schéma en Z. Sauvegarde faite."
                ),
                RepairRecord(
                    code = "002",
                    codeNumber = 2,
                    brand = "Apple",
                    model = "iPhone 11",
                    color = "Blanc",
                    problem = "Batterie 74% (Remplacement OEM)",
                    price = 320.0,
                    clientName = "Fatima Zahra Bennani",
                    clientPhone = "0670891234",
                    receivedAt = isoFormat.format(Date(now - 3 * hourMs)),
                    status = RepairStatus.REPAIRING.id,
                    notes = "Vérifier étanchéité joint après fermeture."
                ),
                RepairRecord(
                    code = "003",
                    codeNumber = 3,
                    brand = "Xiaomi",
                    model = "Redmi Note 12",
                    color = "Bleu",
                    problem = "Connecteur de charge Type-C",
                    price = 180.0,
                    clientName = "Mehdi Tazi",
                    clientPhone = "0612987654",
                    receivedAt = isoFormat.format(Date(now - 2 * hourMs)),
                    status = RepairStatus.READY.id,
                    notes = "Prêt à être récupéré, test charge rapide 33W OK."
                ),
                RepairRecord(
                    code = "004",
                    codeNumber = 4,
                    brand = "Oppo",
                    model = "Reno 8",
                    color = "Gold",
                    problem = "Vitre arrière fissurée + Caméra floue",
                    price = 280.0,
                    clientName = "Omar Alami",
                    clientPhone = "0663456789",
                    receivedAt = isoFormat.format(Date(now - 24 * hourMs)),
                    deliveredAt = isoFormat.format(Date(now - 5 * hourMs)),
                    status = RepairStatus.DELIVERED.id,
                    notes = "Payé en espèces 280 DH. Client satisfait."
                )
            )
            dao.insertAll(samples)
        }
    }
}

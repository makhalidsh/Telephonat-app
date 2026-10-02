package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RepairRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairDao {
    @Query("SELECT * FROM repairs ORDER BY codeNumber DESC")
    fun getAllRepairs(): Flow<List<RepairRecord>>

    @Query("SELECT * FROM repairs WHERE status IN (:statuses) ORDER BY codeNumber DESC")
    fun getRepairsByStatuses(statuses: List<String>): Flow<List<RepairRecord>>

    @Query("SELECT MAX(codeNumber) FROM repairs")
    suspend fun getMaxCodeNumber(): Int?

    @Query("SELECT * FROM repairs WHERE code = :code LIMIT 1")
    suspend fun getRepairByCode(code: String): RepairRecord?

    @Query("SELECT * FROM repairs WHERE id = :id LIMIT 1")
    fun getRepairById(id: String): Flow<RepairRecord?>

    @Query("""
        SELECT * FROM repairs 
        WHERE code LIKE '%' || :query || '%' 
           OR brand LIKE '%' || :query || '%' 
           OR model LIKE '%' || :query || '%' 
           OR clientName LIKE '%' || :query || '%' 
           OR clientPhone LIKE '%' || :query || '%'
        ORDER BY codeNumber DESC
    """)
    fun searchRepairs(query: String): Flow<List<RepairRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: RepairRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<RepairRecord>)

    @Update
    suspend fun update(record: RepairRecord)

    @Delete
    suspend fun delete(record: RepairRecord)

    @Query("DELETE FROM repairs WHERE id = :id")
    suspend fun deleteById(id: String)
}

package com.example.kns.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: Record)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(records: List<Record>)

    @Delete
    suspend fun delete(record: Record)

    @Query("SELECT * FROM records")
    fun getAllRecords(): Flow<List<Record>>

    @Query("SELECT * FROM records")
    suspend fun getAllRecordsList(): List<Record>

    @Query("SELECT * FROM records WHERE name LIKE :query")
    fun findByName(query: String): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE aadhaar LIKE :query")
    fun findByAadhaar(query: String): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE pan LIKE :query")
    fun findByPan(query: String): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE bankAccount LIKE :query")
    fun findByBankAccount(query: String): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE cif LIKE :query")
    fun findByCif(query: String): Flow<List<Record>>
}

package com.example.kns.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val aadhaar: String,
    val pan: String,
    val dob: String,
    val mobile: String,
    val bankAccount: String,
    val cif: String,
    val address: String,
    val remark: String,
    val imageUri: String?
)
package com.example.horapj.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companies")
data class Company(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val hourlyRate: Double // Valor recebido por hora
)
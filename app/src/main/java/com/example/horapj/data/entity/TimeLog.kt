package com.example.horapj.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "time_logs",
    foreignKeys = [
        ForeignKey(
            entity = Company::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["companyId"])]
)
data class TimeLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val companyId: Int,
    val startTime: Long,
    val endTime: Long,
    val durationInMillis: Long
)
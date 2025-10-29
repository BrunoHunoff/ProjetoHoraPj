package com.example.horapj.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.horapj.data.entity.TimeLog
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeLogDao {

    @Insert
    suspend fun insert(timeLog: TimeLog)

    @Query("SELECT * FROM time_logs WHERE companyId = :companyId ORDER BY startTime DESC")
    fun getLogsForCompany(companyId: Int): Flow<List<TimeLog>>

    @Query("""
        SELECT 
            c.id as companyId, 
            c.name as companyName, 
            c.hourlyRate as hourlyRate, 
            SUM(t.durationInMillis) as totalDurationInMillis
        FROM 
            time_logs t
        JOIN 
            companies c ON t.companyId = c.id
        GROUP BY 
            c.id, c.name, c.hourlyRate
        ORDER BY 
            c.name ASC
    """)
    fun getAggregatedLogs(): Flow<List<AggregatedLogData>>
}
data class AggregatedLogData(
    val companyId: Int,
    val companyName: String,
    val hourlyRate: Double,
    val totalDurationInMillis: Long
)
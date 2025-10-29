package com.example.horapj.data.repository

import com.example.horapj.data.dao.AggregatedLogData
import com.example.horapj.data.dao.TimeLogDao
import com.example.horapj.data.entity.TimeLog
import kotlinx.coroutines.flow.Flow

class TimeLogRepository(private val timeLogDao: TimeLogDao) {

    suspend fun insert(timeLog: TimeLog) {
        timeLogDao.insert(timeLog)
    }

    fun getLogsForCompany(companyId: Int): Flow<List<TimeLog>> {
        return timeLogDao.getLogsForCompany(companyId)
    }

    val aggregatedLogs: Flow<List<AggregatedLogData>> =
        timeLogDao.getAggregatedLogs()
}
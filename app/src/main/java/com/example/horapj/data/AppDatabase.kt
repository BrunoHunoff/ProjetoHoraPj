package com.example.horapj.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.horapj.data.dao.CompanyDao
import com.example.horapj.data.dao.TimeLogDao // <-- 1. IMPORTE O NOVO DAO
import com.example.horapj.data.dao.UserDao
import com.example.horapj.data.entity.Company
import com.example.horapj.data.entity.TimeLog // <-- 2. IMPORTE A NOVA ENTIDADE
import com.example.horapj.data.entity.User

@Database(
    entities = [User::class, Company::class, TimeLog::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun companyDao(): CompanyDao
    abstract fun timeLogDao(): TimeLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "horapj_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
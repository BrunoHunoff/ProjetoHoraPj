package com.example.horapj.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.horapj.data.dao.CompanyDao // <-- 1. IMPORTE O NOVO DAO
import com.example.horapj.data.dao.UserDao
import com.example.horapj.data.entity.Company // <-- 2. IMPORTE A NOVA ENTIDADE
import com.example.horapj.data.entity.User

// 3. ADICIONE 'Company::class' NA LISTA DE ENTITIES
// 4. INCREMENTE A VERSÃO DO BANCO DE '1' PARA '2'
@Database(entities = [User::class, Company::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun companyDao(): CompanyDao // <-- 5. ADICIONE A FUNÇÃO ABSTRATA DO NOVO DAO

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
                    // Como mudamos a versão, o .fallbackToDestructiveMigration()
                    // vai destruir o banco antigo e criar um novo com o
                    // schema atualizado. Perfeito para desenvolvimento.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
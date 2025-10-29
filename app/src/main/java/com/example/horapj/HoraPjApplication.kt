package com.example.horapj

import android.app.Application
import com.example.horapj.data.AppDatabase
import com.example.horapj.data.repository.CompanyRepository // <-- 1. IMPORTE O NOVO REPO
import com.example.horapj.data.repository.UserRepository
import com.example.horapj.data.repository.TimeLogRepository

class HoraPjApplication : Application() {

    // Instância do Banco de Dados (sem alteração)
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    // Repositório de Usuário (sem alteração)
    val userRepository: UserRepository by lazy { UserRepository(database.userDao()) }

    // <-- 2. ADICIONE ESTA LINHA PARA O NOVO REPOSITÓRIO -->
    val companyRepository: CompanyRepository by lazy { CompanyRepository(database.companyDao()) }

    val timeLogRepository: TimeLogRepository by lazy { TimeLogRepository(database.timeLogDao()) }
}
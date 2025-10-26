package com.example.horapj.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.horapj.data.entity.Company
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(company: Company)

    @Update
    suspend fun update(company: Company)

    @Delete
    suspend fun delete(company: Company)

    /**
     * Busca uma empresa específica pelo ID.
     * Retorna 'suspend fun' porque é uma busca única.
     */
    @Query("SELECT * FROM companies WHERE id = :id")
    suspend fun getCompanyById(id: Int): Company?

    /**
     * Busca todas as empresas, ordenadas pelo nome.
     * Retorna um 'Flow' para que a UI possa "observar"
     * mudanças no banco em tempo real (reatividade).
     */
    @Query("SELECT * FROM companies ORDER BY name ASC")
    fun getAllCompanies(): Flow<List<Company>>
}
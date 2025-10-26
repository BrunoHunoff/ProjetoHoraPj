package com.example.horapj.data.repository

import com.example.horapj.data.dao.CompanyDao
import com.example.horapj.data.entity.Company
import kotlinx.coroutines.flow.Flow

/**
 * Repositório que abstrai o acesso aos dados da empresa.
 * Ele usa o CompanyDao para interagir com o banco de dados.
 */
class CompanyRepository(private val companyDao: CompanyDao) {

    /**
     * Expõe o Flow da lista de empresas diretamente do DAO.
     * O ViewModel vai "coletar" (observar) este Flow.
     */
    val allCompanies: Flow<List<Company>> = companyDao.getAllCompanies()

    /**
     * Busca uma única empresa pelo ID.
     */
    suspend fun getCompanyById(id: Int): Company? {
        return companyDao.getCompanyById(id)
    }

    /**
     * Insere uma nova empresa.
     */
    suspend fun insert(company: Company) {
        companyDao.insert(company)
    }

    /**
     * Atualiza uma empresa existente.
     */
    suspend fun update(company: Company) {
        companyDao.update(company)
    }

    /**
     * Deleta uma empresa.
     */
    suspend fun delete(company: Company) {
        companyDao.delete(company)
    }
}
package com.example.horapj.data.repository

import com.example.horapj.data.dao.UserDao
import com.example.horapj.data.entity.User

/**
 * Repositório que abstrai o acesso aos dados do usuário.
 * Ele usa o UserDao para interagir com o banco de dados.
 */
class UserRepository(private val userDao: UserDao) {

    /**
     * Tenta logar um usuário. Retorna o User em caso de sucesso ou null se falhar.
     */
    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    /**
     * Tenta registrar um novo usuário.
     * Retorna true se o registro for bem-sucedido.
     * Retorna false se o email já existir (ou outra falha de inserção).
     * */
    suspend fun register(user: User): Boolean {
        if (userDao.getUserByEmail(user.email) != null) {
            return false
        }
        return try {
            userDao.insertUser(user)
            true
        } catch (e: Exception) {
            false
        }
    }
}
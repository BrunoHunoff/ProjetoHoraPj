package com.example.horapj.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.horapj.data.entity.User

@Dao
interface UserDao {

    /**
     * Insere um novo usuário. Se o email já existir (conflito), a operação será abortada.
     * Usamos 'suspend' para que possa ser chamada de uma Coroutine (sem travar a UI).
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    /**
     * Busca um usuário pelo email e senha (para o Login).
     */
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    /**
     * Busca um usuário apenas pelo email (para verificar se já existe no Cadastro).
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
}
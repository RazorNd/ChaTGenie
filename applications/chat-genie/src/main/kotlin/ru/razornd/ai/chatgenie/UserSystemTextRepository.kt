package ru.razornd.ai.chatgenie

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

data class UserSystemText(val userId: String, val systemText: String)

interface UserSystemTextRepository {

    fun getByUserId(userId: String): UserSystemText?

    fun updateSystemText(userId: String, newSystemText: String)

}

@Repository
open class JdbcUserSystemTextRepository(private val jdbcClient: JdbcClient) : UserSystemTextRepository {

    override fun getByUserId(userId: String): UserSystemText? {
        return jdbcClient.sql("SELECT user_id, system_text FROM user_system_text WHERE user_id = :userId")
            .param("userId", userId)
            .queryFor<UserSystemText>()
            .singleOrNull()
    }

    override fun updateSystemText(userId: String, newSystemText: String) {
        jdbcClient.sql("""
            INSERT INTO user_system_text (user_id, system_text) 
            VALUES (:userId, :newSystemText) 
            ON CONFLICT (user_id) DO UPDATE 
            SET system_text = :newSystemText
        """)
        .param("userId", userId)
        .param("newSystemText", newSystemText)
        .update()
    }

}

inline fun <reified T> JdbcClient.StatementSpec.queryFor() = query(T::class.java)

fun <T: Any> JdbcClient.MappedQuerySpec<T>.singleOrNull(): T? = optional().getOrNull()

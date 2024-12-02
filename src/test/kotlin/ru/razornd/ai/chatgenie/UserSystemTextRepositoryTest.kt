package ru.razornd.ai.chatgenie

import org.assertj.core.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnectionFactory
import org.assertj.db.type.Changes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource
import org.assertj.db.api.Assertions.assertThat as assertThatDb

@JdbcTest
@Import(DataBaseTestcontainersConfiguration::class, JdbcUserSystemTextRepository::class)
open class UserSystemTextRepositoryTest {

    @Autowired
    private lateinit var repository: UserSystemTextRepository

    private lateinit var changes: Changes

    @BeforeEach
    fun setUp(@Autowired dataSource: DataSource) {
        val connection = AssertDbConnectionFactory.of(TransactionAwareDataSourceProxy(dataSource)).create()

        changes = connection.changes().table("user_system_text").build()
    }

    @Test
    @Sql(statements = ["INSERT INTO user_system_text (user_id, system_text) VALUES ('existing_user', 'Sample system text')"])
    fun `test getByUserId returns UserSystemText when userId exists`() {
        val userId = "existing_user"

        val result = repository.getByUserId(userId)

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(UserSystemText(userId, "Sample system text"))
    }

    @Test
    @Sql(statements = ["INSERT INTO user_system_text (user_id, system_text) VALUES ('existing_user', 'Sample system text')"])
    fun `test updateSystemText updates existing user's system text`() {
        val userId = "existing_user"
        val newSystemText = "Updated system text"

        changes.setStartPointNow()

        repository.updateSystemText(userId, newSystemText)

        changes.setEndPointNow()

        assertThatDb(changes)
            .ofModification()
            .hasNumberOfChanges(1)
            .change()
            .rowAtEndPoint()
            .value("system_text").isEqualTo("Updated system text")
    }

    @Test
    fun `test updateSystemText creates a new entry for non-existing user`() {
        val userId = "non_existing_user"
        val newSystemText = "Updated system text"

        changes.setStartPointNow()

        repository.updateSystemText(userId, newSystemText)

        changes.setEndPointNow()

        assertThatDb(changes)
            .ofCreation()
            .hasNumberOfChanges(1)
            .change()
            .rowAtEndPoint()
            .value("user_id").isEqualTo(userId)
            .value("system_text").isEqualTo("Updated system text")
    }

    @Test
    fun `test getByUserId returns null when userId does not exist`() {
        val userId = "non_existing_user"

        val result = repository.getByUserId(userId)

        assertThat(result).isNull()
    }
}

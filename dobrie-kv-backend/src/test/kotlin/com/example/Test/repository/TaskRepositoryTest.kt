package com.example.Test.repository

import com.example.Test.model.Task
import com.example.Test.model.TaskStatus
import com.example.Test.repository.TaskRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import java.time.LocalDateTime

@DataJdbcTest
@Import(TaskRepository::class)
class TaskRepositoryTest {
    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Test
    fun `should create and find task`() {
        val task = Task(
                id = 0,
                title = "Test",
                description = "Desc",
                status = TaskStatus.NEW,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
        )

        val saved = taskRepository.save(task)
        val found = taskRepository.findById(saved.id)

        assertThat(found).isNotNull
        assertThat(found?.title).isEqualTo("Test")
    }
}
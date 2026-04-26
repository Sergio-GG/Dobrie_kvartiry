package com.example.Test.repository

import org.springframework.jdbc.support.GeneratedKeyHolder
import com.example.Test.model.Task
import com.example.Test.model.TaskStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
class TaskRepository(private val jdbcClient: JdbcClient) {

    fun save(task: Task): Task {
        val keyHolder = GeneratedKeyHolder()
        jdbcClient.sql("""
            INSERT INTO tasks (title, description, status, created_at, updated_at)
            VALUES (:title, :description, :status, :createdAt, :updatedAt)
        """)
                .param("title", task.title)
                .param("description", task.description)
                .param("status", task.status.name)
                .param("createdAt", task.createdAt)
                .param("updatedAt", task.updatedAt)
                .update(keyHolder)
        val generatedId = keyHolder.key?.toLong() ?: throw IllegalStateException("No ID generated")
        return findById(generatedId) ?: throw IllegalStateException("Failed to save task")
    }

    fun findById(id: Long): Task? {
        return jdbcClient.sql("SELECT * FROM tasks WHERE id = :id")
                .param("id", id)
                .query(Task::class.java)
                .optional() // Возвращает Optional<Task>
                .orElse(null)
    }

    fun findAll(page: Int, size: Int, status: TaskStatus?): Page<Task> {
        val offset = page * size
        val whereClause = if (status != null) "WHERE status = :status" else ""

        val tasks = jdbcClient.sql("""
            SELECT * FROM tasks 
            $whereClause 
            ORDER BY created_at DESC 
            LIMIT :limit OFFSET :offset
        """)
                .param("limit", size)
                .param("offset", offset)
                .let { if (status != null) it.param("status", status.name) else it }
                .query(Task::class.java)
                .list()

        val total = jdbcClient.sql("SELECT COUNT(*) FROM tasks $whereClause")
                .let { if (status != null) it.param("status", status.name) else it }
                .query(Long::class.java)
                .single()
        val totalPagesInt = if (size > 0) Math.ceil(total.toDouble() / size).toInt() else 0
        return Page(tasks, page, size, total, totalPages = totalPagesInt.toLong())
    }

    fun updateStatus(id: Long, status: TaskStatus): Task {
        jdbcClient.sql("""
            UPDATE tasks SET status = :status, updated_at = :now 
            WHERE id = :id
        """)
                .param("status", status.name)
                .param("now", LocalDateTime.now())
                .param("id", id)
                .update()
        return jdbcClient.sql("SELECT * FROM tasks WHERE id = :id")
                .param("id", id)
                .query(Task::class.java)
                .single()
    }

    fun deleteById(id: Long) {
        jdbcClient.sql("DELETE FROM tasks WHERE id = :id")
                .param("id", id)
                .update()
    }
}
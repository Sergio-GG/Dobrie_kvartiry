package com.example.Test.dto
import com.example.Test.model.TaskStatus
import java.time.LocalDateTime

data class TaskResponse(
        val id: Long,
        val title: String,
        val description: String? = null,
        val status: TaskStatus,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null
)
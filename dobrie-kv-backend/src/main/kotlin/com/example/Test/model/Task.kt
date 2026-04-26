package com.example.Test.model

import java.time.LocalDateTime

data class Task(
        val id: Long = 0,
        val title: String,
        val description: String?,
        val status: TaskStatus,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
)
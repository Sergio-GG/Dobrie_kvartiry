package com.example.Test.extensions

import com.example.Test.dto.PageResponse
import com.example.Test.dto.TaskResponse
import com.example.Test.model.Task
import com.example.Test.repository.Page

// Используем T для исходного типа и R для результирующего
fun <T, R> Page<T>.toPageResponse(mapper: (T) -> R): PageResponse<R> {
    return PageResponse(
            content = this.content.map(mapper),
            page = this.page,
            size = this.size,
            totalElements = this.totalElements,
            totalPages = this.totalPages
    )
}

fun Task.toResponse(): TaskResponse = TaskResponse(
        id = id,
        title = title,
        description = description,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
)
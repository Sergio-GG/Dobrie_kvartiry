package com.example.Test.service

import com.example.Test.dto.*
import com.example.Test.dto.CreateTaskRequest
import com.example.Test.dto.PageResponse
import com.example.Test.dto.TaskResponse
import com.example.Test.dto.UpdateStatusRequest
import com.example.Test.extensions.toPageResponse
import com.example.Test.extensions.toResponse
import com.example.Test.model.Task
import com.example.Test.model.TaskStatus
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import com.example.Test.repository.TaskRepository
import java.time.LocalDateTime

@Service
class TaskService(private val repository: TaskRepository) {

    fun createTask(request: CreateTaskRequest): Mono<TaskResponse> {
        val task = Task(
                id = 0,
                title = request.title,
                description = request.description,
                status = TaskStatus.NEW,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
        )
        return Mono.fromCallable { repository.save(task) }
                .subscribeOn(Schedulers.boundedElastic())
                .map { it.toResponse() }
    }

    fun getTaskById(id: Long): Mono<TaskResponse> =
            Mono.fromCallable { repository.findById(id) }
                    .subscribeOn(Schedulers.boundedElastic())
                    // Если findById вернул null, кидаем 404
                    .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")))
                    .map { it?.toResponse() }

    fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<PageResponse<TaskResponse>> =
            Mono.fromCallable { repository.findAll(page, size, status) }
                    .subscribeOn(Schedulers.boundedElastic())
                    .map { it.toPageResponse { task -> task.toResponse() } }

    fun updateStatus(id: Long, request: UpdateStatusRequest): Mono<TaskResponse> = Mono.fromCallable {
        repository.updateStatus(id, request.status)
    }
            .subscribeOn(Schedulers.boundedElastic())
            .map { it.toResponse() }

    fun deleteTask(id: Long): Mono<Void> =
            Mono.fromCallable { repository.deleteById(id) }
                    .subscribeOn(Schedulers.boundedElastic())
                    .then()
}
package com.example.Test.controller

import com.example.Test.dto.*
import com.example.Test.dto.CreateTaskRequest
import com.example.Test.dto.PageResponse
import com.example.Test.dto.TaskResponse
import com.example.Test.dto.UpdateStatusRequest
import jakarta.validation.Valid
import com.example.Test.model.TaskStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import com.example.Test.service.TaskService

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = ["http://localhost:4200"])
class TaskController(private val service: TaskService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTask(@Valid @RequestBody request: CreateTaskRequest): Mono<TaskResponse> =
            service.createTask(request)

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): Mono<TaskResponse> =
            service.getTaskById(id)

    @GetMapping
    fun getTasks(
            @RequestParam(defaultValue = "0") page: Int,
            @RequestParam(defaultValue = "10") size: Int,
            @RequestParam(required = false) status: TaskStatus?
    ): Mono<PageResponse<TaskResponse>> =
            service.getTasks(page, size, status)

    @PatchMapping("/{id}/status")
    fun updateStatus(
            @PathVariable id: Long,
            @Valid @RequestBody request: UpdateStatusRequest
    ): Mono<TaskResponse> =
            service.updateStatus(id, request)

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: Long): Mono<ResponseEntity<Void>> =
            service.deleteTask(id)
                    .thenReturn(ResponseEntity<Void>(HttpStatus.NO_CONTENT))
}
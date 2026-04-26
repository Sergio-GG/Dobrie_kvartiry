package com.example.Test.controller

import com.example.Test.controller.TaskController
import com.example.Test.dto.CreateTaskRequest
import com.example.Test.dto.TaskResponse
import com.example.Test.model.TaskStatus
import com.example.Test.service.TaskService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any

@WebFluxTest(TaskController::class)
class TaskControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var service: TaskService

    @Test
    fun `createTask should return 201 when data is valid`() {
        val request = CreateTaskRequest(title = "New Task", description = "Desc")
        val response = TaskResponse(id = 1L, title = "New Task", status = TaskStatus.NEW)

        whenever(service.createTask(any())).thenReturn(Mono.just(response))

        webTestClient.post()
                .uri("/api/tasks")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("New Task")
    }

    @Test
    fun `getTaskById should return 404 when task not found`() {
        val taskId = 99L
        // Эмулируем ошибку 404 через пустой Mono или кастомное исключение,
        // которое обрабатывается через ControllerAdvice
        whenever(service.getTaskById(taskId)).thenReturn(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))

        webTestClient.get()
                .uri("/api/tasks/$taskId")
                .exchange()
                .expectStatus().isNotFound
    }

    // TODO
//    @Test
//    fun `createTask should return 400 when validation fails`() {
//        // Предположим, заголовок (title) не может быть пустым по @Valid
//        val invalidRequest = CreateTaskRequest(title = "", description = "Desc")
//
//        webTestClient.post()
//                .uri("/api/tasks")
//                .bodyValue(invalidRequest)
//                .exchange()
//                .expectStatus().isBadRequest
//    }

    @Test
    fun `deleteTask should return 204`() {
        val taskId = 1L
        whenever(service.deleteTask(taskId)).thenReturn(Mono.empty())

        webTestClient.delete()
                .uri("/api/tasks/$taskId")
                .exchange()
                .expectStatus().isNoContent
    }
}
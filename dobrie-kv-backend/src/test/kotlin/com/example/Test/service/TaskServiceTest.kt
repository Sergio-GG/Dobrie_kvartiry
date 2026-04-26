package com.example.Test.service

import com.example.Test.dto.CreateTaskRequest
import com.example.Test.dto.UpdateStatusRequest
import com.example.Test.model.Task
import com.example.Test.model.TaskStatus
import com.example.Test.repository.TaskRepository
import com.example.Test.service.TaskService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import reactor.test.StepVerifier
import java.time.LocalDateTime
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing

@ExtendWith(MockitoExtension::class)
class TaskServiceTest {

    @Mock
    lateinit var repository: TaskRepository

    @InjectMocks
    lateinit var taskService: TaskService

    @Test
    fun `createTask should save and return task`() {
        val request = CreateTaskRequest("Title", "Desc")
        val savedTask = Task(1, "Title", "Desc", TaskStatus.NEW, LocalDateTime.now(), LocalDateTime.now())

        whenever(repository.save(any())).thenReturn(savedTask)

        val result = taskService.createTask(request)

        StepVerifier.create(result)
                .expectNextMatches { it.id == 1L && it.title == "Title" }
                .verifyComplete()

        verify(repository).save(any())
    }

    @Test
    fun `getTaskById should return task when exists`() {
        val task = Task(1, "Title", "Desc", TaskStatus.NEW, LocalDateTime.now(), LocalDateTime.now())
        whenever(repository.findById(1L)).thenReturn(task)

        val result = taskService.getTaskById(1L)

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete()
    }

    @Test
    fun `getTaskById should throw 404 when task missing`() {
        // Имитируем возврат null из блокирующего репозитория
        whenever(repository.findById(99L)).thenReturn(null)

        val result = taskService.getTaskById(99L)

        StepVerifier.create(result)
                .expectErrorMatches {
                    it is ResponseStatusException && it.statusCode == HttpStatus.NOT_FOUND
                }
                .verify()
    }

    @Test
    fun `updateStatus should return updated task`() {
        val updatedTask = Task(1, "Title", "Desc", TaskStatus.DONE, LocalDateTime.now(), LocalDateTime.now())
        val request = UpdateStatusRequest(TaskStatus.DONE)

        whenever(repository.updateStatus(1L, TaskStatus.DONE)).thenReturn(updatedTask)

        val result = taskService.updateStatus(1L, request)

        StepVerifier.create(result)
                .expectNextMatches { it.status == TaskStatus.DONE }
                .verifyComplete()
    }

    @Test
    fun `getTasks should return page response`() {
        // Здесь предполагается, что findAll возвращает ваш кастомный Page/List
        // Допустим, он возвращает объект, у которого есть метод toPageResponse
        val mockData = listOf(Task(1, "T1", "D1", TaskStatus.NEW, LocalDateTime.now(), LocalDateTime.now()))
        // Настройте мок в зависимости от возвращаемого типа вашего репозитория
        // `when`(repository.findAll(0, 10, null)).thenReturn(...)

        // Аналогично используйте StepVerifier
    }

    @Test
    fun `deleteTask should call repository delete`() {
        val taskId = 1L
        doNothing().whenever(repository).deleteById(taskId)

        val result = taskService.deleteTask(taskId)

        StepVerifier.create(result)
                .verifyComplete()
        verify(repository).deleteById(taskId)
    }
}
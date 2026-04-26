package com.example.Test.dto
import jakarta.validation.constraints.NotNull
import com.example.Test.model.TaskStatus

data class UpdateStatusRequest(
        @field:NotNull(message = "Status is required")
        val status: TaskStatus
)
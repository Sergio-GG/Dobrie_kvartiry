package com.example.Test.dto

import jakarta.validation.constraints.*


data class CreateTaskRequest(
        @field:NotBlank @field:Size(min = 3, max = 100) val title: String,
        val description: String?
)
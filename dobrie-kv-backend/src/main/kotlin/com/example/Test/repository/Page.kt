package com.example.Test.repository

data class Page<T>(
        val content: List<T>,
        val page: Int,
        val size: Int,
        val totalElements: Long,
        val totalPages: Long,
)
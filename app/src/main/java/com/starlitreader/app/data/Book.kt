package com.starlitreader.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uri: String,
    val title: String,
    val author: String? = null,
    val lastOpenedPage: Int = 0,
    val totalPages: Int = 0,
    val isFavorite: Boolean = false
)

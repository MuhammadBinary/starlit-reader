package com.starlitreader.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY id DESC")
    suspend fun getAll(): List<Book>

    @Insert
    suspend fun insert(book: Book): Long

    @Query("UPDATE books SET lastOpenedPage = :page WHERE id = :bookId")
    suspend fun updateProgress(bookId: Long, page: Int)
}

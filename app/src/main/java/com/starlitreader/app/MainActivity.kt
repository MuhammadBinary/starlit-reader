package com.starlitreader.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.starlitreader.app.data.AppDatabase
import com.starlitreader.app.data.Book
import com.starlitreader.app.ui.BookshelfScreen
import com.starlitreader.app.ui.ReaderScreen
import com.starlitreader.app.ui.theme.StarlitReaderTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)

        setContent {
            StarlitReaderTheme {
                var books by remember { mutableStateOf<List<Book>>(emptyList()) }
                var openBook by remember { mutableStateOf<Book?>(null) }

                LaunchedEffect(Unit) {
                    books = db.bookDao().getAll()
                }

                val pickPdf = rememberLauncherForActivityResult(
                    ActivityResultContracts.OpenDocument()
                ) { uri: Uri? ->
                    uri ?: return@rememberLauncherForActivityResult
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    val title = queryDisplayName(uri) ?: "Untitled PDF"
                    val book = Book(
                        uri = uri.toString(),
                        title = title,
                        lastOpenedPage = 0
                    )
                    lifecycleScope.launch {
                        db.bookDao().insert(book)
                        books = db.bookDao().getAll()
                        openBook = books.find { it.uri == book.uri }
                    }
                }

                val current = openBook
                if (current == null) {
                    BookshelfScreen(
                        books = books,
                        onImportPdf = { pickPdf.launch(arrayOf("application/pdf")) },
                        onOpenBook = { openBook = it }
                    )
                } else {
                    ReaderScreen(
                        book = current,
                        onBack = { openBook = null },
                        onPageChanged = { page ->
                            lifecycleScope.launch {
                                db.bookDao().updateProgress(current.id, page)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun queryDisplayName(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null) ?: return null
        cursor.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }
        return null
    }
}

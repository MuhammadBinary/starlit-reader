package com.starlitreader.app.ui

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.starlitreader.app.data.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    book: Book,
    onBack: () -> Unit,
    onPageChanged: (Int) -> Unit
) {
    val context = LocalContext.current
    var pageIndex by remember { mutableStateOf(book.lastOpenedPage) }
    var pageCount by remember { mutableStateOf(0) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(book.uri, pageIndex) {
        errorMessage = null
        withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(book.uri)
                context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd: ParcelFileDescriptor ->
                    PdfRenderer(pfd).use { renderer ->
                        pageCount = renderer.pageCount
                        val safeIndex = pageIndex.coerceIn(0, (pageCount - 1).coerceAtLeast(0))
                        renderer.openPage(safeIndex).use { page ->
                            val bmp = Bitmap.createBitmap(
                                page.width * 2,
                                page.height * 2,
                                Bitmap.Config.ARGB_8888
                            )
                            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            bitmap = bmp
                        }
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Couldn't open this PDF"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(pageCount) {
                    detectTapGestures { offset ->
                        val third = size.width / 3
                        when {
                            offset.x < third -> if (pageIndex > 0) {
                                pageIndex -= 1
                                onPageChanged(pageIndex)
                            }
                            offset.x > third * 2 -> if (pageIndex < pageCount - 1) {
                                pageIndex += 1
                                onPageChanged(pageIndex)
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            when {
                errorMessage != null -> Text("Couldn't open this PDF: $errorMessage")
                bitmap != null -> Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Page ${pageIndex + 1}",
                    modifier = Modifier.fillMaxSize()
                )
                else -> CircularProgressIndicator()
            }
            if (pageCount > 0) {
                Text(
                    "${pageIndex + 1} / $pageCount",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}

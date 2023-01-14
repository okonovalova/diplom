package com.example.diplom.data.repository

import android.content.Context
import android.net.Uri
import com.example.diplom.data.api.MediaService
import com.example.diplom.data.network.BaseRemoteRepository
import com.example.diplom.data.network.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


class MediaRepository @Inject constructor(
    private val mediaService: MediaService,
    private val context: Context
) : BaseRemoteRepository {
    suspend fun downloadImage(uri: Uri): DataResult<String> {
        return withContext(Dispatchers.Default) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "fileName")
            val oStream = FileOutputStream(tempFile)
            copy(inputStream!!, oStream)
            val requestFile = tempFile
                .asRequestBody("image/jpg".toMediaType())

            getResult(
                request = { mediaService.downloadFile(requestFile) },
                mapTo = {
                    it?.url.orEmpty()
                }
            )
        }
    }

    private fun copy(inputStream: InputStream, outputStream: FileOutputStream) {
        inputStream.use { inputStream ->
            outputStream.use { outputStream ->
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) {
                    outputStream.write(buf, 0, len)
                }
            }
        }
    }
}
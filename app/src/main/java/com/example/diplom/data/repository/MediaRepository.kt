package com.example.diplom.data.repository

import android.content.Context
import android.net.Uri
import com.example.diplom.data.api.MediaService
import com.example.diplom.data.network.BaseRemoteRepository
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.network.ResultError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


class MediaRepository @Inject constructor(
    private val mediaService: MediaService,
    private val context: Context
) : BaseRemoteRepository() {
    fun downloadImage(uri: Uri): Flow<DataResult<String>> {
        return flow {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = java.io.File(context.cacheDir, "fileName")
            val oStream = FileOutputStream(tempFile)
            copy(inputStream!!, oStream)
            val requestFile = tempFile
                .asRequestBody("image/jpg".toMediaType())

            val result = getResult(
                request = { mediaService.downloadFile(requestFile) },
                mapTo = {
                    it?.url.orEmpty()
                }
            )
            emit(result)

        }
            .flowOn(Dispatchers.IO)
            .catch { emit(DataResult.error(ResultError(0, it.toString()))) }
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
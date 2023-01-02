package com.example.diplom.data.repository

import android.content.Context
import android.net.Uri
import com.example.diplom.data.api.UserService
import com.example.diplom.data.mapper.PostMapper
import com.example.diplom.data.mapper.TokenMapper
import com.example.diplom.data.mapper.UserMapper
import com.example.diplom.data.network.BaseRemoteRepository
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.prefs.PreferenceService
import com.example.diplom.data.request.AuthentificationRequest
import com.example.diplom.data.request.RegistrationRequest
import com.example.diplom.domain.entity.Token
import com.example.diplom.domain.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.create
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


class UserRepository @Inject constructor(
    private val userService: UserService,
    private val preferenceService: PreferenceService,
    private val context: Context
) : BaseRemoteRepository() {
    suspend fun authentificate(login: String, password: String): Flow<DataResult<Token>> {
        val authentificationRequest = AuthentificationRequest(login, password)
        return flow {
            val result = getResult(
                request = { userService.authenticate(authentificationRequest) },
                mapTo = TokenMapper::mapDataToDomain
            )
            result.data?.let {
                preferenceService.accessToken = it.token
                preferenceService.userId = it.id
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun registrate(
        login: String,
        password: String,
        name: String,
        uri: Uri?
    ): Flow<DataResult<Token>> {
        return flow {
            var requestFile: RequestBody? = null
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = java.io.File(context.cacheDir, "fileName")
                val oStream = FileOutputStream(tempFile)
                copy(inputStream!!, oStream)
                requestFile = tempFile
                    .asRequestBody("image/jpg".toMediaType())
            }

            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val loginBody = login.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())

            val result = getResult(
                request = { userService.registrate(loginBody, nameBody, passwordBody, requestFile) },
                mapTo = TokenMapper::mapDataToDomain
            )
            result.data?.token.let {
                preferenceService.accessToken = it
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
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

    suspend fun getUserInfo(): Flow<DataResult<User>> {
        return flow {
            val result = getResult(
                request = { userService.getUserInfo(preferenceService.userId.toString()) },
                mapTo = UserMapper::mapDataToDomain
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }
}
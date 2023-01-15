package com.example.diplom.ui.registration

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.repository.UserRepository
import com.example.diplom.ui.utils.ResourceManager
import com.example.diplom.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val resourceManager: ResourceManager,
) : ViewModel() {

    private var login: String = ""
    private var password: String = ""
    private var name: String = ""

    private val _loginError: MutableLiveData<Boolean> = MutableLiveData()
    val loginError: LiveData<Boolean>
        get() = _loginError

    private val _passwordError: MutableLiveData<Boolean> = MutableLiveData()
    val passwordError: LiveData<Boolean>
        get() = _passwordError

    private val _nameError: MutableLiveData<Boolean> = MutableLiveData()
    val nameError: LiveData<Boolean>
        get() = _nameError
    val navigateToMainFragment = SingleLiveEvent<Unit>()
    val registrationError = SingleLiveEvent<String>()

    private val _downloadedImage: MutableLiveData<Uri?> = MutableLiveData(null)
    val downloadedImage: LiveData<Uri?>
        get() = _downloadedImage

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun onRegistrateButtonClicked() {
        if (login.isBlank() || password.isBlank() || name.isBlank()) {
            registrationError.postValue(resourceManager.getString(R.string.error_empty_fields))
            return
        }
        _isLoading.postValue(true)
        viewModelScope.launch {
            val result = userRepository.registrate(login, password, name, downloadedImage.value)
            _isLoading.postValue(false)
            if (result.status == DataResult.Status.SUCCESS) {
                navigateToMainFragment.postValue(Unit)
            } else {
                registrationError.postValue(result.error?.statusMessage.toString())
                Log.e("registration", result.error?.statusMessage.orEmpty())
            }
        }
    }

    fun onNewPictureSet(uri: Uri) {
        _downloadedImage.postValue(uri)
    }

    fun onChangeLogin(login: String) {
        this.login = login
        _loginError.postValue(login.isBlank())
    }

    fun onChangePassword(password: String) {
        this.password = password
        _passwordError.postValue(password.isBlank())
    }

    fun onChangeName(name: String) {
        this.name = name
        _nameError.postValue(name.isBlank())
    }
}
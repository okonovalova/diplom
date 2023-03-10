package com.example.diplom.ui.login

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
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val resourceManager: ResourceManager,
    ) : ViewModel() {
    private var login: String = ""
    private var password: String = ""

    val navigateToMainFragment = SingleLiveEvent<Unit>()

    val authenticateError = SingleLiveEvent<String>()

    private val _loginError: MutableLiveData<Boolean> = MutableLiveData()
    val loginError: LiveData<Boolean>
        get() = _loginError

    private val _passwordError: MutableLiveData<Boolean> = MutableLiveData()
    val passwordError: LiveData<Boolean>
        get() = _passwordError

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun onAuthenticateButtonClicked() {
        if (login.isBlank() || password.isBlank()) {
            authenticateError.postValue(resourceManager.getString(R.string.error_empty_fields))
            return
        }
        _isLoading.postValue(true)
        viewModelScope.launch {
            val result = userRepository.authenticate(login, password)
            _isLoading.postValue(false)
            if (result.status == DataResult.Status.SUCCESS) {
                navigateToMainFragment.postValue(Unit)
            } else {
                Log.e("onAuthenticateButtonClicked", result.error?.statusMessage.orEmpty())
                authenticateError.postValue(result.error?.statusMessage.toString())
            }
        }
    }

    fun onChangeLogin(login: String) {
        this.login = login
        _loginError.postValue(login.isBlank())
    }

    fun onChangePassword(password: String) {
        this.password = password
        _passwordError.postValue(password.isBlank())
    }
}
package com.example.diplom.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.repository.UserRepository
import com.example.diplom.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private var login: String = ""
    private var password: String = ""

    val navigateToMainFragment = SingleLiveEvent<Unit>()

    private val _loginError: MutableLiveData<Boolean> = MutableLiveData()
    val loginError: LiveData<Boolean>
        get() = _loginError

    private val _passwordError: MutableLiveData<Boolean> = MutableLiveData()
    val passwordError: LiveData<Boolean>
        get() = _passwordError

    fun onAunthentificateButtonClicked() {
        if (login.isBlank() || password.isBlank()) return
        viewModelScope.launch {
            userRepository.authentificate(login, password)
                .collect { result ->
                    if (result.status == DataResult.Status.SUCCESS) {
                        navigateToMainFragment.postValue(Unit)
                    } else {
                        Log.e("onAunthentificateButtonClicked", result.error?.statusMessage.orEmpty())
                    }
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
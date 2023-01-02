package com.example.diplom.ui.splash

import androidx.lifecycle.ViewModel
import com.example.diplom.data.prefs.PreferenceService
import com.example.diplom.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferenceService: PreferenceService,
) : ViewModel() {
    val navigateToMainFragment = SingleLiveEvent<Unit>()
    val navigateToLoginFragment = SingleLiveEvent<Unit>()

    init {
        if (preferenceService.accessToken == null) {
            navigateToLoginFragment.postValue(Unit)
        } else {
            navigateToMainFragment.postValue(Unit)
        }
    }
}
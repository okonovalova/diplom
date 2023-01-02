package com.example.diplom.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.diplom.ui.utils.BottomMenuListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val bottomMenuListener: BottomMenuListener
) : ViewModel() {
    val showBottomMenu: LiveData<Boolean> = bottomMenuListener.showBottomMenu
}
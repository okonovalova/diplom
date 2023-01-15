package com.example.diplom.ui.utils

import android.content.Context

class ResourceManagerImpl constructor(
    private val context: Context
): ResourceManager {
    override fun getString(stringRes: Int): String {
        return context.getString(stringRes)
    }
}
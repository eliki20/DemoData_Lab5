package com.example.demodata.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.demodata.data.session.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.demodata.security.PasswordHasher

class SessionViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    val isLoggedIn = sessionManager.isLoggedIn.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false
    )

    val username = sessionManager.currentUsername.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )

    val isDarkMode = sessionManager.isDarkMode.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )

    fun login(
        username: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {

        val salt = "DemoDataSalt2026".toByteArray()

        val storedHash = PasswordHasher.hash(
            password = "jkn",
            salt = salt
        )

        val inputHash = PasswordHasher.hash(
            password = password,
            salt = salt
        )

        val validUser = username == "jkn"

        val validPassword =
            PasswordHasher.constantTimeEquals(
                inputHash,
                storedHash
            )

        if (validUser && validPassword) {

            viewModelScope.launch {
                sessionManager.login(username)
                onResult(true)
            }

        } else {
            onResult(false)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            sessionManager.setDarkMode(enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.logout()
        }
    }

    class Factory(
        private val sessionManager: SessionManager
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SessionViewModel(sessionManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
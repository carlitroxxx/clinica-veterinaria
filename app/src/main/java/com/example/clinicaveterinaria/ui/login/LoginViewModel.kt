package com.example.clinicaveterinaria.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clinicaveterinaria.data.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    fun hacerLogin(
        email: String,
        password: String,
        tipo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = loginRepository.login(email, password, tipo)
            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                onError(e.message ?: "Error al iniciar sesión")
            }
        }
    }
}

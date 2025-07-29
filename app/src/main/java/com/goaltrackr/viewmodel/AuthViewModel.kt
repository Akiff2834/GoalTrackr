package com.goaltrackr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun loginOrRegister(onSuccess: () -> Unit) {
        val email = uiState.value.email.trim()
        val password = uiState.value.password.trim()

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Email ve şifre boş olamaz.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                .addOnFailureListener {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = e.localizedMessage ?: "Bir hata oluştu."
                            )
                        }
                }
        }
    }
}

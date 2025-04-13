package com.example.travelbuddy.ui.screens.code

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CodeState(
    val code: String = ""
) {
    val canSubmit get() = code.isNotBlank()
}

interface CodeActions {
    fun setCode(code: String)
}

class CodeViewModel : ViewModel() {
    private val _state = MutableStateFlow(CodeState())
    val state = _state.asStateFlow()

    val actions = object : CodeActions {
        override fun setCode(code: String) =
            _state.update { it.copy(code = code) }
    }
}
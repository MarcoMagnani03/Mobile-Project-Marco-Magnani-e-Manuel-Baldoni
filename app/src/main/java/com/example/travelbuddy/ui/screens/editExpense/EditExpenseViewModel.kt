package com.example.travelbuddy.ui.screens.editExpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.Expense
import com.example.travelbuddy.data.repositories.ExpensesRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditExpenseState(
    val tripId: Long? = null,
    val expenseId: Long? = null,
    val title: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val description: String = "",
    val errorMessage: String = "",
    val isLoading: Boolean = false,
) {
    val canSubmit: Boolean
        get() = title.isNotBlank() && amount > 0 && date.isNotBlank()
}

interface EditExpenseActions {
    fun setTripId(value: Long)
    fun setExpenseId(value: Long)
    fun setTitle(value: String)
    fun setAmount(value: Double)
    fun setDate(value: String)
    fun setDescription(value: String)
    fun setErrorMessage(value: String)
    fun setIsLoading(value: Boolean)
    fun editExpense()
    fun loadExpense()
}

class EditExpenseViewModel(
    private val expensesRepository: ExpensesRepository,
    private val userSessionRepository: UserSessionRepository
): ViewModel() {
    private val _state = MutableStateFlow(EditExpenseState())
    val state = _state.asStateFlow()

    val actions = object : EditExpenseActions {
        override fun setTripId(value: Long) {
            _state.update { it.copy(tripId = value) }
        }

        override fun setExpenseId(value: Long) {
            _state.update { it.copy(expenseId = value) }
        }

        override fun setTitle(value: String) {
            _state.update { it.copy(title = value) }
        }

        override fun setAmount(value: Double) {
            _state.update { it.copy(amount = value) }
        }

        override fun setDate(value: String) {
            _state.update { it.copy(date = value) }
        }

        override fun setDescription(value: String) {
            _state.update { it.copy(description = value) }
        }

        override fun setErrorMessage(value: String) {
            _state.update { it.copy(errorMessage = value) }
        }

        override fun setIsLoading(value: Boolean) {
            _state.update { it.copy(isLoading = value) }
        }

        override fun loadExpense() {
            viewModelScope.launch {
                val expense = state.value.expenseId?.let { expensesRepository.getExpenseById(it) }

                if (expense != null) {
                    _state.update {
                        it.copy(
                            title = expense.title,
                            amount = expense.amount,
                            date = expense.date,
                            description = expense.description ?: ""
                        )
                    }
                }
            }
        }

        override fun editExpense() {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    userSessionRepository.userEmail.collect { email ->
                        if (email != null) {
                            val newExpense = state.value.tripId?.let {
                                state.value.expenseId?.let { it1 ->
                                    Expense(
                                        id = it1,
                                        title = state.value.title,
                                        amount = state.value.amount,
                                        date = state.value.date,
                                        description = state.value.description,
                                        cretedByUserEmail = email,
                                        tripId = it
                                    )
                                }
                            }

                            newExpense.let {
                                if (it != null) {
                                    expensesRepository.upsert(it)
                                }
                            }

                            _state.value = _state.value.copy(
                                isLoading = false,
                                errorMessage = ""
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error creating the expense, try again later"
                    )
                }
            }
        }
    }
}
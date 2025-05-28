package com.example.travelbuddy.ui.screens.newExpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.Expense
import com.example.travelbuddy.data.repositories.ExpensesRepository
import com.example.travelbuddy.data.repositories.GroupsRepository
import com.example.travelbuddy.data.repositories.NotificationsRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class NewExpenseState(
    val tripId: Long? = null,
    val title: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val description: String = "",
    val errorMessage: String = "",
    val isLoading: Boolean = false,
    val newExpenseId: Long? = null
) {
    val canSubmit: Boolean
        get() = title.isNotBlank() && amount > 0 && date.isNotBlank()
}

interface NewExpenseActions {
    fun setTripId(value: Long)
    fun setTitle(value: String)
    fun setAmount(value: Double)
    fun setDate(value: String)
    fun setDescription(value: String)
    fun setErrorMessage(value: String)
    fun setIsLoading(value: Boolean)
    fun setNewExpenseId(value: Long)
    fun createExpense()
}

class NewExpenseViewModel(
    private val expensesRepository: ExpensesRepository,
    private val userSessionRepository: UserSessionRepository,
    private val notificationsRepository: NotificationsRepository,
    private val groupsRepository: GroupsRepository
): ViewModel() {
    private val _state = MutableStateFlow(NewExpenseState())
    val state = _state.asStateFlow()

    val actions = object : NewExpenseActions {
        override fun setTripId(value: Long) {
            _state.update { it.copy(tripId = value) }
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

        override fun setNewExpenseId(value: Long) {
            _state.update { it.copy(newExpenseId = value) }
        }

        override fun createExpense() {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    userSessionRepository.userEmail.collect { email ->
                        state.value.tripId?.let { it ->
                            if (email != null) {
                                val newExpense =  Expense(
                                    title = state.value.title,
                                    amount = state.value.amount,
                                    date = state.value.date,
                                    description = state.value.description,
                                    cretedByUserEmail = email,
                                    tripId = it
                                )

                                val expenseId = newExpense.let { expensesRepository.upsert(it) }

                                _state.value = _state.value.copy(
                                    newExpenseId = expenseId,
                                    isLoading = false
                                )

                                val groupEntries = groupsRepository.getGroupMembersByTripId(state.value.tripId ?: 0)
                                groupEntries.toSet().forEach { group ->
                                    notificationsRepository.addInfoNotification(description = "$email added an expense of ${state.value.amount}", title = "New Expense", userEmail = group.userEmail)
                                }
                            }
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
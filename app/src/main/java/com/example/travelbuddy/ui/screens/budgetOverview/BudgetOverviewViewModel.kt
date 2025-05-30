package com.example.travelbuddy.ui.screens.budgetOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.Expense
import com.example.travelbuddy.data.repositories.ExpensesRepository
import com.example.travelbuddy.data.repositories.GroupsRepository
import com.example.travelbuddy.data.repositories.NotificationsRepository
import com.example.travelbuddy.data.repositories.TripsRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.utils.formatTimeRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BudgetOverviewState(
    val tripId: Long = 0,
    val tripName: String = "",
    val tripDateRange: String = "",
    val totalBudget: Double = 0.0,
    val spentSoFar: Double = 0.0,
    val expenses: List<Expense> = emptyList(),
    val userContributions: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

interface BudgetOverviewActions {
    fun loadBudgetData(tripId: Long)
    fun deleteExpense(expense: Expense, tripId: Long)
}

class BudgetOverviewViewModel(
    private val tripsRepository: TripsRepository,
    private val expensesRepository: ExpensesRepository,
    private val groupsRepository: GroupsRepository,
    private val userSessionRepository: UserSessionRepository,
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BudgetOverviewState())
    val state = _state.asStateFlow()

    val actions = object : BudgetOverviewActions {
        override fun loadBudgetData(tripId: Long) {
            viewModelScope.launch {
                try {
                    _state.value = _state.value.copy(isLoading = true)

                    // Load trip details
                    val trip = tripsRepository.getTripWithAllRelationsById(tripId)

                    if (trip != null) {
                        // Calculate budget info
                        val expenses = trip.expenses
                        val totalBudget = trip.trip.budget
                        val spentSoFar = expenses.sumOf { it.amount }

                        // Get user contributions
                        val userContributions = expenses
                            .groupBy { it.cretedByUserEmail }
                            .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }

                        // Format date range
                        val dateRange = formatTimeRange(
                            trip.trip.startDate,
                            trip.trip.endDate
                        )

                        _state.value = totalBudget?.let {
                            _state.value.copy(
                                tripId = tripId,
                                tripName = trip.trip.name,
                                tripDateRange = dateRange,
                                totalBudget = it,
                                spentSoFar = spentSoFar,
                                expenses = expenses,
                                userContributions = userContributions,
                                isLoading = false
                            )
                        }!!
                    } else {
                        _state.value = _state.value.copy(
                            error = "Trip not found",
                            isLoading = false
                        )
                    }

                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = "Error loading budget data: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }

        override fun deleteExpense(expense: Expense, tripId: Long) {
            viewModelScope.launch {
                try {
                    expensesRepository.delete(expense)

                    userSessionRepository.userEmail.collect { currentUserEmail ->
                        if (currentUserEmail.isNullOrBlank()) return@collect

                        val groupEntries = groupsRepository.getGroupMembersByTripId(state.value.tripId ?: 0)
                        groupEntries.toSet().forEach { group ->
                            notificationsRepository.addInfoNotification(description = "$currentUserEmail deleted and expense", title = "Deleted expense", userEmail = group.userEmail)
                        }
                    }

                    loadBudgetData(tripId)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = "Error deleting trip: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}
package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.Expense
import com.example.travelbuddy.data.database.ExpensesDAO

class ExpensesRepository (
    private val dao: ExpensesDAO
) {
    suspend fun upsert(expense: Expense): Long = dao.upsert(expense)
    suspend fun delete(expense: Expense) = dao.delete(expense)
}
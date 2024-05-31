package com.example.dmtickets.repository

import android.content.Context
import com.example.dmtickets.MyApplication
import com.example.dmtickets.model.Ticket
import com.google.gson.Gson

object SharedPreferenceRepository {

    private const val REPOSITORY_NAME = "DMTickets"
    private const val TICKET_DATE_KEY = "TICKET_DATE_KEY"
    private const val TICKET_PRICE_KEY = "TICKET_PRICE_KEY"
    private const val TICKET_NAME_KEY = "TICKET_NAME_KEY"

    private val sharedPreferences by lazy {
        MyApplication.getInstance().getSharedPreferences(REPOSITORY_NAME, Context.MODE_PRIVATE)
    }

    private val gson by lazy {
        Gson()
    }

    fun updateTicketDate(dateList: List<Ticket>) {
        sharedPreferences.edit().apply {
            putString(TICKET_DATE_KEY, gson.toJson(dateList))
            apply()
        }
    }

    fun getTicketDate(): List<Ticket> {
        val json = sharedPreferences.getString(TICKET_DATE_KEY, "")
        return if (json.isNullOrBlank()) emptyList() else gson.fromJson(json, Array<Ticket>::class.java).toList()
    }

    fun updateTicketPrice(priceList: List<Ticket>) {
        sharedPreferences.edit().apply {
            putString(TICKET_PRICE_KEY, gson.toJson(priceList))
            apply()
        }
    }

    fun getTicketPrice(): List<Ticket> {
        val json = sharedPreferences.getString(TICKET_PRICE_KEY, "")
        return if (json.isNullOrBlank()) emptyList() else gson.fromJson(json, Array<Ticket>::class.java).toList()
    }

    fun updateTicketName(nameList: List<Ticket>) {
        sharedPreferences.edit().apply {
            putString(TICKET_NAME_KEY, gson.toJson(nameList))
            apply()
        }
    }

    fun getTicketName(): List<Ticket> {
        val json = sharedPreferences.getString(TICKET_NAME_KEY, "")
        return if (json.isNullOrBlank()) emptyList() else gson.fromJson(json, Array<Ticket>::class.java).toList()
    }
}
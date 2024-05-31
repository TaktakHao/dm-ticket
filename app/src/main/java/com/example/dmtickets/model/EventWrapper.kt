package com.example.dmtickets.model

data class EventWrapper(
    val packageName: String,
    val eventName: String,
    val eventType: Int,
) {
    override fun toString(): String {
        return "EventWrapper(packageName='$packageName', eventName='$eventName', eventType=$eventType)"
    }
}
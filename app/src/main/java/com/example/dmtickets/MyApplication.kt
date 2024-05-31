package com.example.dmtickets

import android.app.Application
import com.example.dmtickets.service.BaseService
import com.example.dmtickets.service.DMTicketService
import java.lang.Exception

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BaseService.init(
            this,
            DMTicketService::class.java
        )
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        fun getInstance(): MyApplication = synchronized(this) {
            if (instance == null) {
                throw Exception("程序尚未初始化")
            }
            return instance!!
        }
    }
}
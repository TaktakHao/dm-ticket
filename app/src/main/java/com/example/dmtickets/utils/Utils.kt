package com.example.dmtickets.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.example.dmtickets.service.BaseService

fun jumpAccessibilityServiceSettings(
    cls: Class<*>? = BaseService.serviceClass,
    ctx: Context
) {
    ctx.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val cs = ComponentName(
            ctx.packageName,
            cls?.name ?: ""
        ).flattenToString()
        putExtra(":settings:fragment_args_key", cs)
        putExtra(
            ":settings: show_fragment_args",
            Bundle().apply { putString(":settings:fragment_args_key", cs) })
    })
}

fun CharSequence?.blankOrThis() = if (this.isNullOrBlank()) "" else this.toString()
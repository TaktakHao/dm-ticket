package com.example.dmtickets

import android.accessibilityservice.AccessibilityService
import com.example.dmtickets.service.BaseService

/**
 * 全局操作快速调用
 * */
fun performAction(action: Int) = BaseService.require.performGlobalAction(action)

// 返回
fun back() = performAction(AccessibilityService.GLOBAL_ACTION_BACK)

// Home键
fun home() = performAction(AccessibilityService.GLOBAL_ACTION_HOME)

// 最近任务
fun recent() = performAction(AccessibilityService.GLOBAL_ACTION_RECENTS)

// 电源菜单
fun powerDialog() = performAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)

// 通知栏
fun notificationBar() = performAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)

// 通知栏 → 快捷设置
fun quickSettings() = performAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)


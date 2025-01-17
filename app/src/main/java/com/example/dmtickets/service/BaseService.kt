package com.example.dmtickets.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.dmtickets.R
import com.example.dmtickets.model.AnalyzeSourceResult
import com.example.dmtickets.model.EventWrapper
import com.example.dmtickets.model.NodeWrapper
import com.example.dmtickets.model.ServiceData
import com.example.dmtickets.utils.blankOrThis
import com.example.dmtickets.utils.jumpAccessibilityServiceSettings
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseService : AccessibilityService() {

    companion object {
        const val TAG = "test"
        @Volatile
        var instance: BaseService? = null
        val isServiceEnable get() = instance != null
        var serviceClass: Class<*>? = null
        private var listenEventTypeList = arrayListOf(
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        )
        private var _appContext: WeakReference<Context>? = null

        fun init(
            context: Context,
            clazz: Class<*>,
            typeList: ArrayList<Int>? = null
        ) {
            _appContext = WeakReference(context)
            serviceClass = clazz
            typeList?.let { listenEventTypeList = typeList }
        }

        /**
         * 请求无障碍服务权限，即跳转无障碍设置页
         * */
        fun requireAccessibility() {
            if (!isServiceEnable) {

                _appContext?.get()?.let {
                    jumpAccessibilityServiceSettings(ctx = it)
                }
            }
        }

        val require get() = run { requireAccessibility(); instance!! }
    }

    var currentEventWrapper: EventWrapper? = null
        private set

    var executor: ExecutorService = Executors.newFixedThreadPool(5)

    var foreNotification: Notification? = null

    private val channelId = 0x135337
    private val channelName = "抢票助手"

    abstract val enableListenApp: Boolean

    var pageState = MutableStateFlow<BookingStep>(BookingStep.Init)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onServiceConnected() {
        if (instance == null) instance = this

//        showForegroundNotification(
//            title = "守护最好的坤🐔",
//            content = "用来保活的，不用理会"
//        )
        super.onServiceConnected()
    }

    override fun onDestroy() {
        if (instance != null) instance = null
        executor.shutdown()
        closeForeNotification()
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!enableListenApp || event == null) return

        if (event.eventType in listenEventTypeList) {
            val className = event.className.blankOrThis()
            val packageName = event.packageName.blankOrThis()
            val eventType = event.eventType

            Log.d(TAG, "onAccessibilityEvent: className: $className -> packageName: $packageName -> eventType: $eventType")

            if (className.isNotBlank() && packageName.isNotBlank())
                analyzeSource (
                    EventWrapper(packageName, className, eventType),
                    50,
                    ::analyzeCallBack
                )
        }
    }

    override fun onInterrupt() {

    }

    private fun analyzeNode(
        nodeInfo: AccessibilityNodeInfo?,
        nodeInfoList: ArrayList<NodeWrapper>,
        prefix: String = "",
        isLast: Boolean  = false
    ) {
        if (nodeInfo == null) return
        val bounds = Rect()

        nodeInfo.getBoundsInScreen(bounds)
        if (!ServiceData.isFirst) {
            val marker = if (isLast) """\--- """ else "+--- "
            val currentPrefix = "$prefix$marker"
            Log.d("printNodeInfo", currentPrefix + "classNome: ${nodeInfo.className} -> id: ${nodeInfo.viewIdResourceName} -> text: ${nodeInfo.text} -> isClickable: ${nodeInfo.isClickable}   bounds: $bounds")
        }

        nodeInfoList.add(
            NodeWrapper(
                text = nodeInfo.text.blankOrThis(),
                id = nodeInfo.viewIdResourceName,
                bounds = bounds,
                classNome = nodeInfo.className.blankOrThis(),
                description = nodeInfo.contentDescription.blankOrThis(),
                clickable = nodeInfo.isClickable,
                scrollable = nodeInfo.isScrollable,
                editable = nodeInfo.isEditable,
                nodeInfo = nodeInfo
            )
        )
        if (nodeInfo.childCount > 0) {
            val childPrefix = prefix + if (isLast) "  " else "|  "
            val lastChildIndex = nodeInfo.childCount - 1
            for (index in 0 until nodeInfo.childCount) {
                val isLastChild = index == lastChildIndex
                analyzeNode(
                    nodeInfo.getChild(index),
                    nodeInfoList,
                    childPrefix,
                    isLastChild
                )
            }
        }

    }

    private fun analyzeSource(
        wrapper: EventWrapper? = null,
        waitTime: Long = 5000L,
        callback: ((EventWrapper?, AnalyzeSourceResult) -> Unit)? = null
    ) {
        executor.execute {
            Thread.sleep(waitTime)
            currentEventWrapper = wrapper
            val analyzeSourceResult = AnalyzeSourceResult(arrayListOf())
            analyzeNode(rootInActiveWindow, analyzeSourceResult.nodes)
            if (!ServiceData.isFirst) {
                ServiceData.isFirst = true
            }
            callback?.invoke(currentEventWrapper, analyzeSourceResult)
        }
    }

    open fun analyzeCallBack(wrapper: EventWrapper?, result: AnalyzeSourceResult) {}

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.O)
    fun showForegroundNotification(
        title: String = "抢票小助手",
        content: String = "通知内容",
        ticker: String = "通知提示语",
        icon: Int = R.drawable.icon_logo,
        cls: Class<*>? = null
    ) {
        val notificationManager =
            instance?.getSystemService(Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
        notificationManager?.createNotificationChannel(
            NotificationChannel(
                "$channelId",
                channelName,
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                description = "该通知仅用于程序保活"
                lightColor = Color.GREEN
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
        )

        foreNotification = _appContext?.get()?.let {
            NotificationCompat.Builder(it, "$channelId").apply {
                setSmallIcon(icon)
                setContentTitle(title)
                setContentText(content)
                setTicker(ticker)
                cls?.let { clazz ->
                    setContentIntent(
                        PendingIntent.getActivity(
                            instance,
                            0,
                            Intent(instance, clazz),
                            PendingIntent.FLAG_MUTABLE
                        )
                    )
                }
            }.build()
        }
        instance?.startForeground(channelId, foreNotification)
    }

    fun closeForeNotification() {
        foreNotification?.let { instance?.stopForeground(STOP_FOREGROUND_REMOVE) }
    }
}

sealed class BookingStep {
    data object Init : BookingStep()
    data object WaitingToBuy : BookingStep()
    data object SelectingSeat : BookingStep()
    data object Pay : BookingStep()
}
package com.example.dmtickets.model

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.example.dmtickets.service.BaseService
import com.example.dmtickets.service.BaseService.Companion.TAG
import kotlin.math.log

data class NodeWrapper(
    var text: String? = null,
    var id: String? = null,
    var bounds: Rect? = null,
    var classNome: String,
    var description: String? = null,
    var clickable: Boolean = false,
    var scrollable: Boolean = false,
    var editable: Boolean = false,
    var nodeInfo: AccessibilityNodeInfo? = null,
    var childList: List<NodeWrapper>? = null
) {
    override fun toString(): String {
        return "NodeWrapper(text=$text, id=$id, bounds=$bounds, glassNome='$classNome', description=$description, clickable=$clickable, scrollable=$scrollable, editable=$editable, nodeInfo=$nodeInfo)"
    }
}

/**
 * 结点操作快速调用
 * */
fun NodeWrapper?.click(gestureClick: Boolean = true, duration: Long = 700L) {
    if (this == null) return
    if (gestureClick) {
        bounds?.let {
            val x = ((it.left + it.right) / 2).toFloat()
            val y = ((it.top + it.bottom) / 2).toFloat()
            BaseService.require.dispatchGesture(
                GestureDescription.Builder().apply {
                    val path = Path().apply {
                        moveTo(x, y)
                        lineTo(x, y + 200L)
                    }
                    addStroke(GestureDescription.StrokeDescription(path, 20L, duration))
                }.build(), object : AccessibilityService.GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        super.onCompleted(gestureDescription)
                        // 手势执行完成回调
                        Log.d(TAG, "onCompleted: ")
                    }

                    override fun onCancelled(gestureDescription: GestureDescription?) {
                        super.onCancelled(gestureDescription)
                        Log.d(TAG, "onCancelled: ")
                    }
                }, null
            )
        }
    } else {
        nodeInfo?.let {
            var depthCount = 0  // 查找最大深度
            var tempNode = it
            while (true) {
                if (depthCount < 10) {
                    if (tempNode.isClickable) {
                        if (duration >= 1000L) {
                            tempNode.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
                        } else {
                            tempNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        }
                        break
                    } else {
                        tempNode = tempNode.parent
                        depthCount++
                        Log.d(TAG, "click: depthCount:  ${depthCount}   ${tempNode.parent}")
                    }
                } else break
            }
        }
    }
}

/**
 * 预约或者购买节点操作（找不到相应的节点信息，通过偏移实现）
 * */
fun NodeWrapper?.buyClick(duration: Long = 200L) {
    if (this == null) return
    bounds?.let {
        val x = ((it.left + it.right + 500) / 2).toFloat()
        val y = ((it.top + it.bottom) / 2).toFloat()

        Log.d(TAG, "buyClick: $x  $y")
        try {
            BaseService.require.dispatchGesture(
                GestureDescription.Builder().apply {
                    addStroke(GestureDescription.StrokeDescription(Path().apply { moveTo(x, y) }, 0L, 100))
                }.build(), object : AccessibilityService.GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        super.onCompleted(gestureDescription)
                        // 手势执行完成回调
                    }

                    override fun onCancelled(gestureDescription: GestureDescription?) {
                        gestureDescription
                        super.onCancelled(gestureDescription)
                    }
                }, null
            )
        } catch (e: Exception) {
            Log.d(TAG, "buyClick: ${e.message}")
        }


    }
}

/**
 * 结点长按
 */
fun NodeWrapper?.longClick(gestureClick: Boolean = true, duration: Long = 1000L) {
    if (this == null) return
    click(gestureClick, duration)
}

/**
 * 向前滑动
 */
fun NodeWrapper?.scrollForward(isForward: Boolean = true) {
    if (this == null) return
    nodeInfo?.let {
        var depthCount = 0  // 查找最大深度
        var tempNode = it
        while (true) {
            if (depthCount < 10) {
                if (tempNode.isScrollable) {
                    tempNode.performAction(
                        if (isForward) AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
                        else AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
                    )
                    break
                } else {
                    tempNode = tempNode.parent
                    depthCount++
                }
            } else break
        }
    }
}

/**
 * 向后滑动
 */
fun NodeWrapper?.backward() = scrollForward(false)

/**
 * 从一个坐标点滑动到另一个坐标点
 */
fun NodeWrapper?.swipe(
    startX: Int,
    startY: Int,
    endX: Int,
    endY: Int,
    duration: Long = 1000L
) {
    BaseService.require.dispatchGesture(
        GestureDescription.Builder().apply {
            addStroke(GestureDescription.StrokeDescription(Path().apply {
                moveTo(startX.toFloat(), startY.toFloat())
                lineTo(endX.toFloat(), endY.toFloat())
            }, 0L, duration))
        }.build(), object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                // 手势执行完成回调
            }
        }, null
    )
}

/**
 * 文本填充
 */
fun NodeWrapper?.input(content: String) {
    if (this == null) return
    nodeInfo?.let {
        var depthCount = 0  // 查找最大深度
        var tempNode = it
        while (true) {
            if (depthCount < 10) {
                if (tempNode.isEditable) {
                    tempNode.performAction(
                        AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
                            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content)
                        }
                    )
                    break
                } else {
                    tempNode = tempNode.parent
                    depthCount++
                }
            } else break
        }
    }
}
package com.example.dmtickets.service

import android.util.Log
import com.example.dmtickets.model.AnalyzeSourceResult
import com.example.dmtickets.model.EventWrapper
import com.example.dmtickets.model.ServiceData
import com.example.dmtickets.model.buyClick
import com.example.dmtickets.model.click
import com.example.dmtickets.model.findAllClickableNode
import com.example.dmtickets.model.findAllTextNode
import com.example.dmtickets.model.findAllTextViewNode
import com.example.dmtickets.model.findEditableNode
import com.example.dmtickets.model.findNodeById
import com.example.dmtickets.model.findNodeByText
import com.example.dmtickets.model.performScroll

class DMTicketService : BaseService() {

    override val enableListenApp: Boolean = true

    override fun analyzeCallBack(wrapper: EventWrapper?, result: AnalyzeSourceResult) {
        if (!ServiceData.isServiceEnable) return

        if (result.findNodeByText("猫眼演出详情") != null) {
            pageState.value = BookingStep.WaitingToBuy
        } else if (result.findNodeByText("选择票档") != null) {
            pageState.value = BookingStep.SelectingSeat
        } else if (result.findNodeByText("确认购票") != null) {
            pageState.value = BookingStep.Pay
        } else {
            pageState.value = BookingStep.Init
        }

        when (pageState.value) {
            BookingStep.Init -> {
                result.findNodeByText("我的")?.click(gestureClick = false)

                result.findNodeByText("我的预约抢票").click(gestureClick = false)

                val singer = ServiceData.singer.firstOrNull { it.isChecked } ?: return
                result.findNodeByText(singer.data).click(gestureClick = false)
            }
            BookingStep.WaitingToBuy -> {
                if (result.findNodeByText("已预约") != null) return

                result.findNodeByText("确认并悉知")?.let {
                    performScroll()
                    it.click(gestureClick = false)
                }

                result.findNodeByText("立即预订")?.click(gestureClick = true)
            }
            BookingStep.SelectingSeat -> {
                val selectedDate = ServiceData.allDateList.filter { it.isChecked }
                val selectedPrice = ServiceData.allPriceList.filter { it.isChecked }

                selectedDate.forEach {
                    val node = result.findNodeByText(it.data) ?: return@forEach
                    if (node.text?.contains("缺货") == true) return@forEach
                    node.click(gestureClick = false)
                    return
                }

                selectedPrice.forEach {
                    val node = result.findNodeByText(it.data) ?: return@forEach
                    if (node.text?.contains("缺货") == true) return@forEach
                    node.click(gestureClick = false)
                    return
                }

                result.findNodeByText("确认").click(gestureClick = false)
            }

            BookingStep.Pay -> {
                val name = ServiceData.allNameList.filter { it.isChecked }
                val textviewResult = result.findAllTextViewNode()
                name.forEach {
                    textviewResult.findNodeByText(it.data)?.click(gestureClick = false)
                }

                result.findNodeByText("立即支付").click(gestureClick = false)
            }
        }



//        result.findAllClickableNode()


    }
}
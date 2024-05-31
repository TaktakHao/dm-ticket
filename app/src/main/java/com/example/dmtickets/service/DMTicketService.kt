package com.example.dmtickets.service

import android.util.Log
import com.example.dmtickets.model.AnalyzeSourceResult
import com.example.dmtickets.model.EventWrapper
import com.example.dmtickets.model.ServiceData
import com.example.dmtickets.model.buyClick
import com.example.dmtickets.model.click
import com.example.dmtickets.model.findAllClickableNode
import com.example.dmtickets.model.findNodeById
import com.example.dmtickets.model.findNodeByText

class DMTicketService : BaseService() {

    override val enableListenApp: Boolean = true

    override fun analyzeCallBack(wrapper: EventWrapper?, result: AnalyzeSourceResult) {
        if (!ServiceData.isServiceEnable) return
//        result.findNodeByText("我的").apply {
////            Log.d("test","node: $this")
//        }.click()
//        result.findNodeByText("想看&想玩").apply {
////            Log.d("test","node: $this")
//        }.click()

//        result.findNodeByText("我想看").apply {
//            Log.d(TAG, "analyzeCallBack:  ${this?.bounds}")
//        }.buyClick()

        result.findNodeByText("预约抢票 快人一步").click(gestureClick = false)

        result.findAllClickableNode()


    }
}
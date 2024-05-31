package com.example.dmtickets.model

object ServiceData {

    val allDateList: ArrayList<Ticket> = ArrayList()

    val allPriceList: ArrayList<Ticket> = ArrayList()

    val allNameList: ArrayList<Ticket> = ArrayList()

    var isServiceEnable = false

    var isFirst = false
}

data class Ticket(
    val data: String,
    var isChecked: Boolean = true
)

sealed class OperationType {
    data object Add : OperationType()
    data object Delete : OperationType()
    data object UpdateSelection : OperationType()
}

sealed class DataType {
    data object Date : DataType()
    data object Name : DataType()
    data object Price : DataType()
}

package com.example.dmtickets.model

import android.util.Log
import com.example.dmtickets.service.BaseService.Companion.TAG
import com.example.dmtickets.utils.blankOrThis

data class AnalyzeSourceResult(
    val nodes: ArrayList<NodeWrapper> = arrayListOf()
)


/**
 * 根据文本查找结点
 *
 * @param text 匹配的文本
 * @param textAllMatch 文本全匹配
 * @param includeDesc 同时匹配desc
 * @param descAllMatch desc全匹配
 * @param enableRegular 是否启用正则
 * */
fun AnalyzeSourceResult.findNodeByText(
    text: String,
    textAllMatch: Boolean = false,
    includeDesc: Boolean = false,
    descAllMatch: Boolean = false,
    enableRegular: Boolean = false,
): NodeWrapper? {
    if (enableRegular) {
        val regex = Regex(text)
        nodes.forEach { node ->
            if (!node.text.isNullOrBlank()) {
                if (regex.find(node.text.blankOrThis()) != null) return node
            }
            if (includeDesc && !node.description.isNullOrBlank()) {
                if (regex.find(node.description.blankOrThis()) != null) return node
            }
        }
    } else {
        nodes.forEach { node ->
            if (!node.text.isNullOrBlank()) {
                if (textAllMatch) {
                    if (text == node.text) return node
                } else {
                    if (node.text.blankOrThis().contains(text)) return node
                }
            }
            if (includeDesc && !node.description.isNullOrBlank()) {

                if (descAllMatch) {
                    if (text == node.description) return node
                } else {
                    if (node.description.blankOrThis().contains(text)) return node
                }
            }
        }
    }
    return null
}

/**
 * 根据id查找结点
 *
 * @param id 结点id
 * */
fun AnalyzeSourceResult.findNodeById(id: String): NodeWrapper? {
    nodes.forEach { node ->
        if (!node.id.isNullOrBlank()) {
            if (node.id == id) return node
        }
    }
    return null
}

/**
 * 根据传入的表达式结果查找结点
 *
 * @param expression 匹配条件表达式
 * */
fun AnalyzeSourceResult.findNodeByExpression(expression: (NodeWrapper) -> Boolean): NodeWrapper? {
    nodes.forEach { node ->
        if (expression.invoke(node)) return node
    }
    return null
}

/**
 * 查找所有文本不为空的结点
 * */
fun AnalyzeSourceResult.findAllTextNode(includeDesc: Boolean = false): AnalyzeSourceResult {
    val result = AnalyzeSourceResult()
    nodes.forEach { node ->
        if (!node.text.isNullOrBlank()) {
            result.nodes.add(node)
            return@forEach
        }
        if (includeDesc && !node.description.isNullOrBlank()) {
            result.nodes.add(node)
            return@forEach
        }
    }
    return result
}

fun AnalyzeSourceResult.findEditableNode(): NodeWrapper? {
    nodes.forEach { node ->
        if (node.editable) return node
    }
    return null
}

/**
 * 查找所有可点击的结点
 * */
fun AnalyzeSourceResult.findAllClickableNode(): AnalyzeSourceResult {
    val result = AnalyzeSourceResult()
    nodes.forEach { node ->
        if (node.clickable) {
            result.nodes.add(node)
            return@forEach
        }
    }
    return result
}
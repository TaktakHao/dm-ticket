package com.example.dmtickets.widget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.example.dmtickets.R
import com.example.dmtickets.databinding.LayoutFloatViewBinding
import com.example.dmtickets.model.ServiceData
import com.example.dmtickets.service.BaseService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class FloatingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding by lazy {
        LayoutFloatViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
    }


    private var mMoveListener: ((moveX: Float, moveY: Float) -> Unit)? = null

    init {

        initClickListener()

        checkPermission()

        if (ServiceData.isServiceEnable) {
            binding.tvSwitch.text = "点击停止"
        } else {
            binding.tvSwitch.text = "点击开始"
        }
    }


    private fun initClickListener() {
        binding.tvSwitch.setOnClickListener {

            if (ServiceData.isServiceEnable) {
                ServiceData.isServiceEnable = false
                binding.tvSwitch.text = "点击开始"
            } else {
                ServiceData.isServiceEnable = true
                ServiceData.isFirst = false
                binding.tvSwitch.text = "点击停止"
            }
        }
        binding.tvGoToApp.setOnClickListener {
            context.applicationContext.startActivity(Intent(Intent.ACTION_VIEW).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                addFlags(FLAG_ACTIVITY_NEW_TASK)
                component =
                    ComponentName(context.packageName, "${context.packageName}.MainActivity")
            })
        }
        binding.btnReqPer.setOnClickListener {
            context.applicationContext.startActivity(Intent(Intent.ACTION_VIEW).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                addFlags(FLAG_ACTIVITY_NEW_TASK)
                component =
                    ComponentName(context.packageName, "${context.packageName}.MainActivity")
            })
            if (!BaseService.isServiceEnable) {
                BaseService.requireAccessibility()
            }
        }

        binding.tvGoToDm.setOnClickListener {
            try {
                context?.let {
                    val intent = it.packageManager.getLaunchIntentForPackage(it.getString(R.string.my_package))
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        it.startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "请手动打开大麦app", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkPermission() {

        if (!BaseService.isServiceEnable) {
            binding.tvTips.visibility = View.VISIBLE
            binding.btnReqPer.visibility = View.VISIBLE
            binding.tvSwitch.visibility = View.GONE
        } else {
            binding.tvTips.visibility = View.GONE
            binding.btnReqPer.visibility = View.GONE
            binding.tvSwitch.visibility = View.VISIBLE
        }
    }

    fun showCurrTime() {
        val formatter = SimpleDateFormat("YYYY-MM-dd HH:mm:ss") //设置时间格式


        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08")) //设置时区


        val curDate = Date(System.currentTimeMillis()) //获取当前时间


        val createDate: String = formatter.format(curDate) //格式转换

        binding.tvTips.text = createDate
    }

    fun setOnMoveListener(listener: (moveX: Float, moveY: Float) -> Unit) {
        mMoveListener = listener
    }

    private var mDownX: Float = 0f
    private var mDownY: Float = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.rawX
                mDownY = event.rawY
            }

            MotionEvent.ACTION_MOVE -> {
                val moveX = event.rawX - mDownX
                val moveY = event.rawY - mDownY

                mMoveListener?.invoke(moveX, moveY)

                mDownX = event.rawX
                mDownY = event.rawY
            }
        }
        return true
    }


}
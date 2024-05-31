package com.example.dmtickets

import android.content.ComponentName
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.dmtickets.TicketMessageFragment.Companion.TICKET_MESSAGE_FRAGMENT_KEY
import com.example.dmtickets.databinding.ActivityMainBinding
import com.example.dmtickets.service.BaseService
import com.example.dmtickets.view_model.MainActivityViewModel
import com.example.dmtickets.view_model.MainViewModelFactory
import com.example.dmtickets.widget.FloatingView
import com.example.dmtickets.widget.HomeTitleUIState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var mFloatView: FloatingView? = null
    private var mWindowManager: WindowManager? = null
    private val mLayoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()

    private val ivBack by lazy {
        AppCompatImageView(this).apply {
            setImageResource(R.drawable.icon_back)
        }
    }

    private val ivNewLogo by lazy {
        AppCompatImageView(this).apply {
            setImageResource(R.drawable.icon_logo)
        }
    }

    private val tvTitle by lazy {
        AppCompatTextView(this).apply {
            text = "大麦助手"
        }
    }

    private val ivNewProfile by lazy {
        AppCompatImageView(this)
    }

    private val floatingWindowSettingResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            if (Settings.canDrawOverlays(this)) {
                showWindow()
            }
        }

    private val viewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory())[MainActivityViewModel::class.java]
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val fragmentManager = supportFragmentManager
            if (fragmentManager.backStackEntryCount > 1) {
                fragmentManager.popBackStack()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(backPressedCallback)

        initClick()

        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.navigationFlow.collectLatest {
                    when (it) {
                        TICKET_MESSAGE_FRAGMENT_KEY -> navigateToFragment(TicketMessageFragment.newInstance())
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.titleBarFlow.collectLatest {
                    updateNewTitle(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mFloatView?.checkPermission()
    }

    override fun onDestroy() {
        mFloatView?.let {
            mWindowManager?.removeView(it)
        }
        super.onDestroy()
    }

    private fun initClick() {
        binding.btnGo.setOnClickListener {
            turnOnFloatingWindow()
            BaseService.requireAccessibility()
            viewModel.navigate(TICKET_MESSAGE_FRAGMENT_KEY)
        }

        ivBack.setOnClickListener {
            back()
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fl_container, fragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun turnOnFloatingWindow() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.packageName)
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val cs = ComponentName(
                    packageName,
                    localClassName
                ).flattenToString()
                putExtra(":settings:fragment_args_key", cs)
                putExtra(
                    ":settings: show_fragment_args",
                    Bundle().apply { putString(":settings:fragment_args_key", cs) })
            }
            floatingWindowSettingResult.launch(intent)
        } else {
            showWindow()
        }
    }

    private fun showWindow() {
        if (mFloatView != null) {
            return
        }
        // 获取 WindowManager
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // 创建一个悬浮窗口 View
        mFloatView = FloatingView(this)
        mFloatView?.setOnMoveListener { moveX, moveY ->
            val targetX = mLayoutParams.x - moveX.toInt()
            val targetY = mLayoutParams.y - moveY.toInt()
            mLayoutParams.x = targetX
            mLayoutParams.y = targetY
            mWindowManager?.updateViewLayout(mFloatView, mLayoutParams)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        mLayoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.or(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                .or(
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                )
        mLayoutParams.gravity = Gravity.BOTTOM or Gravity.END // y 指的是底部边框到屏幕底部距离， x指的时右边框到右屏幕的距离

        mLayoutParams.y = 100//默认显示右下角
        mLayoutParams.x = 100
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams.format = PixelFormat.RGBA_8888
        mWindowManager?.addView(mFloatView, mLayoutParams)
    }

    private fun updateNewTitle(titleUIState: HomeTitleUIState?) {
        if (titleUIState == null) return

        if (titleUIState.showLogo) {
            ivNewLogo.setVisibility(View.VISIBLE)
            tvTitle.visibility = View.GONE
        } else {
            ivNewLogo.setVisibility(View.GONE)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = titleUIState.titleText
        }
        if (titleUIState.showAvatar) {
            ivNewProfile.setVisibility(View.VISIBLE)
        } else {
            ivNewProfile.setVisibility(View.GONE)
        }
        if (titleUIState.showAppTopBar) {
            binding.titleBar.visibility = View.VISIBLE
        } else {
            binding.titleBar.visibility = View.GONE
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            ivNewLogo.visibility = View.VISIBLE
        } else {
            ivNewLogo.visibility = View.GONE
        }
    }

}
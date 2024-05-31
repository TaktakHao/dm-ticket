package com.example.dmtickets.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.dmtickets.databinding.AppTitleBarBinding
import java.io.Serializable

class TitleBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var binding: AppTitleBarBinding? = null

    init {
        binding = AppTitleBarBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun loadLeft(vararg views: View) {

        binding?.llLeft?.removeAllViews()
        if (views.isEmpty()) return

        for (view in views) {
            binding?.llLeft?.addView(view)
            view.layoutParams.apply {
                (this as LinearLayout.LayoutParams).gravity = Gravity.CENTER_VERTICAL
            }
        }
    }

    fun loadCenter(vararg views: View) {

        binding?.llCenter?.removeAllViews()
        if (views.isEmpty()) return

        for (view in views) {
            binding?.llCenter?.addView(view)
            view.layoutParams.apply {
                (this as LinearLayout.LayoutParams).gravity = Gravity.CENTER_VERTICAL
            }
        }
    }

    fun loadRight(vararg views: View) {

        binding?.llRight?.removeAllViews()
        if (views.isEmpty()) return

        for (view in views) {
            binding?.llRight?.addView(view)
            view.layoutParams.apply {
                (this as LinearLayout.LayoutParams).gravity = Gravity.CENTER_VERTICAL
            }
        }
    }

}

open class HomeTitleUIState(
    val showLogo: Boolean,
    val titleText: String,
    val showAvatar: Boolean,
    val showAppTopBar: Boolean,
) : Serializable {

    companion object {

        fun getDefaultTitleWithAvatarUIState() = HomeTitleUIState(
            showLogo = true,
            titleText = "",
            showAvatar = false,
            showAppTopBar = true,
        )

        fun getDefaultTitleUIState(title: String) = HomeTitleUIState(
            showLogo = false,
            titleText = title,
            showAvatar = false,
            showAppTopBar = true,
        )


        fun getEmptyBarUIState(title: String) = HomeTitleUIState(
            showLogo = false,
            titleText = title,
            showAvatar = false,
            showAppTopBar = false,
        )

        fun getTabDefaultTitleUIState() = HomeTitleUIState(
            showLogo = true,
            titleText = "",
            showAvatar = true,
            showAppTopBar = true,
        )
    }
}
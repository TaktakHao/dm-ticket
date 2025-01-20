package com.example.dmtickets

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.dmtickets.databinding.FragmentTicketMessageBinding
import com.example.dmtickets.model.DataType
import com.example.dmtickets.model.OperationType
import com.example.dmtickets.model.ServiceData
import com.example.dmtickets.model.Ticket
import com.example.dmtickets.repository.SharedPreferenceRepository
import com.example.dmtickets.view_model.MainActivityViewModel
import com.example.dmtickets.view_model.MainViewModelFactory
import com.example.dmtickets.widget.HomeTitleUIState
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class TicketMessageFragment : BaseFragment<FragmentTicketMessageBinding>() {

    private val parentViewModel by lazy {
        activity?.let {
            ViewModelProvider(it, MainViewModelFactory())[MainActivityViewModel::class.java]
        }
    }
    override val layoutId: Int = R.layout.fragment_ticket_message

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentViewModel?.updateTitleBar(HomeTitleUIState.getDefaultTitleWithAvatarUIState())

        initChipGroup()

        setupClickListener()
    }

    private fun initChipGroup() {
        binding.chipDate.removeAllViews()
        binding.chipPrice.removeAllViews()
        binding.chipName.removeAllViews()

        ServiceData.allDateList.clear()
        ServiceData.allPriceList.clear()
        ServiceData.allNameList.clear()

        val singerList = SharedPreferenceRepository.getSingerDate()
        val dateList = SharedPreferenceRepository.getTicketDate()
        val priceList = SharedPreferenceRepository.getTicketPrice()
        val nameList = SharedPreferenceRepository.getTicketName()

        if (singerList.isNotEmpty()) {
            ServiceData.singer.addAll(singerList)
            singerList.forEach {
                val chipView = generateChip(it, DataType.Singer)
                binding.chipSinger.addView(chipView)
            }
        }

        if (dateList.isNotEmpty()) {
            ServiceData.allDateList.addAll(dateList)
            dateList.forEach {
                val chipView = generateChip(it, DataType.Date)
                binding.chipDate.addView(chipView)
            }
        }

        if (priceList.isNotEmpty()) {
            ServiceData.allPriceList.addAll(priceList)
            priceList.forEach {
                val chipView = generateChip(it, DataType.Price)
                binding.chipPrice.addView(chipView)
            }
        }

        if (nameList.isNotEmpty()) {
            ServiceData.allNameList.addAll(nameList)
            nameList.forEach {
                val chipView = generateChip(it, DataType.Name)
                binding.chipName.addView(chipView)
            }
        }

    }


    private fun setupClickListener() {

        binding.btnOpenDamai.setOnClickListener {
            try {

                context?.let {
                    val intent = it.packageManager.getLaunchIntentForPackage(it.getString(R.string.my_package))
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        it.startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "请手动打开猫眼app", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAddDate.setOnClickListener {
            val datePickerDialog = context?.let { it1 -> DatePickerDialog(it1) }
            datePickerDialog?.setOnDateSetListener { view, year, month, dayOfMonth ->
                val monthStr = if (month+1 < 10) {
                    "0${month+1}"
                } else {
                    (month+1).toString()
                }
                val dayStr = if (dayOfMonth < 10) {
                    "0${dayOfMonth}"
                } else {
                    dayOfMonth.toString()
                }

                val ticket = Ticket("$year-$monthStr-$dayStr")
                val chipView = generateChip(ticket, DataType.Date)
                binding.chipDate.addView(chipView)
                parentViewModel?.updateTicketData(ticket, OperationType.Add, DataType.Date)

            }
            datePickerDialog?.show()
        }

        binding.singerTextField.setEndIconOnClickListener {
            val name = binding.singerTextField.editText?.text?.toString()
                ?: return@setEndIconOnClickListener
            binding.singerTextField.editText?.text?.clear()

            val ticket = Ticket(name)
            val chipView = generateChip(ticket, DataType.Singer)
            binding.chipSinger.addView(chipView)
            parentViewModel?.updateTicketData(ticket, OperationType.Add, DataType.Singer)
        }

        binding.priceTextField.setEndIconOnClickListener {
            val price = binding.priceTextField.editText?.text?.toString()?.toInt()
                ?: return@setEndIconOnClickListener
            binding.priceTextField.editText?.text?.clear()

            val ticket = Ticket("${price}元")
            val chipView = generateChip(ticket, DataType.Price)
            binding.chipPrice.addView(chipView)
            parentViewModel?.updateTicketData(ticket, OperationType.Add, DataType.Price)
        }

        binding.nameTextField.setEndIconOnClickListener {
            val name = binding.nameTextField.editText?.text?.toString()
                ?: return@setEndIconOnClickListener
            binding.nameTextField.editText?.text?.clear()

            val ticket = Ticket(name)
            val chipView = generateChip(ticket, DataType.Name)
            binding.chipName.addView(chipView)
            parentViewModel?.updateTicketData(ticket, OperationType.Add, DataType.Name)
        }
    }

    private fun generateChip(
        ticket: Ticket,
        type: DataType,
    ): Chip {
        val chipView = Chip(context)
        chipView.isCheckedIconVisible = true
        chipView.checkedIcon = ResourcesCompat.getDrawable(resources, R.drawable.icon_check, null)
        chipView.isCheckable = true
        chipView.isChecked = ticket.isChecked
        chipView.text = ticket.data
        chipView.setOnClickListener {
            parentViewModel?.updateTicketData(ticket, OperationType.UpdateSelection, type)

        }
        chipView.setOnLongClickListener {
            context?.let { ctx ->
                MaterialAlertDialogBuilder(ctx)
                    .setTitle("删除")
                    .setMessage("确定要删除该条目吗")
                    .setNeutralButton("取消") { dialog, which ->
                    }
                    .setPositiveButton("删除") { dialog, which ->
                        when (type) {
                            DataType.Singer -> {
                                binding.chipSinger.removeView(chipView)
                            }

                            DataType.Date -> {
                                binding.chipDate.removeView(chipView)
                            }

                            DataType.Price -> {
                                binding.chipPrice.removeView(chipView)
                            }

                            DataType.Name -> {
                                binding.chipName.removeView(chipView)
                            }
                        }
                        parentViewModel?.updateTicketData(ticket, OperationType.Delete, type)
                    }
                    .show()
            }

            true
        }
        return chipView
    }

    companion object {

        const val TICKET_MESSAGE_FRAGMENT_KEY = "TICKET_MESSAGE_FRAGMENT_KEY"

        @JvmStatic
        fun newInstance() =
            TicketMessageFragment()
    }
}
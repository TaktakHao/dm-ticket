package com.example.dmtickets.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dmtickets.model.DataType
import com.example.dmtickets.model.OperationType
import com.example.dmtickets.model.Ticket
import com.example.dmtickets.model.ServiceData
import com.example.dmtickets.repository.SharedPreferenceRepository
import com.example.dmtickets.widget.HomeTitleUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {

    private val _navigationFlow = MutableStateFlow("")
    val navigationFlow: StateFlow<String> = _navigationFlow
    fun navigate(route: String) {
        _navigationFlow.value = route
    }

    private val _titleBarFlow =
        MutableStateFlow(HomeTitleUIState.getDefaultTitleWithAvatarUIState())
    val titleBarFlow: StateFlow<HomeTitleUIState> = _titleBarFlow
    fun updateTitleBar(uiState: HomeTitleUIState) {
        _titleBarFlow.value = uiState
    }

    fun updateTicketData(data: Ticket, type: OperationType, dataType: DataType) {

        when (type) {
            OperationType.Add -> {
                when (dataType) {
                    DataType.Date -> {
                        ServiceData.allDateList.add(data)
                        SharedPreferenceRepository.updateTicketDate(ServiceData.allDateList)
                    }

                    DataType.Price -> {
                        ServiceData.allPriceList.add(data)
                        SharedPreferenceRepository.updateTicketPrice(ServiceData.allPriceList)
                    }

                    DataType.Name -> {
                        ServiceData.allNameList.add(data)
                        SharedPreferenceRepository.updateTicketName(ServiceData.allNameList)
                    }
                }
            }

            OperationType.Delete -> {
                when (dataType) {
                    DataType.Date -> {
                        ServiceData.allDateList.remove(data)
                        SharedPreferenceRepository.updateTicketDate(ServiceData.allDateList)
                    }

                    DataType.Price -> {
                        ServiceData.allPriceList.remove(data)
                        SharedPreferenceRepository.updateTicketPrice(ServiceData.allPriceList)
                    }

                    DataType.Name -> {
                        ServiceData.allNameList.remove(data)
                        SharedPreferenceRepository.updateTicketName(ServiceData.allNameList)
                    }
                }
            }

            OperationType.UpdateSelection -> {
                data.isChecked = !data.isChecked

                when (dataType) {
                    DataType.Date -> {
                        SharedPreferenceRepository.updateTicketDate(ServiceData.allDateList)
                    }

                    DataType.Price -> {
                        SharedPreferenceRepository.updateTicketPrice(ServiceData.allPriceList)
                    }

                    DataType.Name -> {
                        SharedPreferenceRepository.updateTicketName(ServiceData.allNameList)
                    }
                }
            }
        }
    }
}
package com.example.diplom.ui.home.jobs.add_job

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.repository.JobRepository
import com.example.diplom.ui.utils.BottomMenuListener
import com.example.diplom.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddJobViewModel @Inject constructor(
    private val bottomMenuListener: BottomMenuListener,
    private val jobRepository: JobRepository,
) : ViewModel() {
    private val _nameJobError: MutableLiveData<Boolean> = MutableLiveData()
    val nameJobError: LiveData<Boolean>
        get() = _nameJobError

    private val _positionError: MutableLiveData<Boolean> = MutableLiveData()
    val positionError: LiveData<Boolean>
        get() = _positionError

    private val _jobDateStartTime: MutableLiveData<String> = MutableLiveData("")
    val jobDateStartTime: LiveData<String>
        get() = _jobDateStartTime

    private val _jobDateFinishTime: MutableLiveData<String?> = MutableLiveData(null)
    val jobDateFinishTime: LiveData<String?>
        get() = _jobDateFinishTime

    private val _jobDateFinishCheckbox: MutableLiveData<Boolean> = MutableLiveData(false)
    val jobDateFinishCheckbox: LiveData<Boolean>
        get() = _jobDateFinishCheckbox

    private var nameJob: String = ""
    private var position: String = ""
    private var link: String? = null
    val navigateToMainFragment = SingleLiveEvent<Unit>()


    fun onInitView() {
        bottomMenuListener.showBottomMenu.postValue(false)
    }

    fun onDoneButtonClicked() {
        if (nameJob.isBlank()) return
        viewModelScope.launch {
            val dateStartTime = jobDateStartTime.value
            val dateFinishTime = jobDateFinishTime.value
            val result = jobRepository.createJob(nameJob, position, dateStartTime.orEmpty(), dateFinishTime, link)
            if (result.status == DataResult.Status.SUCCESS) {
                navigateToMainFragment.postValue(Unit)
            } else {
                Log.e("onDoneButtonClicked", result.error?.statusMessage.orEmpty())
            }
        }
    }

    fun onChangeNameJob(nameJob: String) {
        this.nameJob = nameJob
        _nameJobError.postValue(nameJob.isBlank())
    }

    fun onChangePosition(position: String) {
        this.position = position
        _positionError.postValue(position.isBlank())
    }

    fun onChangeLink(link: String?) {
        this.link = link
    }

    fun setJobDateStartTime(dateTime: String) {
        _jobDateStartTime.postValue(dateTime)
    }

    fun setJobDateFinishTime(dateTime: String?) {
        _jobDateFinishTime.postValue(dateTime)
    }

    fun changeJobDateFinishCheckboxState(isChecked: Boolean) {
        _jobDateFinishCheckbox.value = isChecked
    }
}
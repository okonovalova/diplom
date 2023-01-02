package com.example.diplom.ui.home.jobs.edit_job

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.diplom.R
import com.example.diplom.databinding.FragmentEditJobBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint

class EditJobFragment() : Fragment() {
    private val viewModel: EditJobViewModel by viewModels()
    private lateinit var binding: FragmentEditJobBinding
    val args: EditJobFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditJobBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.inflateMenu(R.menu.add_post_done)
        args.job.let {
            viewModel.onInitView(it)
            binding.nameJobTextEdit.setText(it.name)
            binding.linkTextEdit.setText(it.link)
            binding.positionTextEdit.setText(it.position)
            binding.dateStartValueTextview.text = convertDateFromStringTime(it.start)
            viewModel.setJobDateStartTime(it.start)
            if (it.finish != null) {
                viewModel.changeJobDateFinishCheckboxState(false)
                binding.dateFinishValueTextview.text = convertDateFromStringTime(it.finish)
                viewModel.setJobDateFinishTime(it.finish)
            } else {
                viewModel.changeJobDateFinishCheckboxState(true)

            }
        }
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.nameJobError.observe(viewLifecycleOwner) {
            with(binding.nameJobTextInput) {
                isErrorEnabled = it
                error = if (it) "Введите название организации" else null
            }
        }
        viewModel.positionError.observe(viewLifecycleOwner) {
            with(binding.positionTextInput) {
                isErrorEnabled = it
                error = if (it) "Введите должность" else null
            }
        }
        viewModel.navigateToMainFragment.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        viewModel.jobDateFinishCheckbox.observe(viewLifecycleOwner) {
            binding.finishDateCheckbox.isChecked = it
            if (it) {
                binding.addDateFinishImageview.visibility = View.GONE
                binding.dateFinishValueTextview.visibility = View.GONE
                viewModel.setJobDateFinishTime(null)

            } else {
                binding.addDateFinishImageview.visibility = View.VISIBLE
                binding.dateFinishValueTextview.visibility = View.VISIBLE
            }
        }
    }

    private fun initListeners() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_add -> viewModel.onDoneButtonClicked(args.job)
            }
            true
        }
        binding.nameJobTextEdit.doOnTextChanged { text, start, before, count ->
            viewModel.onChangeNameJob(text.toString())
        }
        binding.positionTextEdit.doOnTextChanged { text, start, before, count ->
            viewModel.onChangePosition(text.toString())
        }

        binding.linkTextEdit.doOnTextChanged { text, start, before, count ->
            viewModel.onChangeLink(text.toString())
        }

        binding.addDateStartImageview.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)


            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val dayPart = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val monthPart = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
                val dateTimeShow = "$dayPart-${monthPart}-$year"
                val dateTimeData = "$year-${monthPart}-$dayPart 00:00"
                binding.dateStartValueTextview.text = dateTimeShow
                viewModel.setJobDateStartTime(dateTimeData)
            }, year, month, day)
            datePickerDialog.show()
        }

        binding.addDateFinishImageview.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val dayPart = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val monthPart = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
                val dateTimeShow = "$dayPart-${monthPart}-$year"
                val dateTimeData = "$year-${monthPart}-$dayPart 00:00"
                binding.dateFinishValueTextview.text = dateTimeShow
                viewModel.setJobDateFinishTime(dateTimeData)
            }, year, month, day)
            datePickerDialog.show()
        }
        binding.finishDateCheckbox.setOnCheckedChangeListener { compoundButton, isChecked ->
            viewModel.changeJobDateFinishCheckboxState(isChecked)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateFromIsoToDateTime(dateTimeString: String, pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"): String? {
        return try {
            val localDateTime: LocalDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(pattern))
            val currentZoneId: ZoneId = ZoneId.systemDefault()
            val currentZonedDateTime: ZonedDateTime = localDateTime.atZone(currentZoneId)
            val format: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            format.format(currentZonedDateTime)
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateFromStringTime(dateTimeString: String): String {
        return convertDateFromIsoToDateTime(dateTimeString, "yyyy-MM-dd'T'HH:mm:ss'Z'") ?: convertDateFromIsoToDateTime(
            dateTimeString,
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
        ).orEmpty()
    }
}
package com.example.diplom.ui.home.jobs.add_job

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.diplom.R
import com.example.diplom.databinding.FragmentAddJobBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class AddJobFragment : Fragment(R.layout.fragment_add_job) {
    private val viewModel: AddJobViewModel by viewModels()
    private val binding by viewBinding(FragmentAddJobBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onInitView()
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.inflateMenu(R.menu.add_post_done)

        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.nameJobError.observe(viewLifecycleOwner) {
            with(binding.nameJobTextInput) {
                isErrorEnabled = it
                error = if (it) context.getString(R.string.error_job_name) else null
            }
        }
        viewModel.positionError.observe(viewLifecycleOwner) {
            with(binding.positionTextInput) {
                isErrorEnabled = it
                error = if (it) context.getString(R.string.error_job_position) else null
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
            } else {
                binding.addDateFinishImageview.visibility = View.VISIBLE
                binding.dateFinishValueTextview.visibility = View.VISIBLE
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressLayout.root.isVisible = isLoading
        }
    }

    private fun initListeners() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_add -> viewModel.onDoneButtonClicked()
            }
            true
        }
        binding.nameJobTextEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.onChangeNameJob(text.toString())
        }
        binding.positionTextEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.onChangePosition(text.toString())
        }

        binding.linkTextEdit.doOnTextChanged { text, _, _, _ ->
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

        binding.finishDateCheckbox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeJobDateFinishCheckboxState(isChecked)
        }
    }
}
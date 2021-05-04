package com.sid1723431.happytimes.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.sid1723431.happytimes.R
import com.sid1723431.happytimes.databinding.FragmentAddEditBinding
import com.sid1723431.happytimes.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_add_edit.*
import kotlinx.android.synthetic.main.item_row.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit) {
    private val viewModel: AddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val binding = FragmentAddEditBinding.bind(view)

        binding.apply {
            editTextHabitName.setText(viewModel.habitName)
            checkBoxImportant.isChecked = viewModel.habitImportance
            checkBoxImportant.jumpDrawablesToCurrentState()
            textViewDateCreated.isVisible = viewModel.habit != null
            textViewDateCreated.text = "Created: ${viewModel.habit?.createdDateFormatted}"
            editTextHabitNotes.setText(viewModel.habitNotes)
            textViewStartDate.text = viewModel.habitStartDay
            textViewEndDate.text = viewModel.habitEndDay


            editTextHabitName.addTextChangedListener {
                viewModel.habitName = it.toString()
            }

            editTextHabitNotes.addTextChangedListener {
                viewModel.habitNotes = it.toString()
            }

            textViewStartDate.addTextChangedListener {
                viewModel.habitStartDay = it.toString()
            }

            textViewEndDate.addTextChangedListener {
                viewModel.habitEndDay = it.toString()
            }


            checkBoxImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.habitImportance = isChecked
            }

            fabSaveTask.setOnClickListener {
                viewModel.onSaveClick()
            }


        }

        button_start_date.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select start date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.addOnPositiveButtonClickListener() {
                text_view_start_date.text = datePicker.headerText
                Log.d("dateTest", "POSITIVE $text_view_start_date")

                Log.d("dateTest", "Date String = ${datePicker.headerText}:: Date epoch value = $it")

                var startDate = datePicker.headerText
                startDate = viewModel.habitStartDay
                Log.d("dateTest", startDate)

            }

            datePicker.show(childFragmentManager, "TAG")

            datePicker.addOnNegativeButtonClickListener {
                Log.d("startDatePicker", "NEGATIVE")
            }
            datePicker.addOnCancelListener {
                Log.d("startDatePicker", "CANCEL")
            }
            datePicker.addOnDismissListener {
                Log.d("startDatePicker", "DISMISS")
            }

        }

        button_end_date.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select end date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.addOnPositiveButtonClickListener() {
                text_view_end_date.text = datePicker.headerText

                Log.d("dateTest", "Date String = ${datePicker.headerText}:: Date epoch value = $it")

                var endDate = datePicker.headerText
                Log.d("dateTesting", text_view_end_date.toString())
                endDate = viewModel.habitEndDay
                Log.d("dateTest", endDate)
            }

            datePicker.show(childFragmentManager, "TAG")

            datePicker.addOnNegativeButtonClickListener {
                Log.d("endDatePicker", "NEGATIVE")
            }
            datePicker.addOnCancelListener {
                Log.d("endDatePicker", "CANCEL")
            }
            datePicker.addOnDismissListener {
                Log.d("endDatePicker", "DISMISS")
            }

        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditEvent.collect { event ->
                when (event) {
                    is AddEditViewModel.AddEditEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditViewModel.AddEditEvent.NavigateBackWithResult -> {
                        binding.editTextHabitName.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }

                }.exhaustive

            }
        }


    }
}



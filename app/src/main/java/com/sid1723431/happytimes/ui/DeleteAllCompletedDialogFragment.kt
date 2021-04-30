package com.sid1723431.happytimes.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedDialogFragment: DialogFragment() {

    private val viewModel : DeleteAllCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Are you sure?")
                    .setMessage("Do you really want to delete all completed tasks?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Yes") { _, _ ->
                        viewModel.onConfirmClick()
                    }
                    .create()
}
package com.example.simpleto_dolist.ui.deleteallcompleted

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.simpleto_dolist.R

import dagger.hilt.android.AndroidEntryPoint
/*Delete Dialog, when deleting all Completed Tasks*/
@AndroidEntryPoint
class DeleteAllCompletedDialogFragment : DialogFragment() {

    private val viewModel: DeleteAllCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.confirm))
            .setMessage(resources.getString(R.string.confirm_msg))
            .setNegativeButton(resources.getString(R.string.confirm_cancel), null)
            .setPositiveButton(resources.getString(R.string.confirm_yes)) { _, _ ->
                viewModel.onConfirmClick()
            }
            .create()
}
package com.example.simpleto_dolist.ui.addedittask

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.simpleto_dolist.R
import com.example.simpleto_dolist.databinding.FragmentAddEditTaskBinding
import com.example.simpleto_dolist.notifications.AlertReceiver
import com.example.simpleto_dolist.utilities.exhaustive
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_edit_task.*
import kotlinx.coroutines.flow.collect
import java.text.DateFormat
import java.util.*

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {
    private val viewModel: AddEditTaskViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)
        var requestCode:Int = 0
        val mAdView = adView2

        if(viewModel.taskNotification == 0){
            viewModel.taskNotification = (0..2147483647).random()
            requestCode = viewModel.taskNotification
        }
        else{
            requestCode = viewModel.taskNotification
            print(requestCode)
        }

        //Initiates the Ad in this view
        MobileAds.initialize(context) {}
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        binding.apply {
            //When the task is completed not allow the user to set an alarm.
            if (viewModel.taskIsCompleted) {
                reminderBtn.setTextColor(Color.GRAY)
                reminderBtn.setOnClickListener {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.disabled_btn_warning),
                        Toast.LENGTH_LONG
                    ).show()
                }
                btnCancel.setTextColor(Color.GRAY)
                btnCancel.setOnClickListener {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.disabled_btn_warning),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            //If the task is not completed, then allow the user to create an alarm
            else {
                reminderBtn.setOnClickListener {

                    if (editTextTaskName.text.isEmpty()) { //Checks if there is a Task in EditText
                        Toast.makeText(
                            context, resources.getString(R.string.reminder_btn_error),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        //Create's a Calender Instance witch is is later used to set the Date for
                        //the Alarm. To set the Date, End-user has to pick the Year,Month and Day
                        //using DatePicker and the time using TimePicker
                        val c: Calendar = Calendar.getInstance()
                        val dateSetListener =
                            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                                c.set(Calendar.YEAR, year)
                                c.set(Calendar.MONTH, month)
                                c.set(Calendar.DAY_OF_MONTH, day)
                            }

                        val timeSetListener =
                            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                                c.set(Calendar.HOUR_OF_DAY, hour)
                                c.set(Calendar.MINUTE, minute)

                                //If the set time is less then the current time, then it throws a message.
                                if (c.timeInMillis < System.currentTimeMillis()) {
                                    Toast.makeText(
                                        context,
                                        resources.getString(R.string.invalid_time),
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    viewModel.taskReminderTime = c.timeInMillis
                                    viewModel.taskReminder = true
                                    help.text = "${resources.getString(R.string.reminder)}: ${
                                        DateFormat.getDateTimeInstance()
                                            .format(viewModel.taskReminderTime)
                                    }"
                                }
                            }


                        //Creates the TimePicker fo the End-user to interact
                        val timeDialog = TimePickerDialog(
                            context,
                            timeSetListener,
                            c.get(Calendar.HOUR_OF_DAY),
                            c.get(Calendar.MINUTE),
                            true
                        )
                        timeDialog.setTitle(resources.getString(R.string.timePicker_text))
                        timeDialog.show()
                        timeDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setOnClickListener {
                            Toast.makeText(
                                context,
                                resources.getString(R.string.cancel_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            timeDialog.dismiss()

                        }

                        //Creates the DatePicker fo the End-user to interact
                        val dateDialog = DatePickerDialog(
                            requireContext(),
                            dateSetListener,
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH),
                        )
                        dateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                        dateDialog.show()

                        dateDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setOnClickListener {
                            Toast.makeText(
                                context,
                                resources.getString(R.string.cancel_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            dateDialog.dismiss()
                            timeDialog.dismiss()
                        }
                    }
                }

                //Cancel button behaviour
                btnCancel.setOnClickListener {
                    val alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val alarmIntent = Intent(context, AlertReceiver::class.java).let { intent ->
                        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                        PendingIntent.getBroadcast(context, requestCode, intent, 0)
                    }
                    alarmMgr.cancel(alarmIntent)
                    Toast.makeText(
                        context,
                        resources.getString(R.string.cancel_success),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    viewModel.taskReminderTime = 0
                    viewModel.taskReminder = false
                    help.text = "${resources.getString(R.string.reminder)}: "

                }

            }

            //Shows the time for created Alarm and the Task
            textViewDateCreated.isVisible = viewModel.task != null
            textViewDateCreated.text =
                "${resources.getString(R.string.created)} ${viewModel.task?.createdDateFormatted}"
            if (viewModel.task == null || !viewModel.task!!.hasReminder) {
                help.text = "${resources.getString(R.string.reminder)}: "
            } else {
                help.text =
                    "${resources.getString(R.string.reminder)}: ${viewModel.task?.setReminderTimeFormatted}"
            }


            //Checks the value of the EditText
            editTextTaskName.setText(viewModel.taskName)
            editTextTaskName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    if (s.toString().trim().isEmpty()) {
                        println(s.toString())
                        Toast.makeText(
                            context, resources.getString(R.string.on_txt_change_empty),
                            Toast.LENGTH_SHORT
                        ).show()
                        reminderBtn.isEnabled = false
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().trim().isNotEmpty()) {
                        viewModel.taskName = s.toString()
                        reminderBtn.isEnabled = true
                    }

                }

            })

            fabSaveTask.setOnClickListener {
                if(System.currentTimeMillis() > viewModel.taskReminderTime){
                    Toast.makeText(
                        context,
                        resources.getString(R.string.invalid_time),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    val alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    println("lololol")
                    println(requestCode)

                    val alarmIntent =
                        Intent(context, AlertReceiver::class.java).let { intent ->
                            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                            intent.putExtra("task_name", viewModel.taskName)
                            intent.putExtra(
                                "title",
                                resources.getString(R.string.reminder_for)
                            )
                            PendingIntent.getBroadcast(
                                context,
                                requestCode,
                                intent,
                                0
                            )
                        }

                    alarmMgr.setAlarmClock(
                        AlarmManager.AlarmClockInfo(
                            viewModel.taskReminderTime,
                            alarmIntent
                        ),
                        alarmIntent
                    )

                    viewModel.onSaveClick()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
                when (event) {
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.editTextTaskName.clearFocus()
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



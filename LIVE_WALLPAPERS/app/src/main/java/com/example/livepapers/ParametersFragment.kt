package com.example.livepapers

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.livepapers.databinding.DialogBinding
import com.example.livepapers.databinding.GeoTimeDialogBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

const val TIME_PICKER_TAG = "timePickerTag"
const val ALL_PRESETS_KEY = "all_presets_key"

class ParametersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var imageStr: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            imageStr = it.getString(PHOTO)!!

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())
        view.setContent {
            ShowParams(image = imageStr.toUri())
        }
        return view
    }


    @Composable
    private fun ShowParams(image: Uri) {

        val path = imageStr.replace("/", "SLASH").replace(":", "COLON").replace(" ", "SPACE")
        var initTimeList = AlarmList()
        try {
            val inputStream = requireActivity().openFileInput(path)
            val textFile = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
            initTimeList = Gson().fromJson(textFile, AlarmList::class.java)
            initTimeList.list.sortBy { it.time.min }
            initTimeList.list.sortBy { it.time.hour }
        } catch (e: Exception) {

        }
        val timeList = remember { mutableStateOf(initTimeList) }
        val reload = remember { mutableStateOf(false) }

        if (!reload.value) {
            LazyColumn(horizontalAlignment = Alignment.Start) {
                item {
                    GlideImage(
                        url = image,
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                timeList.value.list.sortBy { it.time.min }
                timeList.value.list.sortBy { it.time.hour }
                items(timeList.value.list) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                    ) {
                        //time itself
                        Button(
                            onClick = { /*TODO*/ },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF008FFF)
                            )
                        ) {
                            val cal = Calendar.getInstance()
                            cal.set(Calendar.HOUR_OF_DAY, it.time.hour)
                            cal.set(Calendar.MINUTE, it.time.min)
                            Text(
                                text = SimpleDateFormat("HH:mm").format(cal.time),
                                color = Color.Black
                            )
                        }
                        //settings button
                        Image(painter = painterResource(id = R.drawable.settings),
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                                .padding(start = 10.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFB1B0B0))
                                .clickable {
                                    val builder = AlertDialog.Builder(requireContext())
                                    builder.setTitle(getString(R.string.settings))
                                    val dialogLayout = layoutInflater.inflate(R.layout.dialog, null)
                                    builder.setView(dialogLayout)
                                    builder.setPositiveButton("OK") { dialogInterface, i ->
                                        // do something
                                    }
                                    builder.show()

                                })
                        //delete-time button
                        Image(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                                .padding(start = 10.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFB1B0B0))
                                .clickable {
                                    timeList.value.list.remove(it)

                                    deleteTimeFromFile(path, requireContext(), it)
                                    //refresh LazyColumn
                                    reload.value = true
                                },

                            )
                    }
                }

                item {
                    //add-time button
                    Button(
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        onClick = {

                            var chooseCurrentOrGeoButtons = true
                            var hour: Int? = null
                            var min: Int? = null
                            val weekDays = WeekDays()
                            var lockScreen = true
                            var currentDate: Long? = null
                            var repeatAfterCurrentDate: Boolean? = null
                            var rangeDate: MutableList<Long>? = null
                            var choosenGeoTime: Int? = null

                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle(getString(R.string.settings))
                            val binding = DialogBinding.inflate(layoutInflater)
                            builder.setView(binding.root)
                            //geo or current
                            binding.textChooseCurrentTime.setOnClickListener {
                                if (!chooseCurrentOrGeoButtons) {
                                    choosenGeoTime = null
                                    binding.chooseTimeButton.text = getString(R.string.choose_time)
                                    chooseCurrentOrGeoButtons = true
                                    animateFromGrayToBlue(
                                        requireContext(),
                                        binding.textChooseCurrentTime
                                    )
                                    animateFromBlueToGray(
                                        requireContext(),
                                        binding.textChooseGeoTime
                                    )
                                }
                            }
                            binding.textChooseGeoTime.setOnClickListener {
                                if (chooseCurrentOrGeoButtons) {
                                    binding.chooseTimeButton.text = getString(R.string.choose_time)
                                    chooseCurrentOrGeoButtons = false
                                    animateFromGrayToBlue(
                                        requireContext(),
                                        binding.textChooseGeoTime
                                    )
                                    animateFromBlueToGray(
                                        requireContext(),
                                        binding.textChooseCurrentTime
                                    )
                                }
                            }
                            //choose time itself
                            binding.chooseTimeButton.setOnClickListener {
                                if (chooseCurrentOrGeoButtons) {
                                    MaterialTimePicker.Builder()
                                        .setTimeFormat(TimeFormat.CLOCK_24H)
                                        .build()
                                        .apply {
                                            addOnPositiveButtonClickListener {
                                                val calendar = Calendar.getInstance()
                                                calendar.set(Calendar.HOUR_OF_DAY, this.hour)
                                                calendar.set(Calendar.MINUTE, this.minute)
                                                val text = SimpleDateFormat("HH:mm")
                                                val time = text.format(calendar.time)
                                                //val time = Time(this.hour, this.minute)
                                                binding.chooseTimeButton.text = time
                                                Log.d("TIME", "${this.hour}:${this.minute}")
                                                hour = this.hour
                                                min = this.minute
                                            }
                                        }.show(parentFragmentManager, TIME_PICKER_TAG)

                                } else {
                                    val pref = requireActivity().getSharedPreferences(
                                    CASH_LOC,
                                    Context.MODE_PRIVATE
                                )
                                    val resultStr = pref.getString(TIMERESULT, null)
                                    if(resultStr == null){
                                        Toast.makeText(requireContext(),
                                            "NO SAVED LOCATION. RETURN TO MENU",
                                            Toast.LENGTH_LONG).show()
                                    } else{
                                        val result = Gson().fromJson(
                                            resultStr,
                                            TimeResult::class.java
                                        )
                                        val builder2 = AlertDialog.Builder(requireContext())
                                        val bindingDia = GeoTimeDialogBinding.inflate(layoutInflater)
                                        builder2.setView(bindingDia.root)
                                        val buttonsList = listOf(
                                            bindingDia.sr,
                                            bindingDia.sn,
                                            bindingDia.noon,
                                            bindingDia.tb,
                                            bindingDia.te
                                        )
                                        if (choosenGeoTime != null) {
                                            buttonsList[choosenGeoTime!!].setBackgroundColor(
                                                resources.getColor(
                                                    R.color.light_blue
                                                )
                                            )
                                        }
                                        buttonsList.forEachIndexed { index, button ->
                                            button.setOnClickListener {
                                                animateFromGrayToBlue(requireContext(), it)
                                                if (choosenGeoTime != null) {
                                                    animateFromBlueToGray(
                                                        requireContext(),
                                                        buttonsList[choosenGeoTime!!]
                                                    )
                                                }
                                                choosenGeoTime = index
                                            }
                                        }

                                        builder2.setPositiveButton("OK") { dialogInterface, i ->


                                            val a = when (choosenGeoTime) {
                                                0 -> result.results.sunrise
                                                1 -> result.results.sunset
                                                2 -> result.results.solar_noon
                                                3 -> result.results.civil_twilight_begin
                                                4 -> result.results.civil_twilight_end
                                                else -> ""
                                            }
                                            val sdf = SimpleDateFormat("HH:mm")
                                            val date = sdf.parse(a)
                                            val calendar = Calendar.getInstance()
                                            calendar.time = date
                                            hour = calendar.get(Calendar.HOUR_OF_DAY)
                                            min = calendar.get(Calendar.MINUTE)
                                            binding.chooseTimeButton.text = a
                                        }
                                        builder2.show()
                                    }

                                }
                            }

                            //checkBoxes
                            with(binding) {
                                checkBox1.setOnClickListener {
                                    weekDays.weekList[0] = !weekDays.weekList[0]
                                    Log.d("mon", weekDays.weekList[0].toString())
                                }
                                checkBox2.setOnClickListener {
                                    weekDays.weekList[1] = !weekDays.weekList[1]
                                }
                                checkBox3.setOnClickListener {
                                    weekDays.weekList[2] = !weekDays.weekList[2]
                                }
                                checkBox4.setOnClickListener {
                                    weekDays.weekList[3] = !weekDays.weekList[3]
                                }
                                checkBox5.setOnClickListener {
                                    weekDays.weekList[4] = !weekDays.weekList[4]
                                }
                                checkBox6.setOnClickListener {
                                    weekDays.weekList[5] = !weekDays.weekList[5]
                                }
                                checkBox7.setOnClickListener {
                                    weekDays.weekList[6] = !weekDays.weekList[6]
                                }
                            }

                            //current date
                            binding.buttonChooseDate.setOnClickListener {
                                with(binding) {
                                    buttonChooseDate.text = "Choose date(s)"
                                    if (buttonSingleDate.visibility == View.GONE) {
                                        buttonChooseDate.backgroundTintList =
                                            ColorStateList.valueOf(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.light_blue
                                                )
                                            )
                                        buttonSingleDate.visibility = View.VISIBLE
                                        buttonRangeDate.visibility = View.VISIBLE


                                        buttonSingleDate.setOnClickListener {
                                            switch1.visibility = View.VISIBLE
                                            val datePicker = MaterialDatePicker.Builder.datePicker()
                                                .build()
                                            datePicker.addOnPositiveButtonClickListener { timeInMillis ->
                                                currentDate = timeInMillis
                                                buttonChooseDate.text =
                                                    SimpleDateFormat("yyyy-MM-dd").format(
                                                        timeInMillis
                                                    )
                                            }
                                            datePicker.show(parentFragmentManager, "CurrentDate")
                                            switch1.visibility = View.VISIBLE
                                            repeatAfterCurrentDate = switch1.isChecked
                                            switch1.setOnClickListener {
                                                repeatAfterCurrentDate = switch1.isChecked
                                            }
                                        }

                                        binding.buttonRangeDate.setOnClickListener {
                                            switch1.visibility = View.GONE
                                            repeatAfterCurrentDate = null
                                            val datePicker =
                                                MaterialDatePicker.Builder.dateRangePicker()
                                                    .build()
                                            datePicker.addOnPositiveButtonClickListener {
                                                rangeDate = mutableListOf(it.first, it.second)
                                                binding.buttonChooseDate.text =
                                                    "${SimpleDateFormat("yyyy MM dd").format(it.first)} - ${
                                                        SimpleDateFormat(
                                                            "yyyy MM dd"
                                                        ).format(it.second)
                                                    }"
                                            }
                                            datePicker.show(parentFragmentManager, "RangeDate")
                                        }

                                    } else {
                                        buttonChooseDate.text = "Choose date(s)"
                                        buttonChooseDate.backgroundTintList =
                                            ColorStateList.valueOf(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.light_gray
                                                )
                                            )
                                        buttonSingleDate.visibility = View.GONE
                                        buttonRangeDate.visibility = View.GONE
                                        switch1.visibility = View.GONE
                                        currentDate = null
                                        rangeDate = null
                                        repeatAfterCurrentDate = null
                                    }
                                }
                            }

                            //lock or phone screen
                            binding.lockScreenButton.setOnClickListener {
                                if (!lockScreen) {
                                    lockScreen = true
                                    val colorFrom = resources.getColor(R.color.light_gray)
                                    val colorTo = resources.getColor(R.color.light_blue)
                                    val colorAnimation =
                                        ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
                                    colorAnimation.duration = 250 // milliseconds
                                    colorAnimation.addUpdateListener { animator ->
                                        binding.lockScreenButton.setBackgroundColor(
                                            animator.animatedValue as Int
                                        )
                                    }
                                    colorAnimation.start()

                                    val colorAnimation2 =
                                        ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
                                    colorAnimation2.duration = 250 // milliseconds
                                    colorAnimation2.addUpdateListener { animator ->
                                        binding.phoneScreenButton.setBackgroundColor(
                                            animator.animatedValue as Int
                                        )
                                    }
                                    colorAnimation2.start()
                                }
                            }
                            binding.phoneScreenButton.setOnClickListener {
                                if (lockScreen) {
                                    lockScreen = false
                                    val colorFrom = resources.getColor(R.color.light_gray)
                                    val colorTo = resources.getColor(R.color.light_blue)
                                    val colorAnimation =
                                        ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
                                    colorAnimation.duration = 250 // milliseconds
                                    colorAnimation.addUpdateListener { animator ->
                                        binding.phoneScreenButton.setBackgroundColor(
                                            animator.animatedValue as Int
                                        )
                                    }
                                    colorAnimation.start()

                                    val colorAnimation2 =
                                        ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
                                    colorAnimation2.duration = 250 // milliseconds
                                    colorAnimation2.addUpdateListener { animator ->
                                        binding.lockScreenButton.setBackgroundColor(
                                            animator.animatedValue as Int
                                        )
                                    }
                                    colorAnimation2.start()
                                }
                            }

                            builder.setPositiveButton("OK") { dialogInterface, i ->
                                // do something
                                if (hour != null && min != null && weekDays.weekList.contains(true)) {
                                    val alarm = Alarm(
                                        Time(hour!!, min!!),
                                        weekDays,
                                        currentDate,
                                        repeatAfterCurrentDate,
                                        rangeDate,
                                        lockScreen
                                    )
                                    val a = createAlarmManager(requireContext(), alarm, imageStr)
                                    if (a) {
                                        timeList.value.list.add(alarm)
                                        timeList.value.list.sortBy { it.time.hour }
                                        timeList.value.list.sortBy { it.time.min }
                                    }
                                    reload.value = a

                                } else Toast.makeText(
                                    requireContext(),
                                    "Incorrect parameters",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            builder.setNegativeButton("Cancel") { dialogInterface, i ->

                            }
                            builder.show()

                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFB1B0B0)
                        )
                    ) {
                        Text(text = "Add time of photo setting")
                    }
                }
            }
        } else {
            reload.value = false
        }
    }

}

fun addAlarmToFile(alarm: Alarm, imageStr: String, context: Context) {
    val path = imageStr.replace("/", "SLASH").replace(":", "COLON").replace(" ", "SPACE")
    try {
        val inputStream = context.openFileInput(path)
        var textFile = inputStream.bufferedReader().use { it.readText() }
        inputStream.close()

        val alarmList = Gson().fromJson(textFile, AlarmList::class.java)
        alarmList.list.add(alarm)
        alarmList.list.sortBy { it.time.min }
        alarmList.list.sortBy { it.time.hour }


        textFile = Gson().toJson(alarmList)
        val outputStream = context.openFileOutput(
            path,
            AppCompatActivity.MODE_PRIVATE
        )
        outputStream.write(textFile.toByteArray())
        outputStream.close()

    } catch (e: Exception) {
        val outputStream = context.openFileOutput(
            path,
            AppCompatActivity.MODE_PRIVATE
        )
        outputStream.write(
            Gson().toJson(AlarmList(mutableListOf(alarm)))
                .toByteArray()
        )
        outputStream.close()
    }
    addToAllPresets(context, path, alarm)
}

fun deleteTimeFromFile(path: String, context: Context, item: Alarm) {

    //get TimeList
    val inputStream = context.openFileInput(path)
    var textFile =
        inputStream
            .bufferedReader()
            .use { it.readText() }
    inputStream.close()
    //remove "it" from TimeList
    val gson = Gson()
    val file = gson.fromJson(textFile, AlarmList::class.java)
    file.list.remove(item)
    textFile = gson.toJson(file)
    //save new TimeList into file
    val outputStream = context.openFileOutput(
        path,
        AppCompatActivity.MODE_PRIVATE
    )
    outputStream.write(textFile.toByteArray())
    outputStream.close()
    //cancel AlarmManager
    cancelAlarmManager(
        context = context,
        hour = item.time.hour,
        min = item.time.min
    )
    Log.d("FILE", "DELETED")
    deleteFromAllPresets(context, path, item)
}

fun createAlarmManager(context: Context, alarm: Alarm, imageStr: String): Boolean {
    val code = "${alarm.time.hour * 60}${alarm.time.min}".toInt()
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val calendar = Calendar.getInstance()
    var calendarRange: Calendar? = null
    val intent = Intent(context, MyBroadcastReciever::class.java)
    intent.putExtra(IMAGE_KEY, imageStr)
    intent.putExtra(IS_LOCK_SCREEN, alarm.isLockScreen)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        code,
        intent,
        PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
    )
    calendar.apply {
        if (alarm.currentDate != null) {
            timeInMillis = alarm.currentDate!!
        }
        if (alarm.rangeDate != null) {
            timeInMillis = alarm.rangeDate!![0]
            calendarRange = Calendar.getInstance()
            calendarRange!!.timeInMillis = alarm.rangeDate!![1]
            calendarRange!!.set(Calendar.HOUR_OF_DAY, 23)
            calendarRange!!.set(Calendar.MINUTE, 59)
        }

        set(Calendar.HOUR_OF_DAY, alarm.time.hour)
        set(Calendar.MINUTE, alarm.time.min)

        if (((alarm.currentDate != null && alarm.repeatAfterCurrentDate != true) || alarm.rangeDate != null) && calendar.timeInMillis < System.currentTimeMillis()) {
            Toast.makeText(
                context,
                "Wrong time. You must not choose the past time",
                Toast.LENGTH_LONG
            ).show()
            return false
        } else if (alarm.currentDate == null && alarm.rangeDate == null && calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        //creating alarms

        //for current date
        if (alarm.currentDate != null) {
            Log.d("CURRENT DATE", "STARTED")
            if (alarm.repeatAfterCurrentDate == true) {
                Log.d("CURRENT DATE", "repeating")

                if (!alarm.weekDays.weekList.contains(false)) {
                    Log.d("CURRENT DATE", "all weekdays")
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                } else {
                    alarm.weekDays.weekList.forEach {
                        if (it) {
                            Log.d("CURRENT DATE", "weekday: X")
                            alarmManager.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                AlarmManager.INTERVAL_DAY * 7,
                                pendingIntent
                            )
                        }
                    }
                }
            } else {
                Log.d("CURRENT DATE", "single date")
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }

        //without currentDate(usual)
        if (alarm.currentDate == null && alarm.rangeDate == null) {
            Log.d("USUAL ALARM", "STARTED")

            if (!alarm.weekDays.weekList.contains(false)) {
                Log.d("USUAL ALARM", "everyday alarm")

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else {
                alarm.weekDays.weekList.forEach {
                    if (it) {
                        Log.d("USUAL ALARM", "alarm X")

                        alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            AlarmManager.INTERVAL_DAY * 7,
                            pendingIntent
                        )
                    }
                }
            }
        }
        //with dateRange
        if (alarm.rangeDate != null) {
            Log.d("RANGE ALARM", "STARTED")

            if (!alarm.weekDays.weekList.contains(false)) {
                Log.d("RANGE ALARM", "everyday alarm")

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else {
                alarm.weekDays.weekList.forEach {
                    if (it) {
                        Log.d("RANGE ALARM", "alarm X")

                        alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            AlarmManager.INTERVAL_DAY * 7,
                            pendingIntent
                        )
                    }
                }
            }

            Log.d("RANGE ALARM", "creating cancel intent")

            val cancelIntent = Intent(context, CancellingBroadcastReceiver::class.java)
            cancelIntent.putExtra(RANGE_CODE, code)
            val cancellingPendingIntent = PendingIntent.getBroadcast(
                context,
                code,
                cancelIntent,
                PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC,
                calendarRange!!.timeInMillis,
                cancellingPendingIntent
            )
        }

    }
    addAlarmToFile(alarm, imageStr, context)
    Log.d("ALARM FINALLY", "added")

    return true
}

fun cancelAlarmManager(context: Context, hour: Int, min: Int) {
    val code = "${hour * 60}${min}".toInt()
    Log.d("CODE cancel", "$hour, $min ${("${hour * 60}$min".toInt())}")

    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent =
        Intent(context, MyBroadcastReciever::class.java)
    val pendingIntent =
        PendingIntent.getBroadcast(
            context,
            code,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
    alarmManager.cancel(pendingIntent)
}

fun addToAllPresets(context: Context, path: String, alarm: Alarm) {
    try {
        val inputStream = context.openFileInput(ALL_PRESETS_KEY)
        var textFile = inputStream.bufferedReader().use { it.readText() }
        inputStream.close()

        val presets = Gson().fromJson(textFile, PresetList::class.java)

        val a = presets.presetMappedList[path]
        if (a != null) {
            a.list.add(alarm)
            a.list.sortBy { it.time.hour }
            a.list.sortBy { it.time.min }
            presets.presetMappedList[path] = a
        } else {
            presets.presetMappedList[path] = AlarmList(mutableListOf(alarm))
        }

        textFile = Gson().toJson(presets)
        val outputStream = context.openFileOutput(
            ALL_PRESETS_KEY,
            AppCompatActivity.MODE_PRIVATE
        )
        outputStream.write(textFile.toByteArray())
        outputStream.close()

    } catch (e: Exception) {
        Log.d("All Presets Exception:", e.toString())
        val outputStream = context.openFileOutput(
            ALL_PRESETS_KEY,
            AppCompatActivity.MODE_PRIVATE
        )
        outputStream.write(
            Gson().toJson(
                PresetList(
                    presetMappedList = mutableMapOf(
                        Pair(
                            path,
                            AlarmList(mutableListOf(alarm))
                        )
                    )
                )
            )
                .toByteArray()
        )
        outputStream.close()
        Log.d("PresetList: ", "created")
    }
}

fun deleteFromAllPresets(context: Context, path: String, alarm: Alarm) {
    val inputStream = context.openFileInput(ALL_PRESETS_KEY)
    val textFile = inputStream.bufferedReader().use { it.readText() }
    inputStream.close()

    val presets = Gson().fromJson(textFile, PresetList::class.java)

    val a = presets.presetMappedList[path]!!
    a.list.remove(alarm)
    presets.presetMappedList[path] = a
    if (a.list.isEmpty()) presets.presetMappedList.remove(path)

    val outputStream = context.openFileOutput(
        ALL_PRESETS_KEY,
        AppCompatActivity.MODE_PRIVATE
    )
    outputStream.write(
        Gson().toJson(presets)
            .toByteArray()
    )
    outputStream.close()
    Log.d("PresetList: ", presets.toString())
}

data class Time(
    val hour: Int,
    val min: Int
)

data class WeekDays(
    val weekList: MutableList<Boolean> = mutableListOf(true, true, true, true, true, true, true)
)

data class Alarm(
    var time: Time,
    var weekDays: WeekDays = WeekDays(),
    var currentDate: Long? = null,
    var repeatAfterCurrentDate: Boolean? = null,
    var rangeDate: MutableList<Long>? = null,
    var isLockScreen: Boolean = true
)

data class AlarmList(
    var list: MutableList<Alarm> = mutableListOf()
)

data class PresetList(
    val presetMappedList: MutableMap<String, AlarmList>
)

@Composable
fun GlideImage(
    url: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    loadingDrawable: Int = R.drawable.hourglass_top,
) {
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    Glide.with(context)
        .asBitmap()
        .load(url)
        .apply(RequestOptions().override(Target.SIZE_ORIGINAL))
        .listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean,
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                bitmap.value = resource
                return false
            }
        })
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?,
            ) {
                bitmap.value = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })


    bitmap.value?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
        )
    } ?: Image(
        painterResource(id = loadingDrawable),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}

fun animateFromGrayToBlue(context: Context, button: View) {
    val colorFrom = context.resources.getColor(R.color.light_gray)
    val colorTo = context.resources.getColor(R.color.light_blue)
    val colorAnimation =
        ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
    colorAnimation.duration = 250 // milliseconds
    colorAnimation.addUpdateListener { animator ->
        button.setBackgroundColor(
            animator.animatedValue as Int
        )
    }
    colorAnimation.start()
}

fun animateFromBlueToGray(context: Context, button: View) {
    val colorFrom = context.resources.getColor(R.color.light_gray)
    val colorTo = context.resources.getColor(R.color.light_blue)
    val colorAnimation =
        ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
    colorAnimation.duration = 250 // milliseconds
    colorAnimation.addUpdateListener { animator ->
        button.setBackgroundColor(
            animator.animatedValue as Int
        )
    }
    colorAnimation.start()
}



package com.example.livepapers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val RANGE_CODE = "range_code"
class CancellingBroadcastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        CoroutineScope(Dispatchers.Default).launch {
            val code = intent?.extras?.getInt(RANGE_CODE)!!
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent2 = Intent(context, MyBroadcastReciever::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    code,
                    intent2,
                    PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
                )
            alarmManager.cancel(pendingIntent)
        }

    }

}
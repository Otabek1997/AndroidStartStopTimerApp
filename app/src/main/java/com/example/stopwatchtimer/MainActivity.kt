package com.example.stopwatchtimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.stopwatchtimer.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var timerStarted = false
    private lateinit var serviceInent: Intent
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener{StartTimer()}
        binding.resetButton.setOnClickListener{resetTimer()}

        serviceInent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
    }
    private fun resetTimer(){
        stopTimer()
        time = 0.0
        binding.timeTv.text = getTimerStringDouble(time)
    }

    private fun StartTimer(){
        if (timerStarted) {
            stopTimer()
        }else
            startTimer()
    }


    private fun startTimer() {
        serviceInent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceInent)
        binding.startButton.text = "STOP"
        binding.startButton.icon = getDrawable(R.drawable.ic_baseline_pause_circle_outline_24)
        timerStarted = true
    }

    private fun stopTimer() {
        stopService(serviceInent)
        binding.startButton.text = "START"
        binding.startButton.icon = getDrawable(R.drawable.ic_baseline_play_circle_outline_24)
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            binding.timeTv.text = getTimerStringDouble(time)

        }
    }

    private fun getTimerStringDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hour: Int, min: Int, sec: Int): String =
        String.format("%02d:%02d:%02d", hour, min, sec)

}


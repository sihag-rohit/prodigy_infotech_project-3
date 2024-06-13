package com.example.stopwatch

import android.app.Dialog
import android.os.Bundle
import android.os.SystemClock
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Chronometer
import android.widget.ListView
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.example.stopwatch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var minutes:String?="00.00.00"
    private lateinit var listView: ListView
    private var isRunning = false
    private var lapTimes: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Find views by their IDs
        listView = findViewById(R.id.listview)

        // Set up ListView with an ArrayAdapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lapTimes)
        listView.adapter = adapter

        binding.clock.setOnClickListener {
            var dialog= Dialog(this)
            dialog.setContentView(R.layout.dialog)
            var numberPicker=dialog.findViewById<NumberPicker>(R.id.numberPicker)
            numberPicker.minValue=0
            numberPicker.maxValue=5
            dialog.findViewById<Button>(R.id.set_time).setOnClickListener {
                minutes=numberPicker.value.toString()
                binding.clocktime.text=dialog.findViewById<NumberPicker>(R.id.numberPicker).value.toString()+"mins"
                dialog.dismiss()
            }
            dialog.show()
        }

        // Start/Stop button logic
        binding.start.setOnClickListener {
            if (!isRunning) {
                isRunning = false
                if (!minutes.equals("00.00.00")) {
                    var totalmin = minutes!!.toInt() * 60 * 1000L
                    var countdown = 1000L
                    binding.chronometer.base = SystemClock.elapsedRealtime() + totalmin
                    binding.chronometer.format = "%S %S"
                    binding.chronometer.onChronometerTickListener =Chronometer.OnChronometerTickListener {
                            var elapsedtime =
                                SystemClock.elapsedRealtime() - binding.chronometer.base
                            if (elapsedtime >= totalmin) {
                                binding.chronometer.stop()
                                isRunning = false
                                binding.start.text = "Start"
                            }
                        }
                    binding.chronometer.start()
                } else {
                    binding.chronometer.base = SystemClock.elapsedRealtime()
                    binding.chronometer.start()
                    binding.start.text = "Stop"
                    isRunning = true
                }
            }else{
                    binding.chronometer.stop()
                    isRunning=false
                    binding.start.text="Start"
                }
        }

        // Lap button logic
        binding.lap.setOnClickListener {
            if (isRunning) {
                val elapsedMillis = SystemClock.elapsedRealtime() - binding.chronometer.base
                val minutes = (elapsedMillis / 1000) / 60
                val seconds = (elapsedMillis / 1000) % 60
                val milliseconds = (elapsedMillis % 1000) / 10
                val lapTime = String.format("%02d:%02d:%02d", minutes, seconds, milliseconds)
                lapTimes.add(lapTime)
                adapter.notifyDataSetChanged()
            }
        }
    }
}


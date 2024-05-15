package com.example.stopwatch

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stopwatch.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {


    private lateinit var mainActivityBinding: ActivityMainBinding;

    private lateinit var lapArrayList:ArrayList<LapElements>;

    private var time = 0.0;
    private var running: Boolean = false;
    private lateinit var serviceIntent: Intent;

    private var previousLap: String = "00:00:00.00";
    private var lapCount:Int = 0;
    private lateinit var LapIdArray: Array<Int>;
    private lateinit var LapTimeArray: Array<String>;
    private lateinit var LapDiffArray: Array<String>;


    @SuppressLint("InlinedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        LapIdArray = arrayOf();
        LapDiffArray = arrayOf();
        LapTimeArray = arrayOf();

        lapArrayList = arrayListOf();


        mainActivityBinding.startStop.setOnClickListener {
            startStopTimer();
        }
        mainActivityBinding.resetButton.setOnClickListener {
            resetTimer();
        }
        mainActivityBinding.lapButton.setOnClickListener {
            lapTime();
        }

        mainActivityBinding.copyButton.setOnClickListener {

            if(running){
                Toast.makeText(applicationContext,"Please stop the stopwatch",Toast.LENGTH_SHORT).show();
            }
            else{
                var resultString:String = "Time : "+mainActivityBinding.time.text;

                if(lapArrayList.size != 0){
                    resultString += "\nLap Time :";
                    for(i in lapArrayList.indices)
                        resultString += "\n#Lap "+lapArrayList[i].lapId.toString() + " "+lapArrayList[i].lapTime+" "+lapArrayList[i].lapDiff;
                }

                val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData:ClipData = ClipData.newPlainText("simple text",resultString)
                clipBoard.setPrimaryClip(clipData)
            }
        }

        mainActivityBinding.lap.layoutManager = LinearLayoutManager(this)
        mainActivityBinding.lap.setHasFixedSize(true);

        serviceIntent = Intent(applicationContext, TimerService::class.java);
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED), RECEIVER_EXPORTED);
    }

    private fun lapTime() {

        if(!running) {
            Toast.makeText(applicationContext,"Please run the stopwatch",Toast.LENGTH_SHORT).show();
            return;
        }

        if(lapArrayList.size >= 15){
            Toast.makeText(applicationContext,"Maximum lap limit attained",Toast.LENGTH_SHORT).show();
            return;
        }
        val lapId:Int = ++(lapCount);
        val lapTime:String = timeToString(time);
        val lapDiff:String = "+"+timeDiff(previousLap,lapTime)

        lapArrayList.add(0,LapElements(lapId,lapTime,lapDiff));
        mainActivityBinding.lap.adapter = LapAdapter(lapArrayList);
        previousLap = lapTime;
    }
    private fun timeDiff(time1:String,time2:String):String{

        var hours = time2.substring(0,2).toInt() - time1.substring(0,2).toInt()
        var minutes = time2.substring(3,5).toInt()  - time1.substring(3,5).toInt()
        var seconds = time2.substring(6,8).toInt() - time1.substring(6,8).toInt()
        var milliSeconds = time2.substring(9,time2.length).toInt() - time1.substring(9,time1.length).toInt()

        if(milliSeconds < 0){
            seconds --;
            milliSeconds += 100;
        }
        if(seconds < 0){
            minutes --;
            seconds += 60;
        }
        if(minutes < 0){
            hours--;
            minutes += 60;
        }
        if(hours < 0)
            hours += 24;


        return String.format("%02d:%02d:%02d.%02d",hours,minutes,seconds,milliSeconds);
    }

    private val updateTime : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            Toast.makeText(applicationContext,"Broadcast recieved",Toast.LENGTH_SHORT).show()
            time = intent.getDoubleExtra(TimerService.TIMER_EXTRA, 0.0);
            mainActivityBinding.time.text = timeToString(time);
            mainActivityBinding.progressBar.progress = (mainActivityBinding.progressBar.progress + 1) % 6000;
        }

    }

    private fun timeToString(data: Double): String {

        val time = data.roundToInt();
        val hours = time / (3600 * 100 ) % 24;
        val minutes = (time / (60 * 100)) % 60;
        val seconds = (time / 100 ) % 60;
        val milliSeconds = (time)%100;
        return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds ,milliSeconds);
    }

    private fun resetTimer() {
        stopTimer();
        time = 0.0;
        mainActivityBinding.progressBar.progress = 0;
        lapArrayList = arrayListOf();
        lapCount = 0;
        previousLap = "00:00:00.00";


        mainActivityBinding.time.text = previousLap;
        mainActivityBinding.lap.adapter = LapAdapter(lapArrayList);
    }

    private fun startStopTimer() {
        if (running)
            stopTimer()
        else
            startTimer();
    }

    @SuppressLint("SetTextI18n")
    private fun startTimer() {

        serviceIntent.putExtra(TimerService.TIMER_EXTRA, time);
        startService(serviceIntent);
        mainActivityBinding.startStop.text = "Stop";
        running = true;
    }

    @SuppressLint("SetTextI18n")
    private fun stopTimer() {
        stopService(serviceIntent)
        running = false;
        mainActivityBinding.startStop.text = "Start";
    }


}



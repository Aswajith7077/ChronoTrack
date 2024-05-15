package com.example.stopwatch

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import java.util.Timer
import java.util.TimerTask


class TimerService: Service() {
    override fun onBind(intent: Intent?): IBinder? = null;

    private var timer = Timer();

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val data = intent.getDoubleExtra(TIMER_EXTRA,0.0);
//        Toast.makeText(applicationContext,"Time recieved was $data",Toast.LENGTH_LONG).show()
        timer.scheduleAtFixedRate(TimeTask(data),0,10)
        return START_NOT_STICKY;

    }

    override fun onDestroy() {
        timer.cancel();
        super.onDestroy()
    }

    private inner class TimeTask(private var time:Double): TimerTask(){
        override fun run(){
            val intent = Intent(TIMER_UPDATED);
            time ++;
            intent.putExtra(TIMER_EXTRA,time)
            sendBroadcast(intent);
        }
    }



    companion object{
        const val TIMER_UPDATED = "timeUpdated"
        const val TIMER_EXTRA = "timeExtra";
    }




}


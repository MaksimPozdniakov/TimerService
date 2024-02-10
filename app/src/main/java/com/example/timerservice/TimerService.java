package com.example.timerservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class TimerService extends Service {

    public static final String ACTION_START = "PozMaxPav.com.all_activities.helperClasses.START";
    public static final String ACTION_PAUSE = "PozMaxPav.com.all_activities.helperClasses.PAUSE";
    public static final String ACTION_RESUME = "PozMaxPav.com.all_activities.helperClasses.RESUME";

    private Handler handler;
    private Boolean isRunning = false;
    private Boolean isPaused = false;
    private long startTime = 0;
    private long elapsedMillis = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_START:
                        if (!isRunning) {
                            startTime = SystemClock.elapsedRealtime() - elapsedMillis;
                            isRunning = true;
                            isPaused = false;
                            handler = new Handler();
                            startTimer();
                        }
                        break;
                    case ACTION_PAUSE:
                        if (isRunning && !isPaused) {
                            handler.removeCallbacksAndMessages(null);
                            elapsedMillis = SystemClock.elapsedRealtime() - startTime;
                            isPaused = true;
                        }
                        break;
                    case ACTION_RESUME:
                        if (isRunning && isPaused) {
                            startTime = SystemClock.elapsedRealtime() - elapsedMillis;
                            isPaused = false;
                            startTimer();
                        }
                        break;
                }
            }
        }
        // Возвращаем START_STICKY, чтобы сервис автоматически
        // перезапускался, если его убьет система.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        stopTimer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isRunning && !isPaused) {
                    elapsedMillis = SystemClock.elapsedRealtime() - startTime;
                    Intent intent = new Intent("UPDATE_TIME");
                    intent.putExtra("elapsedMillis", elapsedMillis);
                    sendBroadcast(intent);
                    LocalBroadcastManager.getInstance(TimerService.this).sendBroadcast(intent);
                    handler.postDelayed(this, 1000); // Обновляем каждую секунду
                }
            }
        });
    }

    private void stopTimer() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}



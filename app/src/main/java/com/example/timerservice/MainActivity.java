package com.example.timerservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button fellAsleep,wokeUp,pause,cont;
    private TextView timer;
    private LocalBroadcastManager localBroadcastManager;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long elapsedMillis = intent.getLongExtra("elapsedMillis", 0);
            updateTimer(elapsedMillis);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Поле для вывода таймера
        timer = findViewById(R.id.timer);

        // Инициализируем LocalBroadcastManager
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        addListenerOnButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        localBroadcastManager.registerReceiver(broadcastReceiver,
                new IntentFilter("UPDATE_TIME"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private void addListenerOnButton() {

        fellAsleep = findViewById(R.id.fellAsleep);
        wokeUp = findViewById(R.id.wokeUp);
        pause = findViewById(R.id.pause);
        cont = findViewById(R.id.cont);

        fellAsleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO Запускаем секундомер (разобраться с проблемой сброса секундомера)
                timer.setVisibility(View.VISIBLE);
                Intent serviceIntent = new Intent(MainActivity.this, TimerService.class);
                serviceIntent.setAction(TimerService.ACTION_START);
                startService(serviceIntent);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Приостанавливаем работу секундомера
                Intent serviceIntent = new Intent(MainActivity.this, TimerService.class);
                serviceIntent.setAction(TimerService.ACTION_PAUSE);
                startService(serviceIntent);
            }
        });

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Продолжаем работу секундомера
                Intent serviceIntent = new Intent(MainActivity.this, TimerService.class);
                serviceIntent.setAction(TimerService.ACTION_RESUME);
                startService(serviceIntent);
            }
        });

        wokeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Останавливаем секундомер
                timer.setVisibility(View.GONE);
                Intent serviceIntent = new Intent(MainActivity.this, TimerService.class);
                stopService(serviceIntent);
            }
        });
    }

    // метод для вывода информации секундомера
    private void updateTimer(long elapsedMillis) {
        int seconds = (int) (elapsedMillis / 1000); // Переводим миллисекунды в секунды
        int minutes = seconds / 60; // Получаем количество минут
        int hours = minutes / 60; // Получаем количество часов
        seconds %= 60; // Получаем количество секунд, оставшихся после вычитания минут
        minutes %= 60; // Получаем количество минут, оставшихся после вычитания часов
        String time = String.format(
                Locale.getDefault(), "%02d:%02d:%02d",
                hours, minutes, seconds); // Форматируем время в "чч:мм:сс"
        timer.setText(time); // Устанавливаем отформатированное время в TextView
    }
}
package com.example.numistracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import android.content.Intent;
import android.widget.Button;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main_screen);

        Button button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainScreen.this, CoinsActivity.class);
            startActivity(intent);
        });

        Button buttonBanknotes = findViewById(R.id.button2); // сменете с реалното ID от activity_main_screen.xml

        buttonBanknotes.setOnClickListener(v -> {
            Intent intent = new Intent(MainScreen.this, banknotes.class);
            startActivity(intent);
        });

        SharedPreferences preferences =
                getSharedPreferences("AppSettings", MODE_PRIVATE);

        SwitchCompat themeSwitch = findViewById(R.id.switch1);

        boolean isDark = preferences.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDark
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        themeSwitch.setChecked(isDark);

        themeSwitch.setOnCheckedChangeListener((buttonView, checked) -> {

            preferences.edit()
                    .putBoolean("dark_mode", checked)
                    .apply();

            AppCompatDelegate.setDefaultNightMode(
                    checked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
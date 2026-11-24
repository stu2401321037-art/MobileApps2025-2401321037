package com.example.numistracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainScreen extends AppCompatActivity {

    private SharedPreferences preferences;

    private void applySavedTheme() {
        boolean isDark =
                preferences.getBoolean("dark_mode", false);

        setThemeMode(isDark);
    }

    private void setThemeMode(boolean isDark) {
        int desiredMode = isDark
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;

        if (AppCompatDelegate.getDefaultNightMode()
                != desiredMode) {
            AppCompatDelegate.setDefaultNightMode(desiredMode);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        preferences =
                getSharedPreferences("AppSettings", MODE_PRIVATE);

        applySavedTheme();

        super.onCreate(savedInstanceState);

        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main_screen);

        Button buttonCoins = findViewById(R.id.button);
        Button buttonBanknotes = findViewById(R.id.button2);
        Button buttonCollection = findViewById(R.id.button3);

        SwitchCompat themeSwitch =
                findViewById(R.id.switch1);

        buttonCoins.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainScreen.this,
                    ItemsListActivity.class
            );
            intent.putExtra(
                    ItemsListActivity.EXTRA_TYPE,
                    ItemsListActivity.TYPE_COINS
            );
            startActivity(intent);
        });

        buttonBanknotes.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainScreen.this,
                    ItemsListActivity.class
            );
            intent.putExtra(
                    ItemsListActivity.EXTRA_TYPE,
                    ItemsListActivity.TYPE_BANKNOTES
            );
            startActivity(intent);
        });
        buttonCollection.setOnClickListener(v -> {
            Toast.makeText(
                    MainScreen.this,
                    "Collection is not available yet",
                    Toast.LENGTH_SHORT
            ).show();
        });

        boolean isDark =
                preferences.getBoolean("dark_mode", false);

        themeSwitch.setChecked(isDark);

        themeSwitch.setOnCheckedChangeListener(
                (buttonView, checked) -> {

                    preferences.edit()
                            .putBoolean("dark_mode", checked)
                            .apply();

                    setThemeMode(checked);
                }
        );

        setupWindowInsets();
    }

    private void setupWindowInsets() {
        View root = findViewById(R.id.main);

        final int left = root.getPaddingLeft();
        final int top = root.getPaddingTop();
        final int right = root.getPaddingRight();
        final int bottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(
                root, (view, windowInsets) -> {

                    Insets systemBars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    view.setPadding(
                            left + systemBars.left,
                            top + systemBars.top,
                            right + systemBars.right,
                            bottom + systemBars.bottom
                    );

                    return windowInsets;
                });
    }
}
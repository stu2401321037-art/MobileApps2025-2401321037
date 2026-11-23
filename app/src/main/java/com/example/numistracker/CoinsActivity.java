package com.example.numistracker;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.numistracker.data.DatabaseHelper;

public class CoinsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    private void applySavedTheme() {
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDark = preferences.getBoolean("dark_mode", false);
        int desiredMode = isDark
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;

        if (AppCompatDelegate.getDefaultNightMode() != desiredMode) {
            AppCompatDelegate.setDefaultNightMode(desiredMode);
        }
    }

    private ArrayAdapter<CharSequence> createConditionAdapter() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.coin_conditions)
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.gold));
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_coins);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        ImageButton button4 = findViewById(R.id.button4);
        button4.setOnClickListener(v -> finish());

        Spinner spinnerCondition = findViewById(R.id.spinnerCondition);
        spinnerCondition.setAdapter(createConditionAdapter());

        EditText editCountry = findViewById(R.id.editCountry);
        EditText editYear = findViewById(R.id.editYear);
        EditText editDenomination = findViewById(R.id.editDenomination);
        EditText editCurrency = findViewById(R.id.editCurrency);
        EditText editQuantity = findViewById(R.id.editQuantity);
        EditText editEstimatedValue = findViewById(R.id.editEstimatedValue);
        Button buttonSaveCoin = findViewById(R.id.buttonSaveCoin);

        buttonSaveCoin.setOnClickListener(v -> {
            String name = "";
            String country = editCountry.getText().toString().trim();
            String yearStr = editYear.getText().toString().trim();
            String denomination = editDenomination.getText().toString().trim();
            String currency = editCurrency.getText().toString().trim();
            String condition = spinnerCondition.getSelectedItem() != null
                    ? spinnerCondition.getSelectedItem().toString()
                    : "";
            String qtyStr = editQuantity.getText().toString().trim();
            String valStr = editEstimatedValue.getText().toString().trim();

            if (country.isEmpty()) {
                editCountry.setError("Enter country");
                editCountry.requestFocus();
                return;
            }

            if (yearStr.isEmpty()) {
                editYear.setError("Enter year");
                editYear.requestFocus();
                return;
            }

            int year;
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                editYear.setError("Invalid year");
                editYear.requestFocus();
                return;
            }
            if (year <= 0) {
                editYear.setError("Year must be greater than 0");
                editYear.requestFocus();
                return;
            }

            if (denomination.isEmpty()) {
                editDenomination.setError("Enter denomination");
                editDenomination.requestFocus();
                return;
            }

            double denomValue;
            try {
                denomValue = Double.parseDouble(denomination);
            } catch (NumberFormatException e) {
                editDenomination.setError("Invalid denomination");
                editDenomination.requestFocus();
                return;
            }
            if (denomValue <= 0) {
                editDenomination.setError("Denomination must be greater than 0");
                editDenomination.requestFocus();
                return;
            }

            if (currency.isEmpty()) {
                editCurrency.setError("Enter currency");
                editCurrency.requestFocus();
                return;
            }

            if (condition.isEmpty()) {
                Toast.makeText(this, "Select condition", Toast.LENGTH_SHORT).show();
                return;
            }

            if (qtyStr.isEmpty()) {
                editQuantity.setError("Enter quantity");
                editQuantity.requestFocus();
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(qtyStr);
            } catch (NumberFormatException e) {
                editQuantity.setError("Invalid quantity");
                editQuantity.requestFocus();
                return;
            }
            if (quantity < 1) {
                editQuantity.setError("Quantity must be at least 1");
                editQuantity.requestFocus();
                return;
            }

            double estimatedValue = 0.0;
            if (!valStr.isEmpty()) {
                try {
                    estimatedValue = Double.parseDouble(valStr);
                } catch (NumberFormatException e) {
                    editEstimatedValue.setError("Invalid estimated value");
                    editEstimatedValue.requestFocus();
                    return;
                }
                if (estimatedValue < 0) {
                    editEstimatedValue.setError("Estimated value cannot be negative");
                    editEstimatedValue.requestFocus();
                    return;
                }
            }

            long rowId = dbHelper.insertCoin(name, country, year, denomination, currency, "", condition, 0.0, estimatedValue, quantity, "", false
            );

            if (rowId != -1) {
                Toast.makeText(this, "Saved coin #" + rowId, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving coin", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (database != null && database.isOpen()) {
            database.close();
        }

        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
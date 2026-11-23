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

public class banknotes extends AppCompatActivity {

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
                getResources().getStringArray(R.array.banknote_conditions)
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
        setContentView(R.layout.activity_banknotes);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        ImageButton button4 = findViewById(R.id.button4);
        if (button4 != null) {
            button4.setOnClickListener(v -> finish());
        }

        Spinner spinnerCondition = findViewById(R.id.spinnerCondition);
        if (spinnerCondition != null) {
            spinnerCondition.setAdapter(createConditionAdapter());
        }

        EditText editCountry = findViewById(R.id.editCountry);
        EditText editYear = findViewById(R.id.editYear);
        EditText editDenomination = findViewById(R.id.editDenomination);
        EditText editCurrency = findViewById(R.id.editCurrency);
        EditText editQuantity = findViewById(R.id.editQuantity);
        EditText editEstimatedValue = findViewById(R.id.editEstimatedValue);
        Button buttonSaveBanknote = findViewById(R.id.buttonSaveBanknote);

        if (buttonSaveBanknote != null) {
            buttonSaveBanknote.setOnClickListener(v -> {
                String name = "";
                String country = editCountry != null ? editCountry.getText().toString().trim() : "";
                String yearStr = editYear != null ? editYear.getText().toString().trim() : "";
                String denomination = editDenomination != null ? editDenomination.getText().toString().trim() : "";
                String currency = editCurrency != null ? editCurrency.getText().toString().trim() : "";
                String condition = (spinnerCondition != null && spinnerCondition.getSelectedItem() != null)
                        ? spinnerCondition.getSelectedItem().toString()
                        : "";
                String qtyStr = editQuantity != null ? editQuantity.getText().toString().trim() : "";
                String valStr = editEstimatedValue != null ? editEstimatedValue.getText().toString().trim() : "";

                if (country.isEmpty()) {
                    if (editCountry != null) {
                        editCountry.setError("Enter country");
                        editCountry.requestFocus();
                    }
                    return;
                }

                if (yearStr.isEmpty()) {
                    if (editYear != null) {
                        editYear.setError("Enter year");
                        editYear.requestFocus();
                    }
                    return;
                }

                int year;
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException e) {
                    if (editYear != null) {
                        editYear.setError("Invalid year");
                        editYear.requestFocus();
                    }
                    return;
                }
                if (year <= 0) {
                    if (editYear != null) {
                        editYear.setError("Year must be greater than 0");
                        editYear.requestFocus();
                    }
                    return;
                }

                if (denomination.isEmpty()) {
                    if (editDenomination != null) {
                        editDenomination.setError("Enter denomination");
                        editDenomination.requestFocus();
                    }
                    return;
                }

                double denomValue;
                try {
                    denomValue = Double.parseDouble(denomination);
                } catch (NumberFormatException e) {
                    if (editDenomination != null) {
                        editDenomination.setError("Invalid denomination");
                        editDenomination.requestFocus();
                    }
                    return;
                }
                if (denomValue <= 0) {
                    if (editDenomination != null) {
                        editDenomination.setError("Denomination must be greater than 0");
                        editDenomination.requestFocus();
                    }
                    return;
                }

                if (currency.isEmpty()) {
                    if (editCurrency != null) {
                        editCurrency.setError("Enter currency");
                        editCurrency.requestFocus();
                    }
                    return;
                }

                if (condition.isEmpty()) {
                    Toast.makeText(this, "Select condition", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (qtyStr.isEmpty()) {
                    if (editQuantity != null) {
                        editQuantity.setError("Enter quantity");
                        editQuantity.requestFocus();
                    }
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(qtyStr);
                } catch (NumberFormatException e) {
                    if (editQuantity != null) {
                        editQuantity.setError("Invalid quantity");
                        editQuantity.requestFocus();
                    }
                    return;
                }
                if (quantity < 1) {
                    if (editQuantity != null) {
                        editQuantity.setError("Quantity must be at least 1");
                        editQuantity.requestFocus();
                    }
                    return;
                }

                double denominationValue;
                try {
                    denominationValue = Double.parseDouble(denomination);
                } catch (NumberFormatException e) {
                    if (editDenomination != null) {
                        editDenomination.setError("Invalid denomination");
                        editDenomination.requestFocus();
                    }
                    return;
                }
                if (denominationValue <= 0) {
                    if (editDenomination != null) {
                        editDenomination.setError("Denomination must be greater than 0");
                        editDenomination.requestFocus();
                    }
                    return;
                }

                long rowId = dbHelper.insertBanknote(name, country, year, denomination, currency, condition, 0.0, denominationValue, quantity, "", false
                );

                if (rowId != -1) {
                    Toast.makeText(this, "Saved banknote #" + rowId, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error saving banknote", Toast.LENGTH_SHORT).show();
                }
            });
        }

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
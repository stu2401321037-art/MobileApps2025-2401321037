package com.example.numistracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.numistracker.di.AppContainer;
import com.example.numistracker.viewmodel.FormViewModel;


public class banknotes extends AppCompatActivity {

    private PhotoCaptureHelper photoHelper;
    private FormViewModel formViewModel;
    private long editingId = -1;


    private void applySavedTheme() {
        SharedPreferences preferences =
                getSharedPreferences("AppSettings", MODE_PRIVATE);

        boolean isDark =
                preferences.getBoolean("dark_mode", false);

        int desiredMode = isDark
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;

        if (AppCompatDelegate.getDefaultNightMode()
                != desiredMode) {
            AppCompatDelegate.setDefaultNightMode(desiredMode);
        }
    }

    private ArrayAdapter<CharSequence> createConditionAdapter() {
        ArrayAdapter<CharSequence> adapter =
                new ArrayAdapter<CharSequence>(
                        this,
                        android.R.layout.simple_spinner_item,
                        getResources().getStringArray(
                                R.array.banknote_conditions)
                ) {
                    @NonNull
                    @Override
                    public View getView(
                            int position,
                            View convertView,
                            @NonNull ViewGroup parent) {

                        TextView view = (TextView) super.getView(
                                position, convertView, parent);

                        view.setTextColor(ContextCompat.getColor(
                                parent.getContext(), R.color.gold));

                        return view;
                    }
                };

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);

        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_banknotes);

        AppContainer container = AppContainer.getInstance(this);
        formViewModel = new ViewModelProvider(
                this,
                new FormViewModel.Factory(container.getRepository())
        ).get(FormViewModel.class);

        ImageButton buttonBack =
                findViewById(R.id.button4);

        Spinner spinnerCondition =
                findViewById(R.id.spinnerCondition);

        EditText editCountry =
                findViewById(R.id.editCountry);

        EditText editYear =
                findViewById(R.id.editYear);

        EditText editDenomination =
                findViewById(R.id.editDenomination);

        EditText editCurrency =
                findViewById(R.id.editCurrency);

        EditText editQuantity =
                findViewById(R.id.editQuantity);

        EditText editEstimatedValue =
                findViewById(R.id.editEstimatedValue);

        Button buttonSaveBanknote =
                findViewById(R.id.buttonSaveBanknote);

        ImageView imagePreview =
                findViewById(R.id.imagePreview);

        LinearLayout photoContainer =
                findViewById(R.id.photoContainer);

        photoHelper = new PhotoCaptureHelper(
                this,
                imagePreview
        );
        photoHelper.restoreState(savedInstanceState);

        photoContainer.setOnClickListener(
                v -> photoHelper.openCamera()
        );

        buttonBack.setOnClickListener(v -> finish());

        spinnerCondition.setAdapter(
                createConditionAdapter()
        );

        editingId = getIntent().getLongExtra(
                ItemsListActivity.EXTRA_ID, -1
        );

        formViewModel.getLoadResult().observe(this, result -> {
            if (result == null) return;
            if (result.error != null || result.values == null) {
                Toast.makeText(this, "Banknote not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            String[] values = result.values;
            editCountry.setText(values[0]);
            editYear.setText(values[1]);
            editDenomination.setText(values[2]);
            editCurrency.setText(values[3]);
            editQuantity.setText(values[5]);
            editEstimatedValue.setText(values[6]);
            for (int i = 0; i < spinnerCondition.getCount(); i++) {
                if (spinnerCondition.getItemAtPosition(i).toString().equals(values[4])) {
                    spinnerCondition.setSelection(i);
                    break;
                }
            }
            buttonSaveBanknote.setEnabled(true);
        });

        formViewModel.getSaveResult().observe(this, result -> {
            if (result == null || isFinishing() || isDestroyed()) return;
            buttonSaveBanknote.setEnabled(true);
            if (result.success) {
                Toast.makeText(this,
                        editingId == -1 ? "Banknote saved successfully" : "Banknote updated successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                if (result.id > 0) {
                    editingId = result.id;
                    buttonSaveBanknote.setText("Save Changes");
                }
                Toast.makeText(this, result.error, Toast.LENGTH_LONG).show();
            }
        });

        if (editingId != -1) {
            buttonSaveBanknote.setText("Save Changes");
            buttonSaveBanknote.setEnabled(false);
            formViewModel.load(editingId, false);
        }

        buttonSaveBanknote.setOnClickListener(v -> {

            String country =
                    editCountry.getText().toString().trim();

            String yearStr =
                    editYear.getText().toString().trim();

            String denomination =
                    editDenomination.getText().toString().trim();

            String currency =
                    editCurrency.getText().toString().trim();

            String condition =
                    spinnerCondition.getSelectedItem() != null
                            ? spinnerCondition.getSelectedItem().toString()
                            : "";

            String quantityStr =
                    editQuantity.getText().toString().trim();

            String valueStr =
                    editEstimatedValue.getText().toString().trim();

            if (country.isEmpty()) {
                showError(editCountry, "Enter country");
                return;
            }

            if (yearStr.isEmpty()) {
                showError(editYear, "Enter year");
                return;
            }

            int year;

            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                showError(editYear, "Invalid year");
                return;
            }

            if (year <= 0) {
                showError(
                        editYear,
                        "Year must be greater than 0"
                );
                return;
            }

            if (denomination.isEmpty()) {
                showError(
                        editDenomination,
                        "Enter denomination"
                );
                return;
            }

            double denominationValue;

            try {
                denominationValue = Double.parseDouble(
                        denomination.replace(',', '.')
                );
            } catch (NumberFormatException e) {
                showError(
                        editDenomination,
                        "Invalid denomination"
                );
                return;
            }

            if (!Double.isFinite(denominationValue)
                    || denominationValue <= 0) {

                showError(
                        editDenomination,
                        "Denomination must be positive"
                );
                return;
            }

            if (currency.isEmpty()) {
                showError(editCurrency, "Enter currency");
                return;
            }

            if (condition.isEmpty()) {
                Toast.makeText(
                        this,
                        "Select condition",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (quantityStr.isEmpty()) {
                showError(editQuantity, "Enter quantity");
                return;
            }

            int quantity;

            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                showError(editQuantity, "Invalid quantity");
                return;
            }

            if (quantity < 1) {
                showError(
                        editQuantity,
                        "Quantity must be at least 1"
                );
                return;
            }

            Double estimatedValue = null;

            if (!valueStr.isEmpty()) {
                try {
                    estimatedValue = Double.parseDouble(
                            valueStr.replace(',', '.')
                    );
                } catch (NumberFormatException e) {
                    showError(
                            editEstimatedValue,
                            "Invalid estimated value"
                    );
                    return;
                }

                if (!Double.isFinite(estimatedValue)
                        || estimatedValue < 0) {

                    showError(
                            editEstimatedValue,
                            "Estimated value cannot be negative"
                    );
                    return;
                }
            }

            String name = denomination + " " + currency
                    + " (" + year + ")";

            final int finalYear = year;
            final int finalQuantity = quantity;
            final Double finalEstimatedValue = estimatedValue;
            buttonSaveBanknote.setEnabled(false);
            formViewModel.save(
                    editingId,
                    false,
                    name,
                    country,
                    finalYear,
                    denomination,
                    currency,
                    condition,
                    finalEstimatedValue,
                    finalQuantity,
                    photoHelper.getNewPhotoPaths()
            );
        });

        setupWindowInsets();
    }

    private void showError(
            EditText field,
            String message) {

        field.setError(message);
        field.requestFocus();
    }

    private void setupWindowInsets() {
        View root = findViewById(R.id.main);

        final int left = root.getPaddingLeft();
        final int top = root.getPaddingTop();
        final int right = root.getPaddingRight();
        final int bottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, windowInsets) -> {

                    Insets systemBars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            left + systemBars.left,
                            top + systemBars.top,
                            right + systemBars.right,
                            bottom + systemBars.bottom
                    );

                    return windowInsets;
                }
        );
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        photoHelper.saveState(outState);
        super.onSaveInstanceState(outState);
    }
}
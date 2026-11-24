package com.example.numistracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.numistracker.di.AppContainer;
import com.example.numistracker.ui.TypePieChart;
import com.example.numistracker.viewmodel.CollectionViewModel;

import java.util.Locale;

public class CollectionActivity extends AppCompatActivity {

    private CollectionViewModel viewModel;

    private TypePieChart pieChart;

    private TextView totalValue;
    private TextView coinValue;
    private TextView banknoteValue;
    private TextView coinPercentValue;
    private TextView banknotePercentValue;
    private TextView emptyText;
    private TextView errorText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collection);

        totalValue = findViewById(R.id.statsTotalTypes);
        coinValue = findViewById(R.id.statsCoinTypes);
        banknoteValue =
                findViewById(R.id.statsBanknoteTypes);

        coinPercentValue =
                findViewById(R.id.statsCoinPercent);

        banknotePercentValue =
                findViewById(R.id.statsBanknotePercent);

        emptyText = findViewById(R.id.statsEmpty);
        errorText = findViewById(R.id.statsError);

        pieChart = findViewById(R.id.typePieChart);

        findViewById(R.id.back_button)
                .setOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(
                this,
                new CollectionViewModel.Factory(
                        AppContainer.getInstance(this)
                                .getRepository()
                )
        ).get(CollectionViewModel.class);

        viewModel.getStats().observe(
                this,
                this::showStats
        );

        viewModel.getError().observe(this, message -> {

            if (message == null || message.isEmpty()) {
                errorText.setVisibility(View.GONE);
            } else {
                errorText.setText(message);
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.refreshStats();
    }

    private void showStats(CollectionViewModel.Stats stats) {

        if (stats == null) {
            return;
        }

        totalValue.setText(
                String.valueOf(stats.total())
        );

        coinValue.setText(
                String.valueOf(stats.coinTypes)
        );

        banknoteValue.setText(
                String.valueOf(stats.banknoteTypes)
        );

        coinPercentValue.setText(
                String.format(
                        Locale.US,
                        "%.1f%%",
                        stats.coinPercent()
                )
        );

        banknotePercentValue.setText(
                String.format(
                        Locale.US,
                        "%.1f%%",
                        stats.banknotePercent()
                )
        );

        pieChart.setTypeCounts(
                stats.coinTypes,
                stats.banknoteTypes
        );

        emptyText.setVisibility(
                stats.total() == 0
                        ? View.VISIBLE
                        : View.GONE
        );
    }
}
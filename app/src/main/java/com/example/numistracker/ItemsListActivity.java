
package com.example.numistracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.numistracker.di.AppContainer;
import com.example.numistracker.model.CollectionItem;
import com.example.numistracker.viewmodel.ItemsViewModel;

import java.util.List;

public class ItemsListActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "item_type";
    public static final String EXTRA_ID = "item_id";

    public static final String TYPE_COINS = "coins";
    public static final String TYPE_BANKNOTES = "banknotes";

    private LinearLayout itemsContainer;
    private TextView textEmpty;

    private boolean isCoins;
    private ItemsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences =
                getSharedPreferences("AppSettings", MODE_PRIVATE);

        boolean dark =
                preferences.getBoolean("dark_mode", false);

        int mode = dark
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;

        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode);
        }

        super.onCreate(savedInstanceState);

        WindowCompat.enableEdgeToEdge(getWindow());

        isCoins = TYPE_COINS.equals(
                getIntent().getStringExtra(EXTRA_TYPE)
        );

        setContentView(
                isCoins
                        ? R.layout.activity_coins_list
                        : R.layout.activity_banknotes_list
        );

        itemsContainer = findViewById(R.id.itemsContainer);
        textEmpty = findViewById(R.id.textEmpty);

        ImageButton buttonBack =
                findViewById(R.id.buttonBackList);

        TextView buttonAdd =
                findViewById(R.id.buttonAddItem);

        AppContainer container =
                AppContainer.getInstance(this);

        viewModel = new ViewModelProvider(
                this,
                new ItemsViewModel.Factory(
                        container.getRepository()
                )
        ).get(ItemsViewModel.class);

        viewModel.getItems().observe(this, collectionItems -> {

            if (collectionItems == null) {
                return;
            }

            showItems(collectionItems);
        });

        viewModel.getError().observe(this, message -> {

            if (message != null) {
                Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        viewModel.getDeletionResult().observe(this, success -> {

            if (success != null) {
                Toast.makeText(
                        this,
                        success
                                ? "Item deleted"
                                : "Error deleting item",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        buttonBack.setOnClickListener(v -> finish());

        buttonAdd.setOnClickListener(
                v -> openForm(-1)
        );

        setupWindowInsets();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (viewModel != null) {
            loadItems();
        }
    }

    private void loadItems() {
        viewModel.loadItems(isCoins);
    }

    private void showItems(List<CollectionItem> items) {

        itemsContainer.removeAllViews();

        textEmpty.setVisibility(
                items.isEmpty()
                        ? View.VISIBLE
                        : View.GONE
        );

        for (CollectionItem item : items) {
            addItemCard(item);
        }
    }

    private int dp(int value) {
        return Math.round(
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }

    private GradientDrawable createCardBackground() {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                ContextCompat.getColor(
                        this,
                        R.color.surface_color
                )
        );

        drawable.setCornerRadius(dp(12));

        drawable.setStroke(
                dp(1),
                ContextCompat.getColor(
                        this,
                        R.color.gold
                )
        );

        return drawable;
    }

    private TextView makeText(String value, int size) {

        TextView text = new TextView(this);

        text.setText(value);
        text.setTextSize(size);

        text.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.text_color
                )
        );

        return text;
    }

    private void addItemCard(CollectionItem item) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);

        card.setPadding(
                dp(16),
                dp(14),
                dp(16),
                dp(14)
        );

        card.setBackground(createCardBackground());

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                dp(12)
        );

        TextView name = makeText(item.name, 19);

        name.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        name.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.gold
                )
        );

        TextView details = makeText(
                (item.country == null
                        ? ""
                        : item.country)
                        + " | " + item.year
                        + " | Qty: " + item.quantity,
                14
        );

        details.setPadding(
                0,
                dp(6),
                0,
                dp(4)
        );

        TextView condition = makeText(
                "Condition: " +
                        (item.condition == null
                                ? "-"
                                : item.condition),
                13
        );

        LinearLayout actions = new LinearLayout(this);

        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.END);
        actions.setPadding(0, dp(12), 0, 0);

        Button edit = new Button(this);

        edit.setText(R.string.edit);
        edit.setAllCaps(false);

        edit.setOnClickListener(
                v -> openForm(item.id)
        );

        Button delete = new Button(this);

        delete.setText(R.string.delete);
        delete.setAllCaps(false);

        delete.setOnClickListener(
                v -> confirmDelete(item)
        );

        actions.addView(edit);
        actions.addView(delete);

        if (item.photoPath != null
                && !item.photoPath.isEmpty()) {

            BitmapFactory.Options options =
                    new BitmapFactory.Options();

            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(
                    item.photoPath,
                    options
            );

            int sampleSize = 1;

            while (options.outWidth / sampleSize > 800
                    || options.outHeight / sampleSize > 800) {
                sampleSize *= 2;
            }

            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;

            Bitmap bitmap = BitmapFactory.decodeFile(
                    item.photoPath,
                    options
            );

            if (bitmap != null) {

                ImageView photo = new ImageView(this);

                LinearLayout.LayoutParams photoParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                dp(180)
                        );

                photoParams.bottomMargin = dp(12);

                photo.setImageBitmap(bitmap);

                photo.setScaleType(
                        ImageView.ScaleType.CENTER_CROP
                );

                photo.setClipToOutline(true);

                card.addView(
                        photo,
                        photoParams
                );
            }
        }

        card.addView(name);
        card.addView(details);
        card.addView(condition);
        card.addView(actions);

        itemsContainer.addView(
                card,
                cardParams
        );
    }

    private void openForm(long id) {

        Intent intent = new Intent(
                this,
                isCoins
                        ? CoinsActivity.class
                        : banknotes.class
        );

        if (id != -1) {
            intent.putExtra(EXTRA_ID, id);
        }

        startActivity(intent);
    }

    private void confirmDelete(CollectionItem item) {

        new AlertDialog.Builder(this)
                .setTitle("Delete item")
                .setMessage(
                        "Delete \"" + item.name + "\"?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) ->
                                deleteItem(item.id)
                )
                .show();
    }

    private void deleteItem(long id) {
        viewModel.deleteItem(id, isCoins);
    }

    private void setupWindowInsets() {

        View root = findViewById(R.id.main);

        int left = root.getPaddingLeft();
        int top = root.getPaddingTop();
        int right = root.getPaddingRight();
        int bottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, insets) -> {

                    Insets bars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            left + bars.left,
                            top + bars.top,
                            right + bars.right,
                            bottom + bars.bottom
                    );

                    return insets;
                }
        );
    }
}
package com.example.numistracker.data;

import android.database.Cursor;
import com.example.numistracker.model.CollectionItem;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import com.example.numistracker.model.CollectionStats;

public class NumisRepository {
    private final DatabaseHelper dbHelper;

    public NumisRepository(DatabaseHelper dbHelper) {
        if (dbHelper == null) throw new IllegalArgumentException("DatabaseHelper is null");
        this.dbHelper = dbHelper;
    }

    public List<CollectionItem> getItems(boolean isCoins) {
        List<CollectionItem> items = new ArrayList<>();
        try (Cursor cursor = isCoins ? dbHelper.getAllCoins() : dbHelper.getAllBanknotes()) {
            int id = cursor.getColumnIndexOrThrow("id");
            int name = cursor.getColumnIndexOrThrow("name");
            int country = cursor.getColumnIndexOrThrow("country");
            int year = cursor.getColumnIndexOrThrow("year");
            int qty = cursor.getColumnIndexOrThrow("quantity");
            int condition = cursor.getColumnIndexOrThrow("condition");
            while (cursor.moveToNext()) {
                long itemId = cursor.getLong(id);
                items.add(new CollectionItem(itemId, cursor.getString(name),
                        cursor.getString(country), cursor.getInt(year),
                        cursor.getInt(qty), cursor.getString(condition),
                        getFirstPhotoPath(itemId, isCoins)));
            }
        }
        return items;
    }

    public String getFirstPhotoPath(long id, boolean isCoins) {
        try (Cursor cursor = isCoins ? dbHelper.getCoinImages(id) : dbHelper.getBanknoteImages(id)) {
            if (cursor.moveToFirst()) {
                int column = cursor.getColumnIndexOrThrow("imagePath");
                return cursor.getString(column);
            }
        }
        return null;
    }

    public int deleteItem(long id, boolean isCoins) {
        return isCoins ? dbHelper.deleteCoin(id) : dbHelper.deleteBanknote(id);
    }

    public Cursor getCoinById(long id) { return dbHelper.getCoinById(id); }
    public Cursor getBanknoteById(long id) { return dbHelper.getBanknoteById(id); }

    public long insertCoin(String name, String country, int year, String denomination,
                           String currency, String material, String condition,
                           double purchasePrice, Double estimatedValue, int quantity,
                           String notes, boolean isPublic) {
        return dbHelper.insertCoin(name, country, year, denomination, currency,
                material, condition, purchasePrice, estimatedValue, quantity, notes, isPublic);
    }

    public long insertBanknote(String name, String country, int year, String denomination,
                               String currency, String condition, double purchasePrice,
                               Double estimatedValue, int quantity, String notes, boolean isPublic) {
        return dbHelper.insertBanknote(name, country, year, denomination, currency,
                condition, purchasePrice, estimatedValue, quantity, notes, isPublic);
    }

    public int updateCoinForm(long id, String name, String country, int year,
                              String denomination, String currency, String condition,
                              Double estimatedValue, int quantity) {
        return dbHelper.updateCoinForm(id, name, country, year, denomination,
                currency, condition, estimatedValue, quantity);
    }

    public int updateBanknoteForm(long id, String name, String country, int year,
                                  String denomination, String currency, String condition,
                                  Double estimatedValue, int quantity) {
        return dbHelper.updateBanknoteForm(id, name, country, year, denomination,
                currency, condition, estimatedValue, quantity);
    }

    public long insertCoinImage(long id, String path) { return dbHelper.insertCoinImage(id, path); }
    public long insertBanknoteImage(long id, String path) { return dbHelper.insertBanknoteImage(id, path); }

    public void savePhotos(long itemId, boolean isCoins, List<String> paths) {
        Set<String> existing = new HashSet<>();
        try (Cursor cursor = isCoins ? dbHelper.getCoinImages(itemId)
                : dbHelper.getBanknoteImages(itemId)) {
            int imageColumn = cursor.getColumnIndexOrThrow("imagePath");
            while (cursor.moveToNext()) existing.add(cursor.getString(imageColumn));
        }
        for (String path : paths) {
            if (existing.contains(path)) continue;
            long result = isCoins ? insertCoinImage(itemId, path) : insertBanknoteImage(itemId, path);
            if (result < 0) throw new IllegalStateException("Could not save photo");
            existing.add(path);
        }
    }

    public CollectionStats getCollectionStats() {
        return new CollectionStats(
                dbHelper.getCoinTypeCount(),
                dbHelper.getBanknoteTypeCount()
        );
    }
}
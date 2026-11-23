package com.example.numistracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "numistracker.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE coins (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "country TEXT, " +
                "year INTEGER, " +
                "denomination TEXT, " +
                "currency TEXT, " +
                "material TEXT, " +
                "condition TEXT, " +
                "purchasePrice REAL, " +
                "estimatedValue REAL, " +
                "quantity INTEGER NOT NULL DEFAULT 1, " +
                "notes TEXT, " +
                "isPublic INTEGER NOT NULL DEFAULT 0)");

        db.execSQL("CREATE TABLE coin_images (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "coinId INTEGER NOT NULL, " +
                "imagePath TEXT NOT NULL, " +
                "FOREIGN KEY (coinId) REFERENCES coins(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE banknotes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "country TEXT, " +
                "year INTEGER, " +
                "denomination TEXT, " +
                "currency TEXT, " +
                "condition TEXT, " +
                "purchasePrice REAL, " +
                "estimatedValue REAL, " +
                "quantity INTEGER NOT NULL DEFAULT 1, " +
                "notes TEXT, " +
                "isPublic INTEGER NOT NULL DEFAULT 0)");

        db.execSQL("CREATE TABLE banknote_images (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "banknoteId INTEGER NOT NULL, " +
                "imagePath TEXT NOT NULL, " +
                "FOREIGN KEY (banknoteId) REFERENCES banknotes(id) ON DELETE CASCADE)");
    }

    public long insertCoin(String name, String country, int year, String denomination, String currency, String material, String condition, double purchasePrice, double estimatedValue, int quantity, String notes, boolean isPublic) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("country", country);
        values.put("year", year);
        values.put("denomination", denomination);
        values.put("currency", currency);
        values.put("material", material);
        values.put("condition", condition);
        values.put("purchasePrice", purchasePrice);
        values.put("estimatedValue", estimatedValue);
        values.put("quantity", quantity);
        values.put("notes", notes);
        values.put("isPublic", isPublic ? 1 : 0);

        return db.insert("coins", null, values);
    }

    public long insertBanknote(String name, String country, int year, String denomination, String currency, String condition, double purchasePrice, double estimatedValue, int quantity, String notes, boolean isPublic) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("country", country);
        values.put("year", year);
        values.put("denomination", denomination);
        values.put("currency", currency);
        values.put("condition", condition);
        values.put("purchasePrice", purchasePrice);
        values.put("estimatedValue", estimatedValue);
        values.put("quantity", quantity);
        values.put("notes", notes);
        values.put("isPublic", isPublic ? 1 : 0);

        return db.insert("banknotes", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
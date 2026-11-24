package com.example.numistracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.DatabaseUtils;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "numistracker.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_COINS = "coins";
    public static final String TABLE_BANKNOTES = "banknotes";
    public static final String TABLE_COIN_IMAGES = "coin_images";
    public static final String TABLE_BANKNOTE_IMAGES = "banknote_images";

    public DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
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
                "name TEXT NOT NULL CHECK(length(trim(name)) > 0)," +
                "country TEXT, " +
                "year INTEGER, " +
                "denomination TEXT, " +
                "currency TEXT, " +
                "material TEXT, " +
                "condition TEXT, " +
                "purchasePrice REAL CHECK(" +
                "purchasePrice IS NULL OR purchasePrice >= 0), " +
                "estimatedValue REAL CHECK(" +
                "estimatedValue IS NULL OR estimatedValue >= 0), " +
                "quantity INTEGER NOT NULL DEFAULT 1 " +
                "CHECK(quantity > 0), " +
                "notes TEXT, " +
                "isPublic INTEGER NOT NULL DEFAULT 0 " +
                "CHECK(isPublic IN (0, 1)))");

        db.execSQL("CREATE TABLE coin_images (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "coinId INTEGER NOT NULL, " +
                "imagePath TEXT NOT NULL, " +
                "FOREIGN KEY (coinId) REFERENCES coins(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE banknotes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL CHECK(length(trim(name)) > 0) , " +
                "country TEXT, " +
                "year INTEGER, " +
                "denomination TEXT, " +
                "currency TEXT, " +
                "condition TEXT, " +
                "purchasePrice REAL CHECK(" +
                "purchasePrice IS NULL OR purchasePrice >= 0), " +
                "estimatedValue REAL CHECK(" +
                "estimatedValue IS NULL OR estimatedValue >= 0), " +
                "quantity INTEGER NOT NULL DEFAULT 1 " +
                "CHECK(quantity > 0), " +
                "notes TEXT, " +
                "isPublic INTEGER NOT NULL DEFAULT 0 " +
                "CHECK(isPublic IN (0, 1)))");

        db.execSQL("CREATE TABLE banknote_images (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "banknoteId INTEGER NOT NULL, " +
                "imagePath TEXT NOT NULL, " +
                "FOREIGN KEY (banknoteId) REFERENCES banknotes(id) ON DELETE CASCADE)");

        createIndexes(db);
    }

    private void createIndexes(SQLiteDatabase db) {
        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_coin_images_coin " +
                        "ON coin_images(coinId)"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_banknote_images_banknote " +
                        "ON banknote_images(banknoteId)"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_coins_country " +
                        "ON coins(country)"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_banknotes_country " +
                        "ON banknotes(country)"
        );
    }

    private void validateItem(String name, String country, String denomination, String currency, double purchasePrice, Double estimatedValue, int quantity) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Name cannot be empty");
        }

        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Country cannot be empty");
        }

        if (denomination == null ||
                denomination.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Denomination cannot be empty");
        }

        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Currency cannot be empty");
        }

        if (!Double.isFinite(purchasePrice) ||
                purchasePrice < 0) {
            throw new IllegalArgumentException(
                    "Invalid purchase price");
        }

        if (estimatedValue != null &&
                (!Double.isFinite(estimatedValue) ||
                        estimatedValue < 0)) {
            throw new IllegalArgumentException(
                    "Invalid estimated value");
        }

        if (quantity < 1) {
            throw new IllegalArgumentException(
                    "Quantity must be at least 1");
        }
    }

    private ContentValues createCoinValues(String name, String country, int year, String denomination, String currency, String material, String condition, double purchasePrice, Double estimatedValue, int quantity, String notes, boolean isPublic) {

        validateItem(name, country, denomination, currency,
                purchasePrice, estimatedValue, quantity);

        ContentValues values = new ContentValues();

        values.put("name", name.trim());
        values.put("country", country.trim());
        values.put("year", year);
        values.put("denomination", denomination.trim());
        values.put("currency", currency.trim());
        values.put("material", material);
        values.put("condition", condition);
        values.put("purchasePrice", purchasePrice);

        if (estimatedValue == null) {
            values.putNull("estimatedValue");
        } else {
            values.put("estimatedValue", estimatedValue);
        }

        values.put("quantity", quantity);
        values.put("notes", notes);
        values.put("isPublic", isPublic ? 1 : 0);

        return values;
    }

    public long insertCoin(String name, String country, int year, String denomination, String currency, String material, String condition, double purchasePrice, Double estimatedValue, int quantity, String notes, boolean isPublic) {

        ContentValues values = createCoinValues(
                name, country, year, denomination, currency,
                material, condition, purchasePrice,
                estimatedValue, quantity, notes, isPublic
        );

        return getWritableDatabase()
                .insert(TABLE_COINS, null, values);
    }

    public Cursor getAllCoins() {
        return getReadableDatabase().query(TABLE_COINS, null, null, null, null, null, "id DESC"
        );
    }

    public Cursor getCoinById(long id) {
        return getReadableDatabase().query(TABLE_COINS, null, "id = ?",new String[]{String.valueOf(id)}, null, null, null
        );
    }

    public Cursor searchCoins(String query) {
        String pattern = "%" + escapeLike(query) + "%";

        return getReadableDatabase().query(
                TABLE_COINS,
                null,
                "name LIKE ? ESCAPE '\\' OR " +
                        "country LIKE ? ESCAPE '\\' OR " +
                        "currency LIKE ? ESCAPE '\\'",
                new String[]{pattern, pattern, pattern},
                null,
                null,
                "id DESC"
        );
    }

    public int updateCoin(long id, String name, String country, int year, String denomination, String currency, String material, String condition, double purchasePrice, Double estimatedValue, int quantity, String notes, boolean isPublic) {

        ContentValues values = createCoinValues(
                name, country, year, denomination, currency,
                material, condition, purchasePrice,
                estimatedValue, quantity, notes, isPublic
        );

        return getWritableDatabase().update(
                TABLE_COINS,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int updateCoinForm(long id, String name, String country, int year, String denomination, String currency, String condition, Double estimatedValue, int quantity) {

        validateItem(name, country, denomination, currency,
                0.0, estimatedValue, quantity);

        ContentValues values = new ContentValues();

        values.put("name", name.trim());
        values.put("country", country.trim());
        values.put("year", year);
        values.put("denomination", denomination.trim());
        values.put("currency", currency.trim());
        values.put("condition", condition);
        values.put("quantity", quantity);

        if (estimatedValue == null) {
            values.putNull("estimatedValue");
        } else {
            values.put("estimatedValue", estimatedValue);
        }

        return getWritableDatabase().update(
                TABLE_COINS,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteCoin(long id) {
        return getWritableDatabase().delete(
                TABLE_COINS,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            createIndexes(db);
        }
    }

    private ContentValues createBanknoteValues(String name, String country, int year, String denomination, String currency, String condition, double purchasePrice, Double estimatedValue, int quantity, String notes, boolean isPublic) {

        validateItem(name, country, denomination, currency,
                purchasePrice, estimatedValue, quantity);

        ContentValues values = new ContentValues();

        values.put("name", name.trim());
        values.put("country", country.trim());
        values.put("year", year);
        values.put("denomination", denomination.trim());
        values.put("currency", currency.trim());
        values.put("condition", condition);
        values.put("purchasePrice", purchasePrice);

        if (estimatedValue == null) {
            values.putNull("estimatedValue");
        } else {
            values.put("estimatedValue", estimatedValue);
        }

        values.put("quantity", quantity);
        values.put("notes", notes);
        values.put("isPublic", isPublic ? 1 : 0);

        return values;
    }

    public long insertBanknote(String name, String country, int year, String denomination, String currency, String condition, double purchasePrice, Double estimatedValue, int quantity, String notes, boolean isPublic) {

        ContentValues values = createBanknoteValues(
                name, country, year, denomination, currency,
                condition, purchasePrice, estimatedValue,
                quantity, notes, isPublic
        );

        return getWritableDatabase().insert(
                TABLE_BANKNOTES, null, values);
    }

    public Cursor getAllBanknotes() {
        return getReadableDatabase().query(
                TABLE_BANKNOTES,
                null,
                null,
                null,
                null,
                null,
                "id DESC"
        );
    }

    public Cursor getBanknoteById(long id) {
        return getReadableDatabase().query(
                TABLE_BANKNOTES,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }

    public Cursor searchBanknotes(String query) {
        String pattern = "%" + escapeLike(query) + "%";

        return getReadableDatabase().query(
                TABLE_BANKNOTES,
                null,
                "name LIKE ? ESCAPE '\\' OR " +
                        "country LIKE ? ESCAPE '\\' OR " +
                        "currency LIKE ? ESCAPE '\\'",
                new String[]{pattern, pattern, pattern},
                null,
                null,
                "id DESC"
        );
    }

    public int updateBanknote(long id, String name, String country, int year, String denomination, String currency, String condition, double purchasePrice, Double estimatedValue, int quantity, String notes, boolean isPublic) {

        ContentValues values = createBanknoteValues(
                name, country, year, denomination, currency,
                condition, purchasePrice, estimatedValue,
                quantity, notes, isPublic
        );

        return getWritableDatabase().update(
                TABLE_BANKNOTES,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int updateBanknoteForm(long id, String name, String country, int year, String denomination, String currency, String condition, Double estimatedValue, int quantity) {

        validateItem(name, country, denomination, currency,
                0.0, estimatedValue, quantity);

        ContentValues values = new ContentValues();

        values.put("name", name.trim());
        values.put("country", country.trim());
        values.put("year", year);
        values.put("denomination", denomination.trim());
        values.put("currency", currency.trim());
        values.put("condition", condition);
        values.put("quantity", quantity);

        if (estimatedValue == null) {
            values.putNull("estimatedValue");
        } else {
            values.put("estimatedValue", estimatedValue);
        }

        return getWritableDatabase().update(
                TABLE_BANKNOTES,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteBanknote(long id) {
        return getWritableDatabase().delete(
                TABLE_BANKNOTES,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public long insertCoinImage(long coinId, String imagePath) {
        validateImagePath(imagePath);

        ContentValues values = new ContentValues();
        values.put("coinId", coinId);
        values.put("imagePath", imagePath);

        return getWritableDatabase().insert(
                TABLE_COIN_IMAGES, null, values);
    }

    public long insertBanknoteImage(
            long banknoteId, String imagePath) {

        validateImagePath(imagePath);

        ContentValues values = new ContentValues();
        values.put("banknoteId", banknoteId);
        values.put("imagePath", imagePath);

        return getWritableDatabase().insert(
                TABLE_BANKNOTE_IMAGES, null, values);
    }

    public Cursor getCoinImages(long coinId) {
        return getReadableDatabase().query(
                TABLE_COIN_IMAGES,
                null,
                "coinId = ?",
                new String[]{String.valueOf(coinId)},
                null,
                null,
                "id ASC"
        );
    }

    public Cursor getBanknoteImages(long banknoteId) {
        return getReadableDatabase().query(
                TABLE_BANKNOTE_IMAGES,
                null,
                "banknoteId = ?",
                new String[]{String.valueOf(banknoteId)},
                null,
                null,
                "id ASC"
        );
    }

    public int deleteCoinImage(long imageId) {
        return getWritableDatabase().delete(
                TABLE_COIN_IMAGES,
                "id = ?",
                new String[]{String.valueOf(imageId)}
        );
    }

    public int deleteBanknoteImage(long imageId) {
        return getWritableDatabase().delete(
                TABLE_BANKNOTE_IMAGES,
                "id = ?",
                new String[]{String.valueOf(imageId)}
        );
    }

    private void validateImagePath(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Image path cannot be empty");
        }
    }

    private String escapeLike(String value) {
        if (value == null) return "";

        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
    public int getCoinTypeCount() {
        return (int) DatabaseUtils.queryNumEntries(
                getReadableDatabase(),
                TABLE_COINS
        );
    }

    public int getBanknoteTypeCount() {
        return (int) DatabaseUtils.queryNumEntries(
                getReadableDatabase(),
                TABLE_BANKNOTES
        );
    }
}
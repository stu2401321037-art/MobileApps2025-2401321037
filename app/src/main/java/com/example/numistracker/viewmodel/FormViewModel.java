package com.example.numistracker.viewmodel;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.numistracker.data.NumisRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FormViewModel extends ViewModel {
    private final NumisRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<LoadResult> loadResult = new MutableLiveData<>();
    private final MutableLiveData<SaveResult> saveResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> busy = new MutableLiveData<>(false);

    public FormViewModel(NumisRepository repository) { this.repository = repository; }
    public LiveData<LoadResult> getLoadResult() { return loadResult; }
    public LiveData<SaveResult> getSaveResult() { return saveResult; }
    public LiveData<Boolean> getBusy() { return busy; }

    public void load(long itemId, boolean isCoin) {
        busy.setValue(true);
        executor.execute(() -> {
            try (Cursor cursor = isCoin ? repository.getCoinById(itemId)
                    : repository.getBanknoteById(itemId)) {
                if (!cursor.moveToFirst()) {
                    loadResult.postValue(new LoadResult(null, "Item not found"));
                } else {
                    String[] columns = {"country", "year", "denomination", "currency",
                            "condition", "quantity", "estimatedValue"};
                    String[] values = new String[columns.length];
                    for (int i = 0; i < columns.length; i++) {
                        int column = cursor.getColumnIndexOrThrow(columns[i]);
                        values[i] = cursor.isNull(column) ? "" : cursor.getString(column);
                    }
                    loadResult.postValue(new LoadResult(values, null));
                }
            } catch (Exception e) {
                loadResult.postValue(new LoadResult(null, "Error loading item"));
            } finally {
                busy.postValue(false);
            }
        });
    }

    public void save(long editingId, boolean isCoin, String name, String country,
                     int year, String denomination, String currency, String condition,
                     Double estimatedValue, int quantity, List<String> newPhotoPaths) {
        busy.setValue(true);
        List<String> photos = new ArrayList<>(newPhotoPaths);
        executor.execute(() -> {
            long id = -1;
            try {
                if (editingId == -1) {
                    id = isCoin
                            ? repository.insertCoin(name, country, year, denomination, currency,
                            "", condition, 0.0, estimatedValue, quantity, "", false)
                            : repository.insertBanknote(name, country, year, denomination,
                            currency, condition, 0.0, estimatedValue, quantity, "", false);
                } else {
                    int updated = isCoin
                            ? repository.updateCoinForm(editingId, name, country, year,
                            denomination, currency, condition, estimatedValue, quantity)
                            : repository.updateBanknoteForm(editingId, name, country, year,
                            denomination, currency, condition, estimatedValue, quantity);
                    id = updated > 0 ? editingId : -1;
                }
                if (id <= 0) throw new IllegalStateException("Could not save item");
                repository.savePhotos(id, isCoin, photos);
                saveResult.postValue(new SaveResult(id, true, null));
            } catch (Exception e) {
                saveResult.postValue(new SaveResult(id, false,
                        e.getMessage() == null ? "Error saving item or photo" : e.getMessage()));
            } finally {
                busy.postValue(false);
            }
        });
    }

    @Override
    protected void onCleared() {
        executor.shutdown();
        super.onCleared();
    }

    public static class LoadResult {
        public final String[] values;
        public final String error;
        public LoadResult(String[] values, String error) {
            this.values = values;
            this.error = error;
        }
    }

    public static class SaveResult {
        public final long id;
        public final boolean success;
        public final String error;
        public SaveResult(long id, boolean success, String error) {
            this.id = id;
            this.success = success;
            this.error = error;
        }
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final NumisRepository repository;
        public Factory(NumisRepository repository) { this.repository = repository; }
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(FormViewModel.class)) {
                return (T) new FormViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
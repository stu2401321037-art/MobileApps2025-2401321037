package com.example.numistracker.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.numistracker.data.NumisRepository;
import com.example.numistracker.model.CollectionItem;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemsViewModel extends ViewModel {
    private final NumisRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<CollectionItem>> items = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deletionResult = new MutableLiveData<>();

    public ItemsViewModel(NumisRepository repository) { this.repository = repository; }
    public LiveData<List<CollectionItem>> getItems() { return items; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getDeletionResult() { return deletionResult; }

    public void loadItems(boolean isCoins) {
        executor.execute(() -> {
            try { items.postValue(repository.getItems(isCoins)); }
            catch (Exception e) { error.postValue("Error loading items"); }
        });
    }

    public void deleteItem(long id, boolean isCoins) {
        executor.execute(() -> {
            try {
                boolean success = repository.deleteItem(id, isCoins) > 0;
                deletionResult.postValue(success);
                if (success) items.postValue(repository.getItems(isCoins));
            } catch (Exception e) {
                deletionResult.postValue(false);
            }
        });
    }

    @Override
    protected void onCleared() {
        executor.shutdown();
        super.onCleared();
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final NumisRepository repository;
        public Factory(NumisRepository repository) { this.repository = repository; }
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ItemsViewModel.class)) {
                return (T) new ItemsViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
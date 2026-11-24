
package com.example.numistracker.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.numistracker.data.NumisRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CollectionViewModel extends ViewModel {

    private final NumisRepository repository;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private final MutableLiveData<Stats> stats =
            new MutableLiveData<>();

    private final MutableLiveData<String> error =
            new MutableLiveData<>();

    public CollectionViewModel(NumisRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Repository cannot be null");
        }

        this.repository = repository;
    }

    public LiveData<Stats> getStats() {
        return stats;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void refreshStats() {
        executor.execute(() -> {
            try {
                // One database record = one type.
                // The quantity field is ignored.
                int coins = repository.getItems(true).size();
                int banknotes = repository.getItems(false).size();

                stats.postValue(new Stats(coins, banknotes));
                error.postValue(null);

            } catch (Exception e) {
                error.postValue(
                        "Could not load collection statistics");
            }
        });
    }

    @Override
    protected void onCleared() {
        executor.shutdown();
        super.onCleared();
    }

    public static final class Stats {

        public final int coinTypes;
        public final int banknoteTypes;

        public Stats(int coins, int banknotes) {
            if (coins < 0 || banknotes < 0) {
                throw new IllegalArgumentException(
                        "Counts cannot be negative");
            }

            this.coinTypes = coins;
            this.banknoteTypes = banknotes;
        }

        public int total() {
            return coinTypes + banknoteTypes;
        }

        public double coinPercent() {
            if (total() == 0) {
                return 0;
            }

            return 100.0 * coinTypes / total();
        }

        public double banknotePercent() {
            if (total() == 0) {
                return 0;
            }

            return 100.0 * banknoteTypes / total();
        }
    }

    public static class Factory
            implements ViewModelProvider.Factory {

        private final NumisRepository repository;

        public Factory(NumisRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(
                @NonNull Class<T> modelClass) {

            if (modelClass.isAssignableFrom(
                    CollectionViewModel.class)) {

                return (T) new CollectionViewModel(repository);
            }

            throw new IllegalArgumentException(
                    "Unknown ViewModel class");
        }
    }
}

package com.example.numistracker.viewmodel.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.numistracker.data.NumisRepository;
import com.example.numistracker.model.CollectionItem;
import com.example.numistracker.viewmodel.ItemsViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ItemsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantRule =
            new InstantTaskExecutorRule();

    private NumisRepository repository;
    private ItemsViewModel viewModel;

    @Before
    public void setUp() {
        repository = mock(NumisRepository.class);
        viewModel = new ItemsViewModel(repository);
    }

    private static <T> T awaitNext(
            LiveData<T> liveData,
            Runnable action
    ) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> result = new AtomicReference<>();

        Observer<T> observer = value -> {
            result.set(value);
            latch.countDown();
        };

        liveData.observeForever(observer);

        try {
            action.run();

            assertTrue(
                    "ViewModel did not emit a result within 5 seconds",
                    latch.await(5, TimeUnit.SECONDS)
            );

            return result.get();

        } finally {
            liveData.removeObserver(observer);
        }
    }

    @Test
    public void loadCoinsReturnsItems() throws Exception {

        CollectionItem coin = new CollectionItem(
                3L,
                "1 EUR (2020)",
                "France",
                2020,
                1,
                "UNC",
                null
        );

        when(repository.getItems(true))
                .thenReturn(Collections.singletonList(coin));

        List<CollectionItem> result = awaitNext(
                viewModel.getItems(),
                () -> viewModel.loadItems(true)
        );

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).id);

        verify(repository).getItems(true);
    }

    @Test
    public void deleteBanknoteRefreshesList() throws Exception {

        when(repository.deleteItem(5L, false))
                .thenReturn(1);

        when(repository.getItems(false))
                .thenReturn(Collections.emptyList());

        Boolean result = awaitNext(
                viewModel.getDeletionResult(),
                () -> viewModel.deleteItem(5L, false)
        );

        assertEquals(Boolean.TRUE, result);

        verify(repository).deleteItem(5L, false);
        verify(repository).getItems(false);
    }

    @Test
    public void failedDeletionDoesNotRefreshList() throws Exception {

        when(repository.deleteItem(8L, true))
                .thenReturn(0);

        Boolean result = awaitNext(
                viewModel.getDeletionResult(),
                () -> viewModel.deleteItem(8L, true)
        );

        assertEquals(Boolean.FALSE, result);

        verify(repository).deleteItem(8L, true);
        verify(repository, never()).getItems(true);
    }

    @Test
    public void loadingExceptionProducesError() throws Exception {

        when(repository.getItems(true))
                .thenThrow(
                        new IllegalStateException("DB error")
                );

        String result = awaitNext(
                viewModel.getError(),
                () -> viewModel.loadItems(true)
        );

        assertEquals("Error loading items", result);
    }
}
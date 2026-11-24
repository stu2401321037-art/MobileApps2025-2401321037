package com.example.numistracker.viewmodel.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.numistracker.data.NumisRepository;
import com.example.numistracker.viewmodel.FormViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class FormViewModelTest {
    @Rule public InstantTaskExecutorRule instantRule = new InstantTaskExecutorRule();
    private NumisRepository repository;
    private FormViewModel viewModel;

    @Before public void setUp() {
        repository = mock(NumisRepository.class);
        viewModel = new FormViewModel(repository);
    }

    private static <T> T awaitNext(LiveData<T> liveData, Runnable action)
            throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> result = new AtomicReference<>();
        Observer<T> observer = value -> {
            result.set(value);
            latch.countDown();
        };
        liveData.observeForever(observer);
        try {
            action.run();
            assertTrue("ViewModel did not emit a result within 5 seconds",
                    latch.await(5, TimeUnit.SECONDS));
            return result.get();
        } finally {
            liveData.removeObserver(observer);
        }
    }

    @Test public void insertCoinSavesRecordAndPhotos() throws Exception {
        when(repository.insertCoin(anyString(), anyString(), anyInt(), anyString(),
                anyString(), anyString(), anyString(), anyDouble(), nullable(Double.class),
                anyInt(), anyString(), anyBoolean())).thenReturn(42L);

        FormViewModel.SaveResult result = awaitNext(viewModel.getSaveResult(), () ->
                viewModel.save(-1, true, "2 EUR (2024)", "Germany", 2024,
                        "2", "EUR", "UNC", 12.0, 1,
                        Collections.singletonList("/photos/a.jpg")));

        assertTrue(result.success);
        assertEquals(42L, result.id);
        verify(repository).savePhotos(42L, true,
                Collections.singletonList("/photos/a.jpg"));
    }

    @Test public void updateBanknoteDoesNotInsertNewRecord() throws Exception {
        when(repository.updateBanknoteForm(eq(7L), anyString(), anyString(), anyInt(),
                anyString(), anyString(), anyString(), nullable(Double.class), anyInt()))
                .thenReturn(1);

        FormViewModel.SaveResult result = awaitNext(viewModel.getSaveResult(), () ->
                viewModel.save(7L, false, "10 BGN (1999)", "Bulgaria", 1999,
                        "10", "BGN", "VF", 25.0, 2, Collections.emptyList()));

        assertTrue(result.success);
        assertEquals(7L, result.id);
        verify(repository, never()).insertBanknote(anyString(), anyString(), anyInt(),
                anyString(), anyString(), anyString(), anyDouble(), nullable(Double.class),
                anyInt(), anyString(), anyBoolean());
        verify(repository).savePhotos(7L, false, Collections.emptyList());
    }

    @Test public void unsuccessfulUpdateDoesNotSavePhotos() throws Exception {
        when(repository.updateCoinForm(eq(99L), anyString(), anyString(), anyInt(),
                anyString(), anyString(), anyString(), nullable(Double.class), anyInt()))
                .thenReturn(0);

        FormViewModel.SaveResult result = awaitNext(viewModel.getSaveResult(), () ->
                viewModel.save(99L, true, "1 EUR (2020)", "France", 2020,
                        "1", "EUR", "F", null, 1,
                        Collections.singletonList("/photos/b.jpg")));

        assertFalse(result.success);
        assertEquals(-1L, result.id);
        verify(repository, never()).savePhotos(anyLong(), anyBoolean(), anyList());
    }

    @Test public void photoErrorReturnsFailureAndKeepsSavedId() throws Exception {
        when(repository.insertBanknote(anyString(), anyString(), anyInt(), anyString(),
                anyString(), anyString(), anyDouble(), nullable(Double.class),
                anyInt(), anyString(), anyBoolean())).thenReturn(15L);
        doThrow(new IllegalStateException("Photo write failed"))
                .when(repository).savePhotos(eq(15L), eq(false), anyList());

        FormViewModel.SaveResult result = awaitNext(viewModel.getSaveResult(), () ->
                viewModel.save(-1, false, "5 EUR (2002)", "Germany", 2002,
                        "5", "EUR", "UNC", null, 1,
                        Collections.singletonList("/photos/c.jpg")));

        assertFalse(result.success);
        assertEquals(15L, result.id);
        assertEquals("Photo write failed", result.error);
    }
}
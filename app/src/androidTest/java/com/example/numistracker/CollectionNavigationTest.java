package com.example.numistracker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CollectionNavigationTest {

    @Test public void coinsListOpensCoinForm() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                ItemsListActivity.class);
        intent.putExtra(ItemsListActivity.EXTRA_TYPE, ItemsListActivity.TYPE_COINS);

        try (ActivityScenario<ItemsListActivity> ignored = ActivityScenario.launch(intent)) {
            onView(withId(R.id.buttonAddItem)).check(matches(isDisplayed()));
            onView(withId(R.id.buttonAddItem)).perform(click());
            onView(withId(R.id.buttonSaveCoin)).check(matches(isDisplayed()));
        }
    }

    @Test public void banknotesListOpensBanknoteForm() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                ItemsListActivity.class);
        intent.putExtra(ItemsListActivity.EXTRA_TYPE, ItemsListActivity.TYPE_BANKNOTES);

        try (ActivityScenario<ItemsListActivity> ignored = ActivityScenario.launch(intent)) {
            onView(withId(R.id.buttonAddItem)).check(matches(isDisplayed()));
            onView(withId(R.id.buttonAddItem)).perform(click());
            onView(withId(R.id.buttonSaveBanknote)).check(matches(isDisplayed()));
        }
    }
}
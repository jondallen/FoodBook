package com.uncgcapstone.android.seniorcapstone.activities;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.uncgcapstone.android.seniorcapstone.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SearchTest {

    @Rule
    public ActivityTestRule<StartActivity> mActivityTestRule = new ActivityTestRule<>(StartActivity.class);

    @Test
    public void searchTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.emailText), isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.emailText), isDisplayed()));
        appCompatEditText2.perform(replaceText("jon@aol.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.passwordText), isDisplayed()));
        appCompatEditText3.perform(replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.passwordText), withText("password"), isDisplayed()));
        appCompatEditText4.perform(pressImeActionButton());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.logInButton), withText("Log in"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction clearableEditText = onView(
                allOf(withId(R.id.searchbar),
                        withParent(withId(R.id.toolbar2)),
                        isDisplayed()));
        clearableEditText.perform(click());

        ViewInteraction clearableEditText2 = onView(
                allOf(withId(R.id.searchbar),
                        withParent(withId(R.id.toolbar2)),
                        isDisplayed()));
        clearableEditText2.perform(replaceText("Chicken"), closeSoftKeyboard());

        ViewInteraction clearableEditText3 = onView(
                allOf(withId(R.id.searchbar), withText("Chicken"),
                        withParent(withId(R.id.toolbar2)),
                        isDisplayed()));
        clearableEditText3.perform(pressImeActionButton());

    }

}

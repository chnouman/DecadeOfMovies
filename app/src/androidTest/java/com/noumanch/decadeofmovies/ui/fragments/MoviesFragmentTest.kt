package com.noumanch.decadeofmovies.ui.fragments

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.noumanch.decadeofmovies.R
import com.noumanch.decadeofmovies.ui.activities.MainActivity
import com.noumanch.decadeofmovies.utils.FakeMovieData
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class MoviesFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    val LIST_ITEM_IN_TEST = 9
    val MOVIE_IN_TEST = FakeMovieData.movies[LIST_ITEM_IN_TEST]


    @Test
    fun test_isListFragmentVisible_onAppLaunch() {
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_selectListItem_isDetailFragmentVisible() {
        // Click list item #LIST_ITEM_IN_TEST
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(LIST_ITEM_IN_TEST, click()))

        // Confirm nav to MovieDetailFragment and display title
        onView(withId(R.id.movieTitleTV)).check(matches(withText(MOVIE_IN_TEST.title)))
    }

    @Test
    fun test_backNavigation_toMovieListFragment() {
        // Click list item #LIST_ITEM_IN_TEST
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(LIST_ITEM_IN_TEST, click()))

        // Confirm nav to MovieDetailFragment and display title
        onView(withId(R.id.movieTitleTV)).check(matches(withText(MOVIE_IN_TEST.title)))

        pressBack()

        // Confirm MoviesFragment in view
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }
}



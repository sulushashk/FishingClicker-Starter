/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.dessertclicker

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import com.example.android.dessertclicker.databinding.ActivityMainBinding
import timber.log.Timber

/** onSaveInstanceState Bundle Keys **/
const val KEY_REVENUE = "revenue_key"
const val KEY_DESSERT_SOLD = "dessert_sold_key"
const val KEY_TIMER_SECONDS = "timer_seconds_key"

class MainActivity : AppCompatActivity() {

    private var revenue = 0
    private var fishSold = 0
    private lateinit var fishTimer : FishTimer;

    // Contains all the views
    private lateinit var binding: ActivityMainBinding

    /** Dessert Data **/

    /**
     * Simple data class that represents a dessert. Includes the resource id integer associated with
     * the image, the price it's sold for, and the startProductionAmount, which determines when
     * the dessert starts to be produced.
     */
    data class Fish(val imageId: Int, val price: Int, val startProductionAmount: Int)

    // Create a list of all d
    // esserts, in order of when they start being produced
    private val allfishes = listOf(
        Fish(R.drawable.fish1, 5, 0),
        Fish(R.drawable.fish2, 10, 5),
        Fish(R.drawable.fish4, 15, 20),
        Fish(R.drawable.fish5, 30, 50),
        Fish(R.drawable.turtle, 40, 100),
        Fish(R.drawable.star, 50, 200),
        Fish(R.drawable.kit, 60, 500)
    )
    private var currentFish = allfishes[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.i("onCreate called")

        // Use Data Binding to get reference to the views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.fishButton.setOnClickListener {
            onFishClicked()
        }

        // Setup dessertTimer, passing in the lifecycle
        fishTimer = FishTimer(this.lifecycle)

        // If there is a savedInstanceState bundle, then you're "restarting" the activity
        // If there isn't a bundle, then it's a "fresh" start
        if (savedInstanceState != null) {
            revenue = savedInstanceState.getInt(KEY_REVENUE, 0)
            fishSold = savedInstanceState.getInt(KEY_DESSERT_SOLD, 0)
            fishTimer.secondsCount =
                savedInstanceState.getInt(KEY_TIMER_SECONDS, 0)
            // Show the next dessert
            showCurrentDessert()
        }

        // Set the TextViews to the right values
        binding.revenue = revenue
        binding.amountSold = fishSold

        // Make sure the correct dessert is showing
        binding.fishButton.setImageResource(currentFish.imageId)
    }

    /**
     * Updates the score when the dessert is clicked. Possibly shows a new dessert.
     */
    private fun onFishClicked() {

        // Update the score
        revenue += 2 * currentFish.price
        fishSold += 2

        binding.revenue = revenue
        binding.amountSold = fishSold

        // Show the next dessert
        showCurrentDessert()
    }

    /**
     * Determine which dessert to show.
     */
    private fun showCurrentDessert() {
        var newFish = allfishes[0]
        for (fish in allfishes) {
            if (fishSold >= fish.startProductionAmount) {
                newFish = fish
            }
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a fish who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }

        // If the new dessert is actually different than the current dessert, update the image
        if (newFish != currentFish) {
            currentFish = newFish
            binding.fishButton.setImageResource(newFish.imageId)
        }
    }

    /**
     * Menu methods
     */
    private fun onShare() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setText(getString(R.string.share_text, fishSold, revenue))
            .setType("text/plain")
            .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareMenuButton -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Called when the user navigates away from the app but might come back
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.i("onSaveInstanceState Called")
        outState.putInt(KEY_REVENUE, revenue)
        outState.putInt(KEY_DESSERT_SOLD, fishSold)
        outState.putInt(KEY_TIMER_SECONDS, fishTimer.secondsCount)
    }

    /** Lifecycle Methods **/
    override fun onStart() {
        super.onStart()

        Timber.i("onStart called")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume Called")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy Called")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.i("onRestart Called")
    }
}

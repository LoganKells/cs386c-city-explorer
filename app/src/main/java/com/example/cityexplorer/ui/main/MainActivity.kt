package com.example.cityexplorer.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cityexplorer.R
import com.example.cityexplorer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        // Location data is stored in this JSON file. You can find it in the app/src/main/assets.
        // The LocationApi.kt is used to interact with the data in this file.
        var readJsonInAssetsDirectory = true // Set to false when not debugging.
        lateinit var locationFileName: String

    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()


    /**
     * Load the location data from the persistent JSON file into the model.
     * Initialize data and save to:
     * - The persistent storage JSON file.
     * - Load the view model from the JSON file.
     * */
    private fun initData() {
        var jsonData = "{\"locations\":[]}"
        if (readJsonInAssetsDirectory) {
            // Read from assets directory, useful for debugging by pre-populating data.
            val assetFileName = "locations.json"
            jsonData = assets.open(assetFileName).bufferedReader().use { it.readText() }
        }

        // Write to app storage
        //  data/data/com.example.cityexplorer/files/locationsAppStorage.json
        locationFileName = "locationsAppStorage.json"
        openFileOutput(locationFileName, MODE_PRIVATE).use {
            it.write(jsonData.toByteArray())
        }

        // NOTE - The viewModel is a lazy load, so we must interact with the model first in
        //  MainActivity before we can use it in the other fragments.
        viewModel.refreshLocationsFromJson()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        setSupportActionBar(activityMainBinding.toolbar)

        // Load the location data from the persistent JSON file into the model.
        initData()

        // We are using a navigation graph to manage the navigation between fragments.
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


}
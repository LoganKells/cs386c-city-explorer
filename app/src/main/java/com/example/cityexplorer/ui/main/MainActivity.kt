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
        var readFromJson = true
        lateinit var locationFile: String
    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private fun initData() {
        if (readFromJson) {
            locationFile = assets.open("locations.json")
                .bufferedReader().use {
                    it.readText()
                }
        }

        // NOTE - The viewModel is a lazy load, so we must interact with the model first in
        //  MainActivity before we can use it in the other fragments.
        viewModel.refreshLocations()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        setSupportActionBar(activityMainBinding.toolbar)

        // TODO: Load the location data from the JSON file into the RecyclerView.
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
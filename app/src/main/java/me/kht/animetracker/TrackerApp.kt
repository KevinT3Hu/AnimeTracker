package me.kht.animetracker

import android.app.Application

class TrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AnimeDataRepository.initInstance(this)
    }
}
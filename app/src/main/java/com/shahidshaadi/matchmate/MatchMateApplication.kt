package com.shahidshaadi.matchmate

import android.app.Application
import com.shahidshaadi.matchmate.utils.NetworkMonitor
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MatchMateApplication : Application() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate() {
        super.onCreate()

        networkMonitor.startObserving()

    }
}
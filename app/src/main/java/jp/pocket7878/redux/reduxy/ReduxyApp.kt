package jp.pocket7878.redux.reduxy

import android.app.Application
import timber.log.Timber

class ReduxyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        setupTimber()
    }

    private fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }
}
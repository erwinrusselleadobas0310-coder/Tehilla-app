package com.example

import android.app.Application
import com.example.di.TehillaContainer

class TehillaApplication : Application() {
    lateinit var container: TehillaContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = TehillaContainer(this)
    }
}

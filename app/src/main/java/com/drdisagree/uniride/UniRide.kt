package com.drdisagree.uniride

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference

@HiltAndroidApp
class UniRide : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        contextReference = WeakReference(applicationContext)
    }

    companion object {
        private var instance: UniRide? = null
        private var contextReference: WeakReference<Context>? = null

        val appContext: Context
            get() {
                if (contextReference == null || contextReference?.get() == null) {
                    contextReference = WeakReference(
                        instance?.applicationContext ?: getInstance().applicationContext
                    )
                }
                return contextReference!!.get()!!
            }

        private fun getInstance(): UniRide {
            if (instance == null) {
                instance = UniRide()
            }
            return instance!!
        }
    }
}
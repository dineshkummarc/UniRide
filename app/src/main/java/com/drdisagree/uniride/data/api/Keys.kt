package com.drdisagree.uniride.data.api

object Keys {

    init {
        System.loadLibrary("native-lib")
    }

    external fun mapsApiKey(): String
    external fun geminiApiKey(): String
}
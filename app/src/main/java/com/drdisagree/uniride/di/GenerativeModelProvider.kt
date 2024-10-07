package com.drdisagree.uniride.di

import com.drdisagree.uniride.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel

object GenerativeModelProvider {
    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY
    )
}
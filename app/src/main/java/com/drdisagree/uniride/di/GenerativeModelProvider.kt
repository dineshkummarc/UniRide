package com.drdisagree.uniride.di

import com.drdisagree.uniride.data.api.Keys
import com.google.ai.client.generativeai.GenerativeModel

object GenerativeModelProvider {
    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = Keys.geminiApiKey()
    )
}
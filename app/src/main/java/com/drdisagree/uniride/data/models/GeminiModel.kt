package com.drdisagree.uniride.data.models

import com.drdisagree.uniride.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel

val generativeModel = GenerativeModel(
    modelName = "gemini-pro",
    apiKey = BuildConfig.GEMINI_API_KEY
)
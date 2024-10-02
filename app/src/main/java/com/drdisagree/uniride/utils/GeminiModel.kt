package com.drdisagree.uniride.utils

import com.drdisagree.uniride.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel

val generativeModel = GenerativeModel(
    modelName = "gemini-pro",
    apiKey = BuildConfig.GEMINI_API_KEY
)
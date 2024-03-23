package com.drdisagree.uniride.ui.screens.register.utils

sealed class RegisterValidation {
    data object Valid : RegisterValidation()
    data class Invalid(val error: String) : RegisterValidation()
}
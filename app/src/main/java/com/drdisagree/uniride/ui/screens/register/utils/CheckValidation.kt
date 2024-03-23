package com.drdisagree.uniride.ui.screens.register.utils

import android.util.Patterns

fun validateEmail(email: String): RegisterValidation {
    return if (email.isEmpty()) {
        RegisterValidation.Invalid("Email cannot be empty")
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        RegisterValidation.Invalid("Invalid email format")
    } else {
        RegisterValidation.Valid
    }
}

fun validatePassword(password: String): RegisterValidation {
    return if (password.isEmpty()) {
        RegisterValidation.Invalid("Password cannot be empty")
    } else if (password.length < 8) {
        RegisterValidation.Invalid("Password must be at least 8 characters")
    } else {
        RegisterValidation.Valid
    }
}
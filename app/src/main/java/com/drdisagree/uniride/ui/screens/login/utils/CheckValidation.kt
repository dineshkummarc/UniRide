package com.drdisagree.uniride.ui.screens.login.utils

import android.util.Patterns

fun validateEmail(email: String): LoginValidation {
    return if (email.isEmpty()) {
        LoginValidation.Invalid("Email cannot be empty")
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        LoginValidation.Invalid("Invalid email format")
    } else {
        LoginValidation.Valid
    }
}

fun validatePassword(password: String): LoginValidation {
    return if (password.isEmpty()) {
        LoginValidation.Invalid("Password cannot be empty")
    } else if (password.length < 8) {
        LoginValidation.Invalid("Password must be at least 8 characters")
    } else {
        LoginValidation.Valid
    }
}
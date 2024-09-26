package com.drdisagree.uniride.ui.screens.driver.login.utils

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

fun validatePhoneNumber(phone: String): LoginValidation {
    return if (phone.isEmpty()) {
        LoginValidation.Invalid("Phone number cannot be empty")
    } else if (phone.length != 11 && phone.length != 14) {
        LoginValidation.Invalid("Invalid phone number")
    } else if (!Patterns.PHONE.matcher(phone).matches()) {
        LoginValidation.Invalid("Invalid phone number format")
    } else {
        LoginValidation.Valid
    }
}
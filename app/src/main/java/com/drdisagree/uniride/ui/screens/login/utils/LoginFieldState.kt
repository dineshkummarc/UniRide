package com.drdisagree.uniride.ui.screens.login.utils

import com.drdisagree.uniride.ui.screens.login.utils.LoginValidation

data class LoginFieldState(
    val email: LoginValidation,
    val password: LoginValidation
)
package com.drdisagree.uniride.ui.screens.student.account

import com.drdisagree.uniride.data.models.Student

data class SignInResult(
    val data: Student?,
    val errorMessage: String?
)

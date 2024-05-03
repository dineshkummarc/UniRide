package com.drdisagree.uniride.ui.screens.driver.register

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockClock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.uniride.R
import com.drdisagree.uniride.ui.components.navigation.MainScreenGraph
import com.drdisagree.uniride.ui.components.transitions.SlideInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.PlantBottomCentered
import com.drdisagree.uniride.ui.components.views.StyledTextField
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.screens.destinations.DocumentVerificationScreenDestination
import com.drdisagree.uniride.ui.theme.Blue
import com.drdisagree.uniride.ui.theme.DarkGray
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@MainScreenGraph
@Destination(style = SlideInOutTransition::class)
@Composable
fun RegisterScreen(
    navigator: DestinationsNavigator
) {
    Container {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            PlantBottomCentered()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaterialTheme.spacing.medium1)
                    .verticalScroll(rememberScrollState())
            ) {
                HeaderSection(navigator = navigator)
                DriverRegisterFields(navigator = navigator)
            }
        }
    }
}

@Composable
private fun HeaderSection(
    navigator: DestinationsNavigator
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 84.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .padding(top = MaterialTheme.spacing.extraSmall1)
                .size(width = 24.dp, height = 24.dp),
            painter = painterResource(id = R.drawable.ic_launcher_icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Black)
        )

        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 20.sp,
            fontWeight = FontWeight(600),
            modifier = Modifier
                .padding(start = MaterialTheme.spacing.small2)
        )
    }

    Text(
        text = "Let's Register",
        fontSize = 36.sp,
        fontWeight = FontWeight(600),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.spacing.large3),
        textAlign = TextAlign.Center
    )

    val haveAccount = "Already have an account? "
    val login = "Log in"

    val annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = DarkGray, fontSize = 18.sp)) {
            pushStringAnnotation(tag = haveAccount, annotation = haveAccount)
            append(haveAccount)
        }
        withStyle(SpanStyle(color = Blue, fontSize = 18.sp, fontWeight = FontWeight.Bold)) {
            pushStringAnnotation(tag = login, annotation = login)
            append(login)
        }
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = login, start = offset, end = offset)
                .firstOrNull()?.let {
                    navigator.navigateUp()
                }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = MaterialTheme.spacing.small2,
                bottom = MaterialTheme.spacing.extraLarge2
            ),
        style = TextStyle(
            textAlign = TextAlign.Center
        )
    )
}

@Composable
private fun DriverRegisterFields(
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var repeatPassword by rememberSaveable { mutableStateOf("") }
    var isRepeatPasswordVisible by remember { mutableStateOf(false) }
    var isCorrectEmail by rememberSaveable { mutableStateOf(true) }
    var isCorrectPassword by rememberSaveable { mutableStateOf(true) }
    var isCorrectRepeatPassword by rememberSaveable { mutableStateOf(true) }
    var isCorrectFullName by rememberSaveable { mutableStateOf(true) }
    var fullNameErrorMessage by rememberSaveable { mutableStateOf("") }
    var emailErrorMessage by rememberSaveable { mutableStateOf("") }
    var passwordErrorMessage by rememberSaveable { mutableStateOf("") }
    var repeatPasswordErrorMessage by rememberSaveable { mutableStateOf("") }

    StyledTextField(
        placeholder = "Full Name",
        modifier = Modifier
            .padding(horizontal = MaterialTheme.spacing.small2),
        onValueChange = {
            fullName = it

            isCorrectFullName = true
        },
        inputText = fullName,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        leadingIcon = Icons.Rounded.Person,
        isError = !isCorrectFullName,
        errorIconOnClick = {
            if (!isCorrectFullName) {
                Toast.makeText(
                    context,
                    fullNameErrorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    StyledTextField(
        placeholder = "Email",
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.medium1
            ),
        onValueChange = {
            email = it

            isCorrectEmail = true
        },
        inputText = email,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        leadingIcon = Icons.Rounded.AlternateEmail,
        isError = !isCorrectEmail,
        errorIconOnClick = {
            if (!isCorrectEmail) {
                Toast.makeText(
                    context,
                    emailErrorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    StyledTextField(
        placeholder = "Password",
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.medium1
            ),
        onValueChange = {
            password = it

            if (it.isEmpty()) {
                isPasswordVisible = false
            }
            isCorrectPassword = true
        },
        inputText = password,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        leadingIcon = Icons.Rounded.Lock,
        trailingIcon = if (password.isNotEmpty()) {
            if (isPasswordVisible) {
                Icons.Rounded.Visibility
            } else {
                Icons.Rounded.VisibilityOff
            }
        } else {
            null
        },
        trailingIconOnClick = {
            isPasswordVisible = !isPasswordVisible
        },
        isError = !isCorrectPassword,
        errorIconOnClick = {
            if (!isCorrectPassword) {
                Toast.makeText(
                    context,
                    passwordErrorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    StyledTextField(
        placeholder = "Repeat Password",
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.medium1
            ),
        onValueChange = {
            repeatPassword = it

            if (it.isEmpty()) {
                isRepeatPasswordVisible = false
            }
            isCorrectRepeatPassword = true
        },
        inputText = repeatPassword,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = if (isRepeatPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        leadingIcon = Icons.Rounded.LockClock,
        trailingIcon = if (repeatPassword.isNotEmpty()) {
            if (isRepeatPasswordVisible) {
                Icons.Rounded.Visibility
            } else {
                Icons.Rounded.VisibilityOff
            }
        } else {
            null
        },
        trailingIconOnClick = {
            isRepeatPasswordVisible = !isRepeatPasswordVisible
        },
        isError = !isCorrectRepeatPassword,
        errorIconOnClick = {
            if (!isCorrectRepeatPassword) {
                Toast.makeText(
                    context,
                    repeatPasswordErrorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    ButtonPrimary(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.medium1,
                bottom = MaterialTheme.spacing.medium1
            )
            .fillMaxWidth(),
        text = "Continue"
    ) {
        // Check if all fields are filled

        if (fullName.isEmpty()) {
            isCorrectFullName = false
            fullNameErrorMessage = "Name cannot be empty"
        }

        if (email.isEmpty()) {
            isCorrectEmail = false
            emailErrorMessage = "Email cannot be empty"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isCorrectEmail = false
            emailErrorMessage = "Invalid email"
        }

        if (password.isEmpty()) {
            isCorrectPassword = false
            passwordErrorMessage = "Password cannot be empty"
        } else if (password.length < 8) {
            isCorrectPassword = false
            passwordErrorMessage = "Password must be at least 8 characters long"
        } else if (password.contains(" ")) {
            isCorrectPassword = false
            passwordErrorMessage = "Password cannot contain spaces"
        }

        if (repeatPassword.isEmpty()) {
            isCorrectRepeatPassword = false
            repeatPasswordErrorMessage = "Password cannot be empty"
        } else if (repeatPassword != password) {
            isCorrectRepeatPassword = false
            repeatPasswordErrorMessage = "Passwords do not match"
        }

        if (!isCorrectFullName || !isCorrectEmail || !isCorrectPassword || !isCorrectRepeatPassword) {
            return@ButtonPrimary
        }

        navigator.navigate(
            DocumentVerificationScreenDestination(
                name = fullName.trim(),
                email = email.trim(),
                password = password
            )
        ) {
            launchSingleTop = true
        }
    }
}
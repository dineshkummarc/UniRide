package com.drdisagree.uniride.ui.screens.driver.login

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.ui.components.navigation.MainScreenGraph
import com.drdisagree.uniride.ui.components.transitions.SlideInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.ButtonSecondary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.DisableBackHandler
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.OtpInputDialog
import com.drdisagree.uniride.ui.components.views.PlantBottomCentered
import com.drdisagree.uniride.ui.components.views.StyledAlertDialog
import com.drdisagree.uniride.ui.components.views.StyledTextField
import com.drdisagree.uniride.ui.components.views.ToggleState
import com.drdisagree.uniride.ui.screens.NavGraphs
import com.drdisagree.uniride.ui.screens.destinations.DriverHomeDestination
import com.drdisagree.uniride.ui.screens.destinations.RegisterScreenDestination
import com.drdisagree.uniride.ui.screens.driver.login.utils.LoginValidation
import com.drdisagree.uniride.ui.screens.driver.login.utils.validateEmail
import com.drdisagree.uniride.ui.theme.DarkBlue
import com.drdisagree.uniride.ui.theme.DarkGray
import com.drdisagree.uniride.ui.theme.spacing
import com.google.firebase.auth.PhoneAuthProvider
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@MainScreenGraph
@Destination(style = SlideInOutTransition::class)
@Composable
fun LoginScreen(
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
                LoginFields(navigator = navigator)
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
            .padding(
                top = 114.dp,
                start = MaterialTheme.spacing.small2
            )
    ) {
        Image(
            modifier = Modifier.size(width = 28.dp, height = 28.dp),
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
        text = "Let's Login",
        fontSize = 36.sp,
        fontWeight = FontWeight(600),
        modifier = Modifier
            .padding(
                top = MaterialTheme.spacing.large3,
                start = MaterialTheme.spacing.small2
            )
    )

    val noAccount = "Don't have an account? "
    val register = "Register"

    val annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = DarkGray, fontSize = 18.sp)) {
            pushStringAnnotation(tag = noAccount, annotation = noAccount)
            append(noAccount)
        }
        withStyle(SpanStyle(color = DarkBlue, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)) {
            pushStringAnnotation(tag = register, annotation = register)
            append(register)
        }
    }

    @Suppress("DEPRECATION")
    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = register, start = offset, end = offset)
                .firstOrNull()?.let {
                    navigator.navigate(
                        RegisterScreenDestination()
                    ) {
                        launchSingleTop = true
                    }
                }
        },
        modifier = Modifier
            .padding(
                top = MaterialTheme.spacing.small2,
                bottom = MaterialTheme.spacing.extraLarge1,
                start = MaterialTheme.spacing.small2
            )
    )
}

@Composable
private fun LoginFields(
    navigator: DestinationsNavigator,
    loginViewModel: DriverLoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var phone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isCorrectPhone by rememberSaveable { mutableStateOf(true) }
    var isCorrectEmail by rememberSaveable { mutableStateOf(true) }
    var isCorrectPassword by rememberSaveable { mutableStateOf(true) }
    var phoneErrorMessage by rememberSaveable { mutableStateOf("") }
    var emailErrorMessage by rememberSaveable { mutableStateOf("") }
    var passwordErrorMessage by rememberSaveable { mutableStateOf("") }

    val states = listOf(
        "Email",
        "Phone"
    )
    var selectedOption by rememberSaveable { mutableStateOf(states[0]) }

    val onSelectionChange: (String) -> Unit = { text ->
        selectedOption = text
    }

    ToggleState(
        modifier = Modifier
            .padding(horizontal = MaterialTheme.spacing.small2),
        states = states,
        selectedOption = selectedOption,
        onSelectionChange = onSelectionChange,
        fullWidth = true
    )

    if (selectedOption == states[0]) {
        StyledTextField(
            placeholder = "Email Address",
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
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
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
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
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
    } else {
        StyledTextField(
            placeholder = "Phone Number",
            modifier = Modifier
                .padding(
                    start = MaterialTheme.spacing.small2,
                    end = MaterialTheme.spacing.small2,
                    top = MaterialTheme.spacing.medium1
                ),
            onValueChange = {
                phone = it

                isCorrectPhone = true
            },
            inputText = phone,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            leadingIcon = Icons.Rounded.Phone,
            isError = !isCorrectPhone,
            errorIconOnClick = {
                if (!isCorrectPhone) {
                    Toast.makeText(
                        context,
                        phoneErrorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    var showLoadingDialog by remember { mutableStateOf(false) }
    var showResendVerificationEmail by remember { mutableStateOf(false) }
    var showOtpDialog by remember { mutableStateOf(false) }
    var sentVerificationId by remember { mutableStateOf("") }

    ButtonPrimary(
        modifier = Modifier
            .padding(
                horizontal = MaterialTheme.spacing.small2,
                vertical = MaterialTheme.spacing.medium1,
            )
            .fillMaxWidth(),
        text = "Login"
    ) {
        // Reset error messages
        if (selectedOption == states[0]) {
            isCorrectEmail = true
            isCorrectPassword = true
        } else {
            isCorrectPhone = true
        }

        if (selectedOption == states[0]) {
            showOtpDialog = false

            loginViewModel.loginWithEmailPassword(
                email = email,
                password = password
            )
        } else {
            showOtpDialog = true

            loginViewModel.loginWithPhoneNumber(
                phone = phone,
                activity = context as Activity,
                onCodeSent = { verificationId, _ ->
                    sentVerificationId = verificationId
                }
            )
        }
    }

    if (selectedOption == states[0]) {
        ForgotPasswordSection(
            focusManager = focusManager
        )
    }

    LaunchedEffect(Unit) {
        loginViewModel.login.collect { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoadingDialog = true
                }

                is Resource.Success -> {
                    showLoadingDialog = false
                    showOtpDialog = false
                    showResendVerificationEmail = false

                    navigator.navigate(
                        DriverHomeDestination()
                    ) {
                        popUpTo(NavGraphs.root.startRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                is Resource.Error -> {
                    showLoadingDialog = false
                    showOtpDialog = false
                    showResendVerificationEmail = false

                    var message = result.message.toString()
                    val messageLower = message.lowercase(Locale.ROOT)

                    if (message.contains("EmailNotVerified")) {
                        showResendVerificationEmail = true
                    } else if (messageLower.contains("email") &&
                        messageLower.contains("password")
                    ) {
                        isCorrectEmail = false
                        isCorrectPassword = false
                        emailErrorMessage = message
                        passwordErrorMessage = message
                    } else {
                        var errorShown = false

                        if (messageLower.contains("email") &&
                            !messageLower.contains("please verify your email")
                        ) {
                            isCorrectEmail = false
                            emailErrorMessage = message
                            errorShown = true
                        }

                        if (messageLower.contains("password") &&
                            !messageLower.contains("credential is incorrect")
                        ) {
                            isCorrectPassword = false
                            passwordErrorMessage = message
                            errorShown = true
                        } else if (messageLower.contains("credential is incorrect")) {
                            message = "Invalid email or password"
                        }

                        if (messageLower.contains("phone number") ||
                            messageLower.contains("credential is incorrect")
                        ) {
                            isCorrectPhone = false
                            phoneErrorMessage = message
                            errorShown = true
                        }

                        if (!errorShown) {
                            Toast.makeText(
                                context,
                                message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                else -> {
                    showLoadingDialog = false
                    showOtpDialog = false
                    showResendVerificationEmail = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loginViewModel.verifyEmail.collect { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoadingDialog = true
                    showResendVerificationEmail = false
                }

                is Resource.Success -> {
                    showLoadingDialog = false
                    showResendVerificationEmail = false

                    Toast.makeText(
                        context,
                        result.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

                is Resource.Error -> {
                    showLoadingDialog = false
                    showResendVerificationEmail = false

                    Toast.makeText(
                        context,
                        result.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {
                    showLoadingDialog = false
                    showResendVerificationEmail = false
                }
            }
        }
    }

    if (showLoadingDialog) {
        LoadingDialog()
    }

    if (showResendVerificationEmail) {
        StyledAlertDialog(
            title = "Email not verified",
            message = "Please check your email for verification link.",
            confirmButtonText = "Close",
            dismissButtonText = "Resend",
            onConfirmButtonClick = {
                showResendVerificationEmail = false
            },
            onDismissButtonClick = {
                showResendVerificationEmail = false
                loginViewModel.resendVerificationMail()
            },
            onDismissRequest = {
                showResendVerificationEmail = false
            }
        )
    }

    DisableBackHandler(isDisabled = showOtpDialog) {
        if (showOtpDialog) {
            OtpInputDialog(
                onSubmit = { otp ->
                    if (sentVerificationId.isNotEmpty() && otp.isNotEmpty()) {
                        showOtpDialog = false
                        loginViewModel.verifyPhoneNumberWithCode(
                            phoneAuthCredential = PhoneAuthProvider.getCredential(
                                sentVerificationId,
                                otp
                            )
                        )
                    }
                },
                onCancel = {
                    showOtpDialog = false
                    showLoadingDialog = false

                    Toast.makeText(
                        context,
                        "Login cancelled",
                        Toast.LENGTH_LONG
                    ).show()
                },
                onDismissRequest = {},
                resendOtp = {
                    loginViewModel.loginWithPhoneNumber(
                        phone = phone,
                        activity = context as Activity,
                        onCodeSent = { verificationId, _ ->
                            sentVerificationId = verificationId
                        }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForgotPasswordSection(
    focusManager: FocusManager,
    driverLoginViewModel: DriverLoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf("") }
    var isCorrectEmail by rememberSaveable { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetOpen by remember { mutableStateOf(false) }

    val forgotPass = "Forgot password? "
    val recover = "Recover now"

    val annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = DarkGray, fontSize = 14.sp)) {
            pushStringAnnotation(tag = forgotPass, annotation = forgotPass)
            append(forgotPass)
        }
        withStyle(SpanStyle(color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)) {
            pushStringAnnotation(tag = recover, annotation = recover)
            append(recover)
        }
    }

    @Suppress("DEPRECATION")
    ClickableText(
        text = annotatedString,
        style = TextStyle(textAlign = TextAlign.Center),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = recover, start = offset, end = offset)
                .firstOrNull()?.let {
                    isSheetOpen = true
                }
        },
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.large3,
                bottom = MaterialTheme.spacing.medium1
            )
            .fillMaxWidth()
    )

    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                isSheetOpen = false
                email = ""
                isCorrectEmail = true
            },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = MaterialTheme.spacing.medium3)
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.spacing.medium3,
                        end = MaterialTheme.spacing.medium3,
                        bottom = MaterialTheme.spacing.medium3
                    )
            ) {
                Text(
                    text = "Reset Password",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "We will send you the password reset link to your email address.",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.medium1)
                )

                StyledTextField(
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.medium1),
                    onValueChange = {
                        email = it
                        isCorrectEmail = true
                    },
                    inputText = email,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            focusManager.clearFocus()
                            isCorrectEmail = validateEmail(email) is LoginValidation.Valid

                            if (isCorrectEmail) {
                                driverLoginViewModel.resetPassword(email)
                            }
                        }
                    ),
                    leadingIcon = Icons.Rounded.AlternateEmail,
                    isError = !isCorrectEmail,
                    errorIconOnClick = {
                        if (!isCorrectEmail) {
                            Toast.makeText(
                                context,
                                "Invalid email address",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    placeholder = "Email address"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.medium3)
                ) {
                    ButtonSecondary(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = MaterialTheme.spacing.small2),
                        text = "Cancel",
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                delay(100)
                                isSheetOpen = false
                                email = ""
                                isCorrectEmail = true
                            }
                        }
                    )

                    ButtonPrimary(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = MaterialTheme.spacing.small2),
                        text = "Send",
                        onClick = {
                            isCorrectEmail = validateEmail(email) is LoginValidation.Valid

                            if (isCorrectEmail) {
                                driverLoginViewModel.resetPassword(email)
                            }
                        }
                    )
                }
            }
        }

        LaunchedEffect(key1 = driverLoginViewModel) {
            driverLoginViewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {
                        Unit
                    }

                    is Resource.Success -> {
                        Toast.makeText(
                            context,
                            it.data.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            context,
                            it.data.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }
    }
}
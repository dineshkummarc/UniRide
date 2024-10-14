package com.drdisagree.uniride.ui.screens.admin.more.panel.new_location

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.LatLngSerializable
import com.drdisagree.uniride.data.models.Place
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.StyledTextField
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.viewmodels.AccountStatusViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@MoreNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun NewLocation(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(R.string.new_place),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                NewLocationContent(
                    paddingValues = paddingValues,
                    navigator = navigator
                )
            }
        )
    }
}

@Composable
private fun NewLocationContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator,
    accountStatusViewModel: AccountStatusViewModel = hiltViewModel()
) {
    val isAdminState by accountStatusViewModel.isAdmin.collectAsState()

    when (isAdminState) {
        null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(Color.White)
                        .wrapContentSize()
                )
            }
        }

        true -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(MaterialTheme.spacing.medium1)
            ) {
                NewLocationFields()
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.you_are_not_an_admin)
                )
            }
        }
    }
}

@Composable
private fun NewLocationFields(
    newLocationViewModel: NewLocationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var placeName by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    StyledTextField(
        placeholder = stringResource(R.string.place_name),
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.small2,
            end = MaterialTheme.spacing.small2
        ),
        onValueChange = { placeName = it },
        inputText = placeName,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = false
    )

    StyledTextField(
        placeholder = stringResource(R.string.latitude),
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.small2,
            end = MaterialTheme.spacing.small2,
            top = MaterialTheme.spacing.medium1
        ),
        onValueChange = { latitude = it },
        inputText = latitude,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )

    StyledTextField(
        placeholder = stringResource(R.string.longitude),
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.small2,
            end = MaterialTheme.spacing.small2,
            top = MaterialTheme.spacing.medium1
        ),
        onValueChange = { longitude = it },
        inputText = longitude,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
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
        text = stringResource(R.string.submit)
    ) {
        if (placeName.isEmpty() ||
            latitude.isEmpty() ||
            longitude.isEmpty()
        ) {
            Toast.makeText(
                context,
                "Please fill all the fields",
                Toast.LENGTH_SHORT
            ).show()

            return@ButtonPrimary
        } else if (
            latitude.toDoubleOrNull() == null ||
            longitude.toDoubleOrNull() == null ||
            latitude.toDouble() == 0.0 ||
            longitude.toDouble() == 0.0 ||
            latitude.toDouble() > 90.0 ||
            latitude.toDouble() < -90.0 ||
            longitude.toDouble() > 180.0 ||
            longitude.toDouble() < -180.0
        ) {
            Toast.makeText(
                context,
                "Please enter valid latitude and longitude",
                Toast.LENGTH_SHORT
            ).show()

            return@ButtonPrimary
        }

        newLocationViewModel.saveLocation(
            Place(
                name = placeName,
                latlng = LatLngSerializable(
                    latitude.toDouble(),
                    longitude.toDouble()
                )
            )
        )
    }

    var showLoadingDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        newLocationViewModel.state.collect { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoadingDialog = true
                }

                is Resource.Success -> {
                    showLoadingDialog = false

                    placeName = ""
                    latitude = ""
                    longitude = ""

                    Toast.makeText(
                        context,
                        result.data,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Error -> {
                    showLoadingDialog = false

                    Toast.makeText(
                        context,
                        result.message,
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {
                    showLoadingDialog = false
                }
            }
        }
    }

    if (showLoadingDialog) {
        LoadingDialog()
    }
}
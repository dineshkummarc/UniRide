package com.drdisagree.uniride.ui.screens.driver.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.data.utils.Constant.DRIVER_PROFILE_PICTURE
import com.drdisagree.uniride.data.utils.Prefs
import com.drdisagree.uniride.ui.components.navigation.RoutesNavGraph
import com.drdisagree.uniride.ui.components.transitions.SlideInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.StyledTextField
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.theme.DarkBlue
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.viewmodels.GetDriverViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers

@RoutesNavGraph
@Destination(style = SlideInOutTransition::class)
@Composable
fun EditProfileScreen(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(R.string.edit_profile),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                EditProfileContent(
                    navigator = navigator,
                    paddingValues = paddingValues
                )
            }
        )
    }
}

@Composable
private fun EditProfileContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(vertical = MaterialTheme.spacing.medium1)
    ) {
        EditProfileFields()
    }
}

@Composable
private fun EditProfileFields(
    editProfileViewModel: EditProfileViewModel = hiltViewModel(),
    getDriverViewModel: GetDriverViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var driver: Driver? by remember { mutableStateOf(null) }

    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var image by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    LaunchedEffect(getDriverViewModel.getDriver) {
        getDriverViewModel.getDriver.collect { result ->
            when (result) {
                is Resource.Success -> {
                    driver = result.data

                    name = driver?.name ?: ""
                    phone = driver?.contactPhone ?: ""
                    email = driver?.contactEmail ?: ""
                    image = driver?.profileImage ?: ""

                    Prefs.putString(DRIVER_PROFILE_PICTURE, image)
                }

                is Resource.Error -> {
                    Toast.makeText(
                        context,
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    Unit
                }
            }
        }
    }

    val placeholder by remember { mutableIntStateOf(R.drawable.img_profile_pic_default) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                imageUri = it
            }
        } else {
            if (result.resultCode != Activity.RESULT_CANCELED) {
                Toast.makeText(
                    context,
                    result.data?.data.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                imageUri = null
            }
        }
    }

    val imageRequest = ImageRequest.Builder(context)
        .data(image)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(image + "_low")
        .diskCacheKey(image + "_low")
        .placeholder(placeholder)
        .error(placeholder)
        .fallback(placeholder)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .crossfade(true)
        .crossfade(250)
        .size(256)
        .build()

    Box(
        modifier = Modifier
            .padding(
                horizontal = MaterialTheme.spacing.medium1,
                vertical = MaterialTheme.spacing.small2
            )
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.medium1)
                .size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(100))
                    .background(Gray)
                    .padding(MaterialTheme.spacing.small2)
                    .align(Alignment.Center)
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest
                                .Builder(context)
                                .data(imageUri)
                                .size(Size.ORIGINAL)
                                .build()
                        ),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(100)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = imageRequest,
                        placeholder = painterResource(id = R.drawable.img_loading),
                        contentDescription = stringResource(R.string.profile_picture),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(100)),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            IconButton(
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(DarkBlue)
                    .size(36.dp)
                    .align(Alignment.BottomEnd),
                onClick = {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    intent.type = "image/*"
                    imagePickerLauncher.launch(intent)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.change_profile_picture),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            MaterialTheme.spacing.small2
                        ),
                    tint = Color.White
                )
            }
        }
    }

    StyledTextField(
        placeholder = stringResource(R.string.full_name),
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.medium1,
            end = MaterialTheme.spacing.medium1,
            top = MaterialTheme.spacing.medium1
        ),
        onValueChange = { name = it },
        inputText = name,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )

    StyledTextField(
        placeholder = stringResource(R.string.phone_number),
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.medium1,
            end = MaterialTheme.spacing.medium1,
            top = MaterialTheme.spacing.medium1
        ),
        onValueChange = { phone = it },
        inputText = phone,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )

    StyledTextField(
        placeholder = stringResource(R.string.email_address),
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.medium1,
            end = MaterialTheme.spacing.medium1,
            top = MaterialTheme.spacing.medium1
        ),
        onValueChange = { email = it },
        inputText = email,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )

    ButtonPrimary(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.medium1,
                end = MaterialTheme.spacing.medium1,
                top = MaterialTheme.spacing.medium1,
                bottom = MaterialTheme.spacing.medium1
            )
            .fillMaxWidth(),
        text = stringResource(R.string.update_profile)
    ) {
        if (name.isEmpty()) {
            Toast.makeText(
                context,
                "Name cannot be empty",
                Toast.LENGTH_SHORT
            ).show()

            return@ButtonPrimary
        }

        if (driver != null) {
            editProfileViewModel.updateProfile(
                driver = driver!!.copy(
                    name = name,
                    contactPhone = phone,
                    contactEmail = email
                ),
                image = imageUri to DRIVER_PROFILE_PICTURE
            )
        }
    }

    var showLoadingDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        editProfileViewModel.update.collect { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoadingDialog = true
                }

                is Resource.Success -> {
                    showLoadingDialog = false

                    Toast.makeText(
                        context,
                        "Profile updated successfully",
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

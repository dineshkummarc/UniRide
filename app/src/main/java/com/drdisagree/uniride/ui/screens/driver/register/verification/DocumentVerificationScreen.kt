package com.drdisagree.uniride.ui.screens.driver.register.verification

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.drdisagree.uniride.R
import com.drdisagree.uniride.ui.components.transitions.SlideInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.PlantBottomCentered
import com.drdisagree.uniride.ui.extension.Container
import com.drdisagree.uniride.ui.screens.driver.register.RegisterViewModel
import com.drdisagree.uniride.ui.theme.DarkGray
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(style = SlideInOutTransition::class)
@Composable
fun DocumentVerificationScreen(
    navigator: DestinationsNavigator,
    name: String,
    email: String,
    password: String,
    registerViewModel: RegisterViewModel = hiltViewModel()
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
                    .verticalScroll(rememberScrollState())
            ) {
                HeaderSection()
                VerificationContent()
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.spacing.extraLarge1),
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
        text = "Verification Required",
        fontSize = 28.sp,
        fontWeight = FontWeight(600),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.spacing.medium2),
        textAlign = TextAlign.Center
    )

    Text(
        text = "We need the following documents",
        color = DarkGray,
        fontSize = 17.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = MaterialTheme.spacing.large2,
                start = MaterialTheme.spacing.small2
            ),
        style = TextStyle(
            textAlign = TextAlign.Center
        )
    )
}

@Composable
private fun VerificationContent() {
    val context = LocalContext.current as Activity

    Row(
        modifier = Modifier
            .padding(bottom = MaterialTheme.spacing.small2)
            .fillMaxWidth()
    ) {
        Text(
            text = "Front Side of\nNID Card",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = MaterialTheme.spacing.small2,
                    end = MaterialTheme.spacing.small2
                ),
            style = TextStyle(
                textAlign = TextAlign.Center
            )
        )

        Text(
            text = "Back Side of\nNID Card",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = MaterialTheme.spacing.small2,
                    end = MaterialTheme.spacing.small2
                ),
            style = TextStyle(
                textAlign = TextAlign.Center
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        var nidFrontImageUri by remember { mutableStateOf<Uri?>(null) }
        val nidFrontImagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    nidFrontImageUri = it
                }
            } else {
                if (result.resultCode != Activity.RESULT_CANCELED) {
                    Toast.makeText(
                        context,
                        result.data?.data.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    nidFrontImageUri = null
                }
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1.8f, true)
                .padding(
                    start = 16.dp,
                    end = 8.dp
                )
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    intent.type = "image/*"
                    nidFrontImagePickerLauncher.launch(intent)
                },
            colors = CardDefaults.cardColors(
                containerColor = LightGray
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (nidFrontImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest
                                .Builder(context)
                                .data(nidFrontImageUri)
                                .size(Size.ORIGINAL)
                                .build()
                        ),
                        contentDescription = "Photo of front side of NID card",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_image),
                        contentDescription = "Capture photo of front side of NID card",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }

        var nidBackImageUri by remember { mutableStateOf<Uri?>(null) }
        val nidBackImagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    nidBackImageUri = it
                }
            } else {
                if (result.resultCode != Activity.RESULT_CANCELED) {
                    Toast.makeText(
                        context,
                        result.data?.data.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    nidBackImageUri = null
                }
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1.8f, true)
                .padding(
                    start = 8.dp,
                    end = 16.dp
                )
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    intent.type = "image/*"
                    nidBackImagePickerLauncher.launch(intent)
                },
            colors = CardDefaults.cardColors(
                containerColor = LightGray
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (nidBackImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest
                                .Builder(context)
                                .data(nidBackImageUri)
                                .size(Size.ORIGINAL)
                                .build()
                        ),
                        contentDescription = "Photo of back side of NID card",
                        modifier = Modifier.weight(1f),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_image),
                        contentDescription = "Capture photo of back side of NID card",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .padding(
                top = MaterialTheme.spacing.medium3,
                bottom = MaterialTheme.spacing.small2
            )
            .fillMaxWidth()
    ) {
        Text(
            text = "Front Side of\nDriving License",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = MaterialTheme.spacing.small2,
                    end = MaterialTheme.spacing.small2
                ),
            style = TextStyle(
                textAlign = TextAlign.Center
            )
        )

        Text(
            text = "Back Side of\nDriving License",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = MaterialTheme.spacing.small2,
                    end = MaterialTheme.spacing.small2
                ),
            style = TextStyle(
                textAlign = TextAlign.Center
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        var drivingLicenseFrontImageUri by remember { mutableStateOf<Uri?>(null) }
        val drivingLicenseFrontImagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    drivingLicenseFrontImageUri = it
                }
            } else {
                if (result.resultCode != Activity.RESULT_CANCELED) {
                    Toast.makeText(
                        context,
                        result.data?.data.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    drivingLicenseFrontImageUri = null
                }
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(0.9f, true)
                .padding(
                    start = 16.dp,
                    end = 8.dp
                )
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    intent.type = "image/*"
                    drivingLicenseFrontImagePickerLauncher.launch(intent)
                },
            colors = CardDefaults.cardColors(
                containerColor = LightGray
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (drivingLicenseFrontImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest
                                .Builder(context)
                                .data(drivingLicenseFrontImageUri)
                                .size(Size.ORIGINAL)
                                .build()
                        ),
                        contentDescription = "Photo of front side of driving license",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_image),
                        contentDescription = "Capture photo of front side of driving license",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }

        var drivingLicenseBackImageUri by remember { mutableStateOf<Uri?>(null) }
        val drivingLicenseBackImagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    drivingLicenseBackImageUri = it
                }
            } else {
                if (result.resultCode != Activity.RESULT_CANCELED) {
                    Toast.makeText(
                        context,
                        result.data?.data.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    drivingLicenseBackImageUri = null
                }
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(0.9f, true)
                .padding(
                    start = 8.dp,
                    end = 16.dp
                )
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    intent.type = "image/*"
                    drivingLicenseBackImagePickerLauncher.launch(intent)
                },
            colors = CardDefaults.cardColors(
                containerColor = LightGray
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (drivingLicenseBackImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest
                                .Builder(context)
                                .data(drivingLicenseBackImageUri)
                                .size(Size.ORIGINAL)
                                .build()
                        ),
                        contentDescription = "Photo of back side of driving license",
                        modifier = Modifier.weight(1f),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_image),
                        contentDescription = "Capture photo of back side of driving license",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

    ButtonPrimary(
        modifier = Modifier
            .padding(
                top = MaterialTheme.spacing.medium3,
                start = MaterialTheme.spacing.medium1,
                end = MaterialTheme.spacing.medium1,
                bottom = MaterialTheme.spacing.medium1,
            )
            .fillMaxWidth(),
        text = "Register"
    ) {
        /* TODO */
    }
}
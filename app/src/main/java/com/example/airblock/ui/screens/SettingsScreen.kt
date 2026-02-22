package com.example.airblock.ui.screens

import android.R.attr.navigationIcon
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airblock.R
import com.example.airblock.data.TagStorage
import com.example.airblock.state.AirBlockState

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    context: Context
) {
    val bgDark = colorResource(id = R.color.dark_background)
    val uriHandler = LocalUriHandler.current
    var showResetDialog by remember { mutableStateOf(false) }

    // --- Confirmation Dialog ---
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Unlink NFC Tag?") },
            text = { Text("This will delete your currently registered physical key. You will need to scan a new one to use Focus Mode.") },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        resetNfcTag(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Unlink")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = bgDark)
            .padding(top = 16.dp)
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 8.dp)
        ) {
            Text(
                text = "< AIRBLOCK",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        // --- Option 1: Reset NFC Tag (Destructive Action) ---
        SettingsRow(
            title = "Unlink NFC Tag",
            subtitle = "Remove the current security key",
            icon = Icons.Default.DeleteForever,
            iconTint = MaterialTheme.colorScheme.error,
            textColor = MaterialTheme.colorScheme.error,
            onClick = { showResetDialog = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- Option 2: GitHub Repository ---
        SettingsRow(
            title = "Open Source Repository",
            subtitle = "View the code on GitHub",
            icon = Icons.AutoMirrored.Filled.OpenInNew,
            iconTint = Color.Gray,
            textColor = Color.White, // Or white depending on your text theme
            onClick = {
                uriHandler.openUri("https://github.com/Tomiboard/AirBlock")
            }
        )
    }
}

// 📦 Reusable UI Component for Settings Rows
@Composable
fun SettingsRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(onBack = {}, context = LocalContext.current)
}



fun resetNfcTag(context: Context) {

    AirBlockState.registeredTagId = null
    AirBlockState.hasTagRegistered = false
    AirBlockState.isLocked = false
    TagStorage.clearTag(context)
    Toast.makeText(context, context.getString(R.string.tag_eliminated), Toast.LENGTH_SHORT).show()
}

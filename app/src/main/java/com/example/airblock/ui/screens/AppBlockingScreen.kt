package com.tu.usuario.airblock.ui.screens

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.airblock.R
import com.example.airblock.data.TagStorage
import com.example.airblock.state.AirBlockState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Modelo de datos
data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBlockingScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // --- TUS COLORES DEL XML ---
    val bgDark = colorResource(id = R.color.dark_background)
    val accentRed = colorResource(id = R.color.unlock_red)
    val textGray = colorResource(id = R.color.text_gray)
    val faintWhite = colorResource(id = R.color.faint_white)

    // Estado
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            installedApps = getInstalledApps(context)
            isLoading = false
        }
    }

    Scaffold(
        containerColor = bgDark, // Fondo #121212
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "TARGET_SELECTOR //", // Estilo Terminal
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    // Botón de texto estilo hacker
                    TextButton(onClick = onBack) {
                        Text(
                            "< BACK",
                            color = accentRed,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgDark,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                // Spinner Rojo
                CircularProgressIndicator(color = accentRed)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(installedApps) { app ->
                    CyberAppItemRow(
                        app = app,
                        accentColor = accentRed,
                        textColor = textGray
                    )

                    // Línea divisoria muy fina (faint_white)
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = faintWhite
                    )
                }
            }
        }
    }
}

@Composable
fun CyberAppItemRow(
    app: AppInfo,
    accentColor: Color,
    textColor: Color
) {
    val context = LocalContext.current
    val isBlocked = AirBlockState.blockedApps.contains(app.packageName)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                toggleAppBlock(context, app.packageName, !isBlocked)
            }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Icono
        if (app.icon != null) {
            Image(
                bitmap = app.icon.toBitmap().asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(42.dp)
            )
        } else {
            // Placeholder si no hay icono
            Box(modifier = Modifier.size(42.dp).background(Color.DarkGray))
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 2. Textos (Estilo Consola)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.name,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White, // Nombre en blanco brillante
                    fontSize = 16.sp
                )
            )
            Text(
                text = app.packageName,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    color = textColor, // Package name en gris (text_gray)
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
        }

        // 3. Checkbox Personalizado
        Checkbox(
            checked = isBlocked,
            onCheckedChange = { isChecked ->
                toggleAppBlock(context, app.packageName, isChecked)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = accentColor,      // Rojo (unlock_red) al marcar
                checkmarkColor = Color.Black,    // Tick negro para contraste
                uncheckedColor = textColor,      // Borde gris al estar desmarcado
                disabledCheckedColor = accentColor.copy(alpha = 0.6f)
            )
        )
    }
}

// Lógica auxiliar para guardar
fun toggleAppBlock(context: Context, packageName: String, shouldBlock: Boolean) {
    if (shouldBlock) {
        AirBlockState.blockedApps.add(packageName)
    } else {
        AirBlockState.blockedApps.remove(packageName)
    }
    TagStorage.saveBlockedApps(context, AirBlockState.blockedApps.toSet())
}

// Lógica de carga de apps
fun getInstalledApps(context: Context): List<AppInfo> {
    val pm = context.packageManager
    val apps = pm.getInstalledPackages(0)

    val myPackageName = context.packageName

    val filteredApps = mutableListOf<AppInfo>()

    for (app in apps) {
        if (pm.getLaunchIntentForPackage(app.packageName) != null
            && app.packageName != myPackageName) {
            val name = app.applicationInfo?.loadLabel(pm).toString()
            val icon = app.applicationInfo?.loadIcon(pm)

            if (name.isNotEmpty()) {
                filteredApps.add(AppInfo(name, app.packageName, icon))
            }
        }
    }
    return filteredApps.sortedBy { it.name.lowercase() }
}
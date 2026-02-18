package com.example.airblock.ui.screens

import android.R.attr.duration
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
// IMPORTS PARA GESTIONAR PANTALLA COMPLETA
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
// ------------------------------------------
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.airblock.R
import com.example.airblock.data.TagStorage
import com.example.airblock.state.AirBlockState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.*



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
    val focusManager = LocalFocusManager.current

    // --- TUS COLORES DEL XML ---
    val bgDark = colorResource(id = R.color.dark_background)
    val accentRed = colorResource(id = R.color.unlock_red)
    val textGray = colorResource(id = R.color.text_gray)
    val faintWhite = colorResource(id = R.color.faint_white)

    // Estados
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        val t0 = System.currentTimeMillis() // ⏱️ Tiempo inicial

        withContext(Dispatchers.IO) {
            val apps = getInstalledApps(context)
            val t1 = System.currentTimeMillis() // ⏱️ Tiempo final

            val totalMilis = t1 - t0 // 👈 LA RESTA ES LA CLAVE
            android.util.Log.d("AIRBLOCK_PERF", "TIEMPO REAL DE CARGA: $totalMilis ms")

            installedApps = apps
            isLoading = false
        }
    }

    // --- LÓGICA DE FILTRADO Y SELECCIÓN ---
    val filteredApps = if (searchQuery.isEmpty()) {
        installedApps
    } else {
        installedApps.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    val allSelected = filteredApps.isNotEmpty() && filteredApps.all {
        AirBlockState.blockedApps.contains(it.packageName)
    }

    // 👇 AQUÍ ESTÁ EL CAMBIO: SURFACE EN LUGAR DE SCAFFOLD
    // Surface ocupa toda la pantalla y la pinta de negro, tapando lo de atrás.

        Column(modifier = Modifier.fillMaxSize()
            .background(color = bgDark),
            ) {

            // 1. ESPACIO PARA LA BARRA DE ESTADO (HORA/BATERÍA)
            // Esto evita que el título se meta debajo de la cámara
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // 2. CABECERA (NO SE MUEVE)
            Column(modifier = Modifier.background(bgDark)) {
                // A. TÍTULO
                TopAppBar(
                    title = {
                        Text(
                            "TARGET_SELECTOR //",
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

                // B. BUSCADOR
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = {
                        Text(
                            "Search module...",
                            color = textGray,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = textGray)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Clear",
                                    tint = textGray
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentRed,
                        unfocusedBorderColor = textGray,
                        cursorColor = accentRed,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )

                // C. SELECT ALL
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            toggleAll(context, filteredApps, !allSelected)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (allSelected) "[ DESELECT ALL ]" else "[ SELECT ALL ]",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (allSelected) accentRed else textGray
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Checkbox(
                        checked = allSelected,
                        onCheckedChange = { isChecked ->
                            toggleAll(context, filteredApps, isChecked)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = accentRed,
                            checkmarkColor = Color.Black,
                            uncheckedColor = textGray
                        )
                    )
                }

                HorizontalDivider(color = accentRed, thickness = 1.dp)
            }

            // 3. CONTENIDO (LA LISTA)
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = accentRed)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f), // Ocupa todo el espacio restante
                    // Calculamos el espacio inferior para que la lista pase POR DEBAJO de los botones
                    // pero el último elemento tenga margen para leerse.
                    contentPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 80.dp
                    )
                ) {
                    items(filteredApps) { app ->
                        CyberAppItemRow(
                            app = app,
                            accentColor = accentRed,
                            textColor = textGray
                        )

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
        if (app.icon != null) {
            Image(
                bitmap = app.icon.toBitmap().asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(42.dp)
            )
        } else {
            Box(modifier = Modifier
                .size(42.dp)
                .background(Color.DarkGray))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.name,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            )
            Text(
                text = app.packageName,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    color = textColor,
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
        }

        Checkbox(
            checked = isBlocked,
            onCheckedChange = { isChecked ->
                toggleAppBlock(context, app.packageName, isChecked)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = accentColor,
                checkmarkColor = Color.Black,
                uncheckedColor = textColor,
                disabledCheckedColor = accentColor.copy(alpha = 0.6f)
            )
        )
    }
}

// --- FUNCIONES AUXILIARES ---

fun toggleAppBlock(context: Context, packageName: String, shouldBlock: Boolean) {
    if (shouldBlock) {
        AirBlockState.blockedApps.add(packageName)
    } else {
        AirBlockState.blockedApps.remove(packageName)
    }
    TagStorage.saveBlockedApps(context, AirBlockState.blockedApps.toSet())
}

fun toggleAll(context: Context, apps: List<AppInfo>, shouldSelect: Boolean) {
    val myPackage = context.packageName
    val visiblePackages = apps.map { it.packageName }
        .filter { it != myPackage }

    if (shouldSelect) {
        AirBlockState.blockedApps.addAll(visiblePackages)
    } else {
        AirBlockState.blockedApps.removeAll(visiblePackages.toSet())
    }

    val unique = AirBlockState.blockedApps.toSet()
    AirBlockState.blockedApps.clear()
    AirBlockState.blockedApps.addAll(unique)
    TagStorage.saveBlockedApps(context, unique)
}


suspend fun getInstalledApps(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
    val pm = context.packageManager
    val apps = pm.getInstalledPackages(0)
    val myPackageName = context.packageName
    val launcher = getDefaultLauncherPackageName(context)

    val essentialApps = setOf(
        myPackageName,
        launcher,
        "com.android.settings",
        "com.android.systemui",
        "com.google.android.googlequicksearchbox"
    )

    // 1. Filtramos primero la lista para no procesar apps que no vamos a mostrar
    val validPackages = apps.filter { app ->
        val pkg = app.packageName
        pm.getLaunchIntentForPackage(pkg) != null && !essentialApps.contains(pkg)
    }

    // 2. Procesamos en paralelo usando 'async'
    // Dividimos la carga de trabajo: cada app se procesa en un hilo del pool IO
    val deferredApps = validPackages.map { app ->
        async {
            try {
                val name = app.applicationInfo?.loadLabel(pm).toString()
                val icon = app.applicationInfo?.loadIcon(pm)

                if (name.isNotEmpty()) {
                    AppInfo(name, app.packageName, icon)
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }

    // 3. Esperamos a que todas las tareas paralelas terminen y ordenamos
    return@withContext deferredApps.awaitAll()
        .filterNotNull()
        .sortedBy { it.name.lowercase() }
}

@Preview
@Composable
fun preview(){
    AppBlockingScreen(onBack = {})
}

// Función inspirada en Mindful para detectar el Launcher de cualquier marca
fun getDefaultLauncherPackageName(context: Context): String? {
    return context.packageManager.resolveActivity(
        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
        PackageManager.MATCH_DEFAULT_ONLY
    )?.activityInfo?.packageName
}
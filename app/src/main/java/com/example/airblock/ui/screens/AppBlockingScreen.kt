package com.tu.usuario.airblock.ui.screens

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.airblock.data.TagStorage
import com.example.airblock.state.AirBlockState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Modelo de datos simple para la lista
data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBlockingScreen(
    onBack: () -> Unit // Función para volver atrás cuando terminemos
) {
    val context = LocalContext.current

    // Lista de todas las apps instaladas (se carga asíncronamente)
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar apps en segundo plano para no congelar la pantalla
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            installedApps = getInstalledApps(context)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            // Título y botón de guardar/atrás
            TopAppBar(
                title = { Text("Select Apps to Block") },
                navigationIcon = {
                    Button(onClick = onBack) {
                        Text("Done")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // LA LISTA DE APPS
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(installedApps) { app ->
                    AppItemRow(app = app)
                }
            }
        }
    }
}

@Composable
fun AppItemRow(app: AppInfo) {
    val context = LocalContext.current

    // Verificamos si esta app está actualmente en la lista de bloqueadas
    // Usamos el estado global para que se actualice al momento
    val isBlocked = AirBlockState.blockedApps.contains(app.packageName)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Icono de la App
        Image(
            bitmap = app.icon!!.toBitmap().asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 2. Nombre de la App
        Text(
            text = app.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )

        // 3. Checkbox de Selección
        Checkbox(
            checked = isBlocked,
            onCheckedChange = { isChecked ->
                // Lógica de añadir/quitar
                if (isChecked) {
                    AirBlockState.blockedApps.add(app.packageName)
                } else {
                    AirBlockState.blockedApps.remove(app.packageName)
                }

                // GUARDAR EN DISCO INMEDIATAMENTE
                // Convertimos la lista a un Set para guardarla
                TagStorage.saveBlockedApps(context, AirBlockState.blockedApps.toSet())
            }
        )
    }
}

/**
 * Función mágica para obtener las apps.
 * Filtra las apps del sistema para mostrar solo las que el usuario puede abrir.
 */
fun getInstalledApps(context: Context): List<AppInfo> {
    val pm = context.packageManager
    val apps = pm.getInstalledPackages(0)

    val filteredApps = mutableListOf<AppInfo>()

    for (app in apps) {
        // Filtro 1: Que tenga nombre
        // Filtro 2: Que tenga "Intent de lanzamiento" (significa que se puede abrir desde el menú)
        // Esto elimina servicios ocultos de Android
        if (pm.getLaunchIntentForPackage(app.packageName) != null) {
            val name = app.applicationInfo?.loadLabel(pm).toString()
            val icon = app.applicationInfo?.loadIcon(pm)

            filteredApps.add(AppInfo(name, app.packageName, icon))
        }
    }

    // Ordenar alfabéticamente
    return filteredApps.sortedBy { it.name }
}



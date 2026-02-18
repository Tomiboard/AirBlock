package com.example.airblock.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.accessibility.AccessibilityEvent
import com.example.airblock.MainActivity
import com.example.airblock.state.AirBlockState

class AppBlockService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            // 1. Detección dinámica del Launcher (Universal)
            val defaultLauncher = getDefaultLauncher()

            // 2. Lista Blanca Híbrida
            // Aquí combinamos nombres fijos universales + detecciones dinámicas
            val isWhitelisted = packageName == this.packageName ||
                    packageName == defaultLauncher ||
                    packageName == "com.android.systemui" ||
                    packageName == "com.android.settings" ||
                    packageName == "android" ||
                    packageName.contains("googlequicksearchbox") ||
                    // 🛡️ Filtros de seguridad por fabricante (Cubre el 99% de los móviles)
                    packageName.startsWith("com.samsung.android.") ||
                    packageName.startsWith("com.miui.") ||
                    packageName.startsWith("com.huawei.") ||
                    packageName.contains(".launcher", ignoreCase = true)

            if (isWhitelisted) {
                android.util.Log.d("AIRBLOCK_DEBUG", "Permitido por seguridad: $packageName")
                return
            }

            // 3. Lógica de Bloqueo
            if (AirBlockState.isLocked && AirBlockState.blockedApps.contains(packageName)) {
                android.util.Log.d("AIRBLOCK_DEBUG", "🛑 BLOQUEANDO: $packageName")

                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
            }
        }
    }

    private fun getDefaultLauncher(): String? {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName
    }
    override fun onInterrupt() {

    }
}
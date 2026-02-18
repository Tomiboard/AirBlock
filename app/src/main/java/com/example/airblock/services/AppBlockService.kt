package com.example.airblock.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.content.pm.PackageManager
import android.view.accessibility.AccessibilityEvent
import com.example.airblock.MainActivity
import com.example.airblock.state.AirBlockState
import com.example.airblock.ui.screens.getDefaultLauncherPackageName

class AppBlockService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            // 1. Obtener Launcher dinámico
            val launcher = getDefaultLauncherPackageName(this)

            // 2. REGLA DE ORO: Ignorar procesos base y críticos
            val isEssential = packageName == this.packageName ||
                    packageName == launcher ||
                    packageName == "com.android.systemui" ||
                    packageName == "android" ||
                    packageName.startsWith("com.android.") ||
                    packageName.startsWith("com.google.android.") ||
                    packageName.startsWith("com.samsung.android.") // Mantenemos esto por seguridad en tu móvil actual

            if (isEssential) return

            // 3. Bloqueo normal
            if (AirBlockState.isLocked && AirBlockState.blockedApps.contains(packageName)) {
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
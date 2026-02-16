package com.example.airblock.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.airblock.MainActivity
import com.example.airblock.state.AirBlockState

class AppBlockService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            val packageName = event.packageName?.toString()?: return
            if (AirBlockState.isLocked && AirBlockState.blockedApps.contains(packageName)){
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
        }
    }

    override fun onInterrupt() {

    }
}
package com.tasnimulhasan.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object Utils {
    fun getBuildTypeName(buildType: String) = when (buildType) {
        "debug" -> "Dev"
        "qa" -> "QA"
        "release" -> "Live"
        else -> "Unknown"
    }
}

fun Context.navigateAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", this.packageName, null)
    intent.data = uri
    startActivity(intent)
}
package com.tasnimulhasan.designsystem

import android.content.Context
import android.graphics.Typeface

object FontsOverride {
    const val REGULAR_FONT = "fonts/Inter-Regular.ttf"
    const val BOLD_FONT = "fonts/Inter-Bold.ttf"
    const val SEMI_BOLD_FONT = "fonts/Inter-SemiBold.ttf"
    const val MEDIUM_FONT = "fonts/Inter-Medium.ttf"
    const val ITALIC_FONT = "fonts/Inter-Italic.ttf"

    fun setDefaultFont(context: Context, staticTypefaceFieldName: String, fontAssetName: String) {
        val regular = Typeface.createFromAsset(context.assets, fontAssetName)
        replaceFont(staticTypefaceFieldName, regular)
    }


    private fun replaceFont(staticTypefaceFieldName: String, newTypeface: Typeface) {
        try {
            val staticField = Typeface::class.java.getDeclaredField(staticTypefaceFieldName)
            staticField.isAccessible = true
            staticField.set(null, newTypeface)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    private val fontCache = HashMap<String, Typeface>()
    fun getTypeface(fontName: String, context: Context): Typeface? {
        var typeface: Typeface? = fontCache[fontName]

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.assets, fontName)
            } catch (e: Exception) {
                return null
            }

            fontCache[fontName] = typeface
        }

        return typeface
    }


}
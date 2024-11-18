package com.tasnimulhasan.common.extfun

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.TypedValue

fun getFontBitmap(context: Context, text: String, color: Int, fontSize: Float, fontPath: String): Bitmap {
    val fontSizePx = convertDipToPix(context, fontSize)
    val padding = (fontSizePx/9)
    val paint = Paint()
    val typeFace: Typeface = Typeface.createFromAsset(context.assets, fontPath)
    paint.isAntiAlias = true
    paint.setTypeface(typeFace)
    paint.setColor(color)
    paint.textSize = fontSizePx

    val textWidth = paint.measureText(text) + padding * 2
    val height = fontSizePx / 0.75
    val bitmap = Bitmap.createBitmap(textWidth.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawText(text, padding, fontSizePx, paint)
    return bitmap
}

fun convertDipToPix(context: Context, dip: Float): Float {
    val intValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.resources.displayMetrics)
    return intValue
}
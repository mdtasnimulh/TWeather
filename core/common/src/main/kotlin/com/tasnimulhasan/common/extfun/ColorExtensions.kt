package com.tasnimulhasan.common.extfun

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.tasnimulhasan.designsystem.R as Res

fun setDetailsTvTextColor(image: Int, context: Context): Int{
    return Palette.from(
        ContextCompat.getDrawable(context, image)?.toBitmap()!!
    ).generate().darkVibrantSwatch?.rgb ?: ContextCompat.getColor(context, Res.color.white)
}

fun setDetailsValueTextColor(image: Int, context: Context): Int{
    return Palette.from(
        ContextCompat.getDrawable(context, image)?.toBitmap()!!
    ).generate().lightVibrantSwatch?.rgb ?: ContextCompat.getColor(context, Res.color.white)
}

fun setTextColor(textView: AppCompatTextView, palette: Palette) {
    textView.paint.shader = LinearGradient(
        0f, 0f, textView.paint.measureText(textView.text.toString()), textView.textSize,
        palette.vibrantSwatch?.rgb!!, (palette.vibrantSwatch?.rgb!! and 0x66FFFFFF),
        Shader.TileMode.CLAMP
    )
}
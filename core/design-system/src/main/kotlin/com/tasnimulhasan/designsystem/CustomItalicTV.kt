package com.tasnimulhasan.designsystem

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

open class CustomItalicTV : AppCompatTextView{

    constructor(context: Context) : super(context){
        applyCustomFont(context)
    }

    constructor(context: Context, attrs:AttributeSet) : super(context,attrs){
        applyCustomFont(context)
    }

    constructor(context: Context, attrs:AttributeSet,defStyleAttr:Int) : super(context,attrs,defStyleAttr){
        applyCustomFont(context)
    }

    private fun applyCustomFont(context: Context) {
        val customFont = FontsOverride.getTypeface(FontsOverride.ITALIC_FONT, context)
        typeface = customFont
    }

}
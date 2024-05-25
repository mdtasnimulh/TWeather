package com.tasnimulhasan.common.extfun

import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import java.util.regex.Matcher
import java.util.regex.Pattern

fun EditText.isEmptyInput(errorMessage: String): Boolean {
    val input = this.text.toString().trim()
    if (input.isEmpty()) this.error = errorMessage
    return input.isEmpty()
}

fun TextView.isEmpty(text: String, errorTV: TextView): Boolean {
    errorTV.isVisible = text.isBlank()
    //errorTV.text = errorText
    return text.isBlank()
}

fun EditText.isNotMandatoryEmailValid(): String {
    val emailRegex = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    val emailText = this.getTextFromTv()
    if (emailText.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
        this.error = "আপনার ইমেইল সঠিক নয়"
    }
    return emailText
}

fun EditText.isEmailValid(email: CharSequence): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun Int.isValueValid(): Boolean {
    return this > 0
}

fun String.isPasswordValid(): Boolean {
    return this.isNotEmpty() && this.length >= 8
}

fun String.isPhoneNumberValid(): Boolean {
    val pattern: Pattern =
        Pattern.compile("((0|01|\\+88|\\+88\\s*\\(0\\)|\\+88\\s*0)\\s*)?1(\\s*[0-9]){9}")
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}

fun <T> List<T>.isListEmpty(): T? {
    if (this.isNotEmpty())
        return this[0]

    return null
}
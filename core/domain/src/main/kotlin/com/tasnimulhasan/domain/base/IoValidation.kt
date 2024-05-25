package com.tasnimulhasan.domain.base

import android.text.TextUtils
import android.util.Patterns
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

class IoValidation @Inject constructor() {

    /*fun loginDataValidation(params: LoginApiUseCase.Params): DataValidationResult {
        if (params.username.isEmpty() || params.username.length < 5)
            return DataValidationResult.Failure(LoginIoResult.InvalidUsername)

        if (!params.password.isPasswordValid())
            return DataValidationResult.Failure(LoginIoResult.InvalidPassword)

        return DataValidationResult.Success
    }

    fun resetPasswordDataValidation(params: ResetPasswordApiUseCase.Params) : DataValidationResult {
        *//*if (params.userName.isEmpty() || params.userName.length < 5)
            return DataValidationResult.Failure(ResetPasswordIoResult.InvalidUsername)*//*

        if (!params.oldPassword.isPasswordValid())
            return DataValidationResult.Failure(ResetPasswordIoResult.InvalidOldPassword)

        if (!params.newPassword.isPasswordValid())
            return DataValidationResult.Failure(ResetPasswordIoResult.InvalidNewPassword)

        *//*if (!params.confirmPassword.isPasswordValid())
            return DataValidationResult.Failure(ResetPasswordIoResult.InvalidConfirmPassword)*//*

        if (params.newPassword != params.confirmPassword)
            return DataValidationResult.Failure(ResetPasswordIoResult.InvalidConfirmPassword)

        return DataValidationResult.Success

    }*/
}

fun String.isPhoneNumberValid(): Boolean {
    val pattern: Pattern =
        Pattern.compile("((0|01|\\+88|\\+88\\s*\\(0\\)|\\+88\\s*0)\\s*)?1(\\s*[0-9]){9}")
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}

fun String.isPasswordValid(): Boolean {
    return this.isNotEmpty() && this.length >= 6
}

fun String.isNameValid(): Boolean {
    return this.isNotEmpty() && this.length >= 5
}

fun String.isEmailValid(email: CharSequence): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
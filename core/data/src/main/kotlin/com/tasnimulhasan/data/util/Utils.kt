package com.tasnimulhasan.data.util

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object Utils {
    fun mConverter(data: String): RequestBody =
        data.toRequestBody("text/plain".toMediaTypeOrNull())

    fun mConvertImg(imageFile: File?, fileName : String): MultipartBody.Part? {
        val body = imageFile?.asRequestBody("image/jpg".toMediaTypeOrNull())
        return body?.let { MultipartBody.Part.createFormData(fileName, imageFile.name, it) }
    }
}
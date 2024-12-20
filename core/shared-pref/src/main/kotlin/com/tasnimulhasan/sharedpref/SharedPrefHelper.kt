package com.tasnimulhasan.sharedpref

import android.content.Context

class SharedPrefHelper(application: Context) {
    private var sharedPreferences = application.getSharedPreferences("com.tasnimulhasan.tweather", 0)
    fun putString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String{
        return sharedPreferences.getString(key, "")!!
    }

    fun putBool(key: String, value: Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun putInt(key: String, value: Int){
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun putLong(key: String, value: Long){
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, -1)
    }

    fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, -1)
    }

    fun clearAllCache() {
        sharedPreferences.edit().clear().apply()
    }

    fun savedWeatherData(exists: Boolean, cityName: String, temperature: String, condition: String, windSpeed: String, rain: String, feelsLike: String, icon: String, dayMax: String, dayMin: String) {
        with(sharedPreferences.edit()) {
            putBoolean("EXISTS", exists)
            putString("CITY_NAME", cityName)
            putString("CURRENT_TEMP", temperature)
            putString("CURRENT_CONDITION", condition)
            putString("WIND_SPEED", windSpeed)
            putString("RAIN", rain)
            putString("FEELS_LIKE", feelsLike)
            putString("ICON", icon)
            putString("DAY_MAX", dayMax)
            putString("DAY_MIN", dayMin)
            putLong("LAST_UPDATE", System.currentTimeMillis())
            apply()
        }
    }

    fun getWeatherData(): WidgetWeatherData {
        val exists = sharedPreferences.getBoolean("EXISTS", true)
        val cityName = sharedPreferences.getString("CITY_NAME", "Unknown") ?: "Unknown"
        val temperature = sharedPreferences.getString("CURRENT_TEMP", "0.0") ?: "0.0"
        val condition = sharedPreferences.getString("CURRENT_CONDITION", "Unknown") ?: "Unknown"
        val windSpeed = sharedPreferences.getString("WIND_SPEED", "0.0") ?: "0.0"
        val rain = sharedPreferences.getString("RAIN", "0.0") ?: "0.0"
        val feelsLike = sharedPreferences.getString("FEELS_LIKE", "0.0") ?: "0.0"
        val icon = sharedPreferences.getString("ICON", "1d") ?: "1d"
        val dayMax = sharedPreferences.getString("DAY_MAX", "0") ?: "0"
        val dayMin = sharedPreferences.getString("DAY_MIN", "0") ?: "0"
        return WidgetWeatherData(exists, cityName, temperature, condition, windSpeed, rain, feelsLike, icon, dayMax, dayMin)
    }

    fun getLastUpdateTime(): Long {
        return sharedPreferences.getLong("LAST_UPDATE", 0)
    }
}

data class WidgetWeatherData(
    val exists: Boolean,
    val cityName: String,
    val temperature: String,
    val condition: String,
    val windSpeed: String,
    val rain: String,
    val feelsLike: String,
    val icon: String,
    val dayMax: String,
    val dayMin: String,
)
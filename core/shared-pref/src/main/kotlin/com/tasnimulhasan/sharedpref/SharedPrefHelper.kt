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

    fun savedWeatherData(cityName: String, temperature: String, condition: String) {
        with(sharedPreferences.edit()) {
            putString("CITY_NAME", cityName)
            putString("CURRENT_TEMP", temperature)
            putString("CURRENT_CONDITION", condition)
            putLong("LAST_UPDATE", System.currentTimeMillis())
            apply()
        }
    }

    fun getWeatherData(): WidgetWeatherData {
        val cityName = sharedPreferences.getString("CITY_NAME", "Unknown") ?: "Unknown"
        val temperature = sharedPreferences.getString("CURRENT_TEMP", "N/A") ?: "N/A"
        val condition = sharedPreferences.getString("CURRENT_CONDITION", "N/A") ?: "N/A"
        return WidgetWeatherData(cityName, temperature, condition)
    }

    fun getLastUpdateTime(): Long {
        return sharedPreferences.getLong("LAST_UPDATE", 0)
    }
}

data class WidgetWeatherData(
    val cityName: String,
    val temperature: String,
    val condition: String
)
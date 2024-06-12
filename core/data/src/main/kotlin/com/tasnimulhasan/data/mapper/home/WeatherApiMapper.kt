package com.tasnimulhasan.data.mapper.home

import com.tasnimulhasan.apiresponse.home.WeatherApiResponse
import com.tasnimulhasan.data.mapper.Mapper
import com.tasnimulhasan.entity.home.CurrentWeatherData
import com.tasnimulhasan.entity.home.CurrentWeatherConditionData
import com.tasnimulhasan.entity.home.DailyFeelsLikeData
import com.tasnimulhasan.entity.home.DailyTemperatureData
import com.tasnimulhasan.entity.home.DailyWeatherConditionData
import com.tasnimulhasan.entity.home.DailyWeatherData
import com.tasnimulhasan.entity.home.HourlyWeatherConditionData
import com.tasnimulhasan.entity.home.HourlyWeatherData
import com.tasnimulhasan.entity.home.WeatherApiEntity
import javax.inject.Inject

class WeatherApiMapper @Inject constructor() :
    Mapper<WeatherApiResponse, WeatherApiEntity> {
    override fun mapFromApiResponse(type: WeatherApiResponse): WeatherApiEntity {
        return WeatherApiEntity(
            lat = type.lat ?: 0.0,
            lon = type.lon ?: 0.0,
            currentWeatherData = CurrentWeatherData(
                currentTime = (type.current?.dt ?: 0L).toLong(),
                currentSunrise = (type.current?.sunrise ?: 0L).toLong(),
                currentSunset = (type.current?.sunset ?: 0L).toLong(),
                currentTemp = type.current?.temp ?: 0.0,
                currentFeelsLike = type.current?.feels_like ?: 0.0,
                currentPressure = type.current?.pressure ?: 0,
                currentHumidity = type.current?.humidity ?: 0,
                currentDewPoint = type.current?.dew_point ?: 0.0,
                currentUvi = type.current?.uvi ?: 0.0,
                currentClouds = type.current?.clouds ?: 0,
                currentVisibility = type.current?.visibility ?: 0,
                currentWindSpeed = type.current?.wind_speed ?: 0.0,
                currentWindDegree = type.current?.wind_deg ?: 0,
                currentWindGust = type.current?.wind_gust ?: 0.0,
                currentRain = type.current?.rain?.`1h` ?: 0.0,
                currentWeatherCondition = type.current?.weather?.map { currentWeatherConditionData ->
                    CurrentWeatherConditionData(
                        currentWeatherIcon = currentWeatherConditionData.icon ?: "",
                        currentWeatherCondition = currentWeatherConditionData.main ?: "",
                        currentWeatherConditionDesc = currentWeatherConditionData.description ?: ""
                    )
                } ?: emptyList()
            ),
            dailyWeatherData = type.daily?.map { daily ->
                DailyWeatherData(
                    day = (daily.dt ?: 0L).toLong(),
                    dailySunrise = (daily.sunrise ?: 0L).toLong(),
                    dailySunSet = (daily.sunset ?: 0L).toLong(),
                    dailyMoonRise = (daily.moonrise ?: 0L).toLong(),
                    dailyMoonSet = (daily.moonset ?: 0L).toLong(),
                    dailyMoonPhase = daily.moon_phase ?: 0.0,
                    dailySummary = daily.summary ?: "",
                    dailyTemp = DailyTemperatureData(
                        dailyDayTemperature = daily.temp?.day ?: 0.0,
                        dailyNightTemperature = daily.temp?.night ?: 0.0,
                        dailyMorningTemperature = daily.temp?.morn ?: 0.0,
                        dailyEveningTemperature = daily.temp?.eve ?: 0.0,
                        dailyMinimumTemperature = daily.temp?.min ?: 0.0,
                        dailyMaximumTemperature = daily.temp?.max ?: 0.0
                    ),
                    dailyFeelsLike = DailyFeelsLikeData(
                        dailyDayFeelsLike = daily.feels_like?.day ?: 0.0,
                        dailyNightFeelsLike = daily.feels_like?.night ?: 0.0,
                        dailyMorningFeelsLike = daily.feels_like?.morn ?: 0.0,
                        dailyEveningFeelsLike = daily.feels_like?.eve ?: 0.0,
                    ),
                    dailyPressure = daily.pressure ?: 0,
                    dailyHumidity = daily.humidity ?: 0,
                    dailyDewPoint = daily.dew_point ?: 0.0,
                    dailyWindSpeed = daily.wind_speed ?: 0.0,
                    dailyWindDegree = daily.wind_deg ?: 0,
                    dailyWindGust = daily.wind_gust ?: 0.0,
                    dailyWeatherCondition = daily.weather?.map { dailyWeatherConditionData ->
                        DailyWeatherConditionData(
                            dailyWeatherIcon = dailyWeatherConditionData.icon ?: "",
                            dailyWeatherCondition = dailyWeatherConditionData.main ?: "",
                            dailyWeatherConditionDesc = dailyWeatherConditionData.description ?: ""
                        )
                    } ?: emptyList(),
                    dailyRain = daily.rain ?: 0.0,
                    dailyClouds = daily.clouds ?: 0,
                    dailyUvi = daily.uvi ?: 0.0
                )
            } ?: emptyList(),
            hourlyWeatherData = type.hourly?.map { hourly ->
                HourlyWeatherData(
                    hourlyTime = hourly.dt ?: 0,
                    hourlyTemperature = hourly.temp ?: 0.0,
                    hourlyFeelsLike = hourly.feels_like ?: 0.0,
                    hourlyPressure = hourly.pressure ?: 0,
                    hourlyHumidity = hourly.humidity ?: 0,
                    hourlyDewPoint = hourly.dew_point ?: 0.0,
                    hourlyUvi = hourly.uvi ?: 0.0,
                    hourlyClouds = hourly.clouds ?: 0,
                    hourlyVisibility = hourly.visibility ?: 0,
                    hourlyWindSpeed = hourly.wind_speed ?: 0.0,
                    hourlyWindDegree = hourly.wind_deg ?: 0,
                    hourlyWindGust = hourly.wind_gust ?: 0.0,
                    hourlyRain = hourly.rain?.`1h` ?: 0.0,
                    hourlyWeatherCondition = hourly.weather?.map { hourlyWeatherConditionData ->
                        HourlyWeatherConditionData(
                            hourlyWeatherIcon = hourlyWeatherConditionData.icon ?: "",
                            hourlyWeatherCondition = hourlyWeatherConditionData.main ?: "",
                            hourlyWeatherConditionDesc = hourlyWeatherConditionData.description ?: ""
                        )
                    } ?: emptyList(),
                )
            } ?: emptyList()
        )
    }
}
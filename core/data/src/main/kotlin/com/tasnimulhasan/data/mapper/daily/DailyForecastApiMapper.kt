package com.tasnimulhasan.data.mapper.daily

import com.tasnimulhasan.entity.daily.DailyCoordinate
import com.tasnimulhasan.apiresponse.daily.DailyForecastApiResponse
import com.tasnimulhasan.data.mapper.Mapper
import com.tasnimulhasan.entity.daily.City
import com.tasnimulhasan.entity.daily.DailyFeelsLike
import com.tasnimulhasan.entity.daily.DailyForecast
import com.tasnimulhasan.entity.daily.DailyForecastApiEntity
import com.tasnimulhasan.entity.daily.DailyTemp
import com.tasnimulhasan.entity.daily.DailyWeather
import javax.inject.Inject

class DailyForecastApiMapper @Inject constructor() :
    Mapper<DailyForecastApiResponse, DailyForecastApiEntity> {
    override fun mapFromApiResponse(type: DailyForecastApiResponse): DailyForecastApiEntity {
        return DailyForecastApiEntity(
            city = City(
                coordinate = DailyCoordinate(
                    lat = type.city?.coord?.lat ?: 0.0,
                    lon = type.city?.coord?.lon ?: 0.0
                ),
                country = type.city?.country ?: "",
                cityId = type.city?.id ?: 0,
                cityName = type.city?.name ?: "",
                population = type.city?.population ?: 0,
                timezone = type.city?.timezone ?: 0
            ),
            cnt = type.cnt ?: 0,
            cod = type.cod ?: "",
            dailyList = type.list?.map { dailyForecast ->
                DailyForecast(
                    clouds = dailyForecast?.clouds ?: 0,
                    deg = dailyForecast?.deg ?: 0,
                    dateTime = dailyForecast?.dt?.toLong() ?: 0,
                    feelsLike = DailyFeelsLike(
                        dayFeelsLike = dailyForecast?.feels_like?.day ?: 0.0,
                        eveFeelsLike = dailyForecast?.feels_like?.eve ?: 0.0,
                        mornFeelsLike = dailyForecast?.feels_like?.morn ?: 0.0,
                        nightFeelsLike = dailyForecast?.feels_like?.night ?: 0.0
                    ),
                    gust = dailyForecast?.gust ?: 0.0,
                    humidity = dailyForecast?.humidity ?: 0,
                    pop = dailyForecast?.pop ?: 0.0,
                    pressure = dailyForecast?.pressure ?: 0,
                    rain = dailyForecast?.rain ?: 0.0,
                    speed = dailyForecast?.speed ?: 0.0,
                    sunrise = dailyForecast?.sunrise ?: 0,
                    sunset = dailyForecast?.sunset ?: 0,
                    temp = DailyTemp(
                        dayTemp = dailyForecast?.temp?.day ?: 0.0,
                        eveTemp = dailyForecast?.temp?.eve ?: 0.0,
                        maxTemp = dailyForecast?.temp?.max ?: 0.0,
                        minTemp = dailyForecast?.temp?.min ?: 0.0,
                        mornTemp = dailyForecast?.temp?.morn ?: 0.0,
                        nightTemp = dailyForecast?.temp?.night ?: 0.0
                    ),
                    weather = dailyForecast?.weather?.map { dailyWeather ->
                        DailyWeather(
                            description = dailyWeather?.description ?: "",
                            icon = dailyWeather?.icon ?: "",
                            id = dailyWeather?.id ?: 0,
                            main = dailyWeather?.main ?: ""
                        )
                    } ?: emptyList()
                )
            } ?: emptyList()
        )
    }
}
package com.example.myapplication2.weatherapi

import com.google.gson.annotations.SerializedName

data class WeatherData(
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("body")
    val body: Body
)

data class Body(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: MyData
)

data class MyData (
        @SerializedName("location")
    val location: Location,
        @SerializedName("weather")
    val weather: Weather,
        @SerializedName("lottery")
    val lottery: List<Int>
)

data class Location(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("city")
    val city: String
)

data class Weather(
    @SerializedName("celcius")
    val celcius: Int,
    @SerializedName("feelCelcius")
    val feelCelcius: Int,
    @SerializedName("maxCelcius")
    val maxCelcius: Int,
    @SerializedName("minCelcius")
    val minCelcius: Int,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("sunrise")
    val sunrise: Long,
    @SerializedName("sunset")
    val sunset: Long,
    @SerializedName("timezone")
    val timezone: Long,
    @SerializedName("icon")
    val icon: String
)
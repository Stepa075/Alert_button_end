package com.stepa0751.alertbuttonend.location

import org.osmdroid.util.GeoPoint
//  Создали дата-класс, которым будут передаваться данные в активити
data class LocationModel(
    val velocity: Float = 0.0f,
    val distance: Float = 0.0f,
    val latitude: Float = 0.0f,
    val longitude: Float = 0.0f,
    val geoPointsList: ArrayList<GeoPoint>
)
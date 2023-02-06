package com.test.fitnessstudios.data.models.studio

data class Studio(
    val id: String,
    val name: String,
    val image_url: String,
    val url: String,
    val rating: String,
    val coordinates: Coordinates,
    val distance: Double,
    val phone: String,
    val display_phone: String
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)
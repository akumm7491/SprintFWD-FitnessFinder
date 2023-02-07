package com.test.fitnessstudios.data.models.routes

import com.google.android.gms.maps.model.LatLngBounds

/**
 * Route model that uses Google Route for the basic structure
 */
data class Route(
    val status: String,
    val bounds: LatLngBounds,
    val legs: List<DirectionsLeg>,
    val overview_polyline: DirectionsPolyline
)

data class DirectionsLeg(
    val distance: TextValueObject,
    val duration: TextValueObject,
    val start_location: LatLngLiteral,
    val end_location: LatLngLiteral,
    val steps: List<DirectionsStep>
)

data class DirectionsStep(
    val distance: TextValueObject,
    val duration: TextValueObject,
    val end_location: LatLngLiteral,
    val start_location: LatLngLiteral,
    val polyline: DirectionsPolyline
)

data class DirectionsPolyline(
    val points: String
)

data class LatLngLiteral(
    val lat: Double,
    val lng: Double
)

data class LatLngQuery(
    val lat: Double,
    val lng: Double
) {
    // Format LatLngQuery toString to match required Query param format
    override fun toString(): String {
        return "$lat,$lng"
    }
}

data class TextValueObject(
    val text: String,
    val value: Int
)

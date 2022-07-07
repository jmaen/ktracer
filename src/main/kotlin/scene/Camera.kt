package scene

import kotlinx.serialization.Serializable

import models.Vector3

@Serializable
data class Camera(
    val point: Vector3,
    val canvasOrigin: Vector3,
    val canvasWidth: Double,
    val canvasHeight: Double,
    val pixelsPerUnit: Int,
    val superSamplingFactor: Int,
    val focalLength: Double,
    val aperture: Double,
    val samplesPerRay: Int
    ) {
    init {
        if(point.z <= 0) {
            throw IllegalArgumentException("Camera's z value has to be > 0.")
        }
        if(canvasOrigin.z != 0.0) {
            throw IllegalArgumentException("Canvas' z value has to be 0.")
        }
    }
}
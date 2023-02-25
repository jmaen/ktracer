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
    val focalLength: Double,
    val aperture: Double
    ) {
    init {
        require(point.z > 0) { "Camera's z value has to be > 0." }
        require(canvasOrigin.z == 0.0) { "Canvas' z value has to be 0." }
        require(pixelsPerUnit >= 1) { "There has to be at least one pixel per unit." }
    }
}

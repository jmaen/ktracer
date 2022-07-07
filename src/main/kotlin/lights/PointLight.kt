package lights

import kotlinx.serialization.Serializable

import models.*

@Serializable
data class PointLight(private val point: Vector3, val color: Color, val intensity: Double) : PositionalLight {
    override fun getPoints(): List<Vector3> {
        return listOf(point)
    }
}
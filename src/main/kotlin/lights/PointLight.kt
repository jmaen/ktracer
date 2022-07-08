package lights

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*

@Serializable
@SerialName("point")
class PointLight(private val point: Vector3, override val color: Color, override val intensity: Double) : PositionalLight() {
    override fun getPoints(): List<Vector3> {
        return listOf(point)
    }
}
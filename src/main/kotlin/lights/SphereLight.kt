package lights

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*

@Serializable
@SerialName("sphere")
class SphereLight(val point: Vector3,
                  val radius: Double,
                  override val color: Color,
                  override val intensity: Double,
                  val sampleDensity: Int) : PositionalLight() {
    override fun getPoints(): List<Vector3> {
        TODO("Not yet implemented")
    }
}
package lights

import models.Color
import models.Vector3

class SphereLight(val point: Vector3,
                  val radius: Double,
                  val color: Color,
                  val intensity: Double,
                  val density: Int) : PositionalLight {
    override fun getPoints(): List<Vector3> {
        TODO("Not yet implemented")
    }
}
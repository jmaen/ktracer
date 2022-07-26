package objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

import models.Hit
import models.Ray
import models.Rotation3
import models.Vector3

@Serializable
@SerialName("transform")
class Transform(
    val transformable: Transformable,
    val translate: Vector3 = Vector3.ZERO,
    val rotate: Rotation3 = Rotation3.ZERO,
    val scale: Double = 1.0) : Hittable {
    @Transient
    private lateinit var transformed: Transformable

    init {
//        transformed = transformable.translate(translate)
        transformed = transformable.rotate(rotate)
//        transformed = transformed.scale(scale)
    }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        return transformed.hit(ray, tMin, tMax)
    }

    override fun checkPoint(point: Vector3): Boolean {
        return transformed.checkPoint(point)
    }
}
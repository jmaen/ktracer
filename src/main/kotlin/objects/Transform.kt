package objects

import kotlin.math.PI
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

import models.Hit
import models.Ray
import models.Vector3

@Serializable
@SerialName("transform")
class Transform(
    private val transformable: Transformable,
    private val translate: Vector3 = Vector3.ZERO,
    private val rotateX: Double = 0.0,
    private val rotateY: Double = 0.0,
    private val rotateZ: Double = 0.0,
    private val scale: Double = 1.0) : Hittable {
    @Transient
    private lateinit var transformed: Transformable

    init {
        transformed = transformable.translate(translate)
        transformed = transformed.scale(scale)
        transformed = transformed.rotateX(rotateX * (PI / 180))
        transformed = transformed.rotateY(rotateY * (PI / 180))
        transformed = transformed.rotateZ(rotateZ * (PI / 180))
    }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        return transformed.hit(ray, tMin, tMax)
    }

    override fun checkPoint(point: Vector3): Boolean {
        return transformed.checkPoint(point)
    }
}
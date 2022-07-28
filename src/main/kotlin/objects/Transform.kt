package objects

import kotlin.math.PI
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*

@Serializable
@SerialName("transform")
class Transform(
    private var transformable: Transformable,
    private val translate: Vector3? = null,
    private val rotate: Rotation? = null,
    private val scale: Double? = null) : Hittable {

    init {
        if(translate != null) {
            transformable = transformable.translate(translate)
        }
        if(scale != null) {
            transformable = transformable.scale(scale)
        }
        if(rotate != null) {
            var (axis, angle) = rotate
            angle *= PI / 180
            transformable = when(axis) {
                "x" -> transformable.rotateX(angle)
                "y" -> transformable.rotateY(angle)
                "z" -> transformable.rotateZ(angle)
                else -> throw IllegalStateException()
            }
        }
    }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        return transformable.hit(ray, tMin, tMax)
    }

    override fun checkPoint(point: Vector3): Boolean {
        return transformable.checkPoint(point)
    }
}
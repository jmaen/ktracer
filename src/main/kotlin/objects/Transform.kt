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
    private val scale: Double? = null,
    private val rotate: Rotation? = null) : Hittable {
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
                Rotation.Axis.X -> transformable.rotateX(angle)
                Rotation.Axis.Y -> transformable.rotateY(angle)
                Rotation.Axis.Z -> transformable.rotateZ(angle)
            }
        }
    }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        return transformable.hit(ray, tMin, tMax)
    }
}
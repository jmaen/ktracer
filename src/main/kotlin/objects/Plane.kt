package objects

import kotlin.math.abs
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

import materials.Material
import models.*

@Serializable
@SerialName("plane")
class Plane(private val point: Vector3, val normal: Vector3, private val material: Material) : Transformable {
    @Transient
    private val d = point dot normal

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit?  {
        val or = ray.origin
        val di = ray.direction
        var n = normal

        /*
        plane equation:
        p ⋅ n = pn
        <=> (o + t*d) ⋅ n = pn
        <=> t = (pn - (o ⋅ n)) / (d ⋅ n)
         */
        val dn = di dot n
        if(abs(dn) > 0.0001) {  // ray is not (almost) parallel to plane
            val t = (d - (or dot n)) / dn
            if(t in tMin..tMax) {
                val intersection = ray.pointAt(t)
                n = if(dn < 0) n else -n  // make sure normal points at the ray origin
                return Hit(intersection, n, ray, t, material)
            }
        }

        return null
    }

    fun checkPoint(point: Vector3): Boolean {
        val difference = (point dot normal) - d
        return abs(difference) < 0.0001
    }

    override fun translate(offset: Vector3): Plane {
        return Plane(point + offset, normal, material)
    }

    override fun scale(factor: Double): Plane = this

    override fun rotateX(angle: Double): Plane {
        return Plane(point, normal.rotateX(angle), material)
    }

    override fun rotateY(angle: Double): Plane {
        return Plane(point, normal.rotateY(angle), material)
    }

    override fun rotateZ(angle: Double): Plane {
        return Plane(point, normal.rotateZ(angle), material)
    }
}

package objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

import materials.Material
import models.*

@Serializable
@SerialName("disk")
class Disk(private val center: Vector3,
           private val normal: Vector3,
           private val radius: Double,
           private val material: Material) : Transformable {
    @Transient
    private val plane = Plane(center, normal, material)

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val hit = plane.hit(ray, tMin, tMax)

        // if plane is hit, check if hit point is inside the circle
        if(hit != null) {
            if(checkCircle(hit.point)) {
                return hit
            }
        }

        return null
    }

    private fun checkCircle(point: Vector3): Boolean {
        // check if point is inside the circle
        val distance = (point - center).length()
        return distance <= radius
    }

    override fun translate(offset: Vector3): Transformable {
        return Disk(center + offset, normal, radius, material)
    }

    override fun scale(factor: Double): Transformable {
        return Disk(center, normal, radius * factor, material)
    }

    override fun rotateX(angle: Double): Transformable {
        return Disk(center, normal.rotateX(angle), radius, material)
    }

    override fun rotateY(angle: Double): Transformable {
        return Disk(center, normal.rotateY(angle), radius, material)
    }

    override fun rotateZ(angle: Double): Transformable {
        return Disk(center, normal.rotateZ(angle), radius, material)
    }
}
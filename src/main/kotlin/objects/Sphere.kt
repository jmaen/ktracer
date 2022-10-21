package objects

import kotlin.math.sqrt
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import materials.Material
import models.*
import util.*

@Serializable
@SerialName("sphere")
class Sphere(private val center: Vector3, private val radius: Double, private val material: Material) : Transformable {
    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val co = ray.origin - center
        val d = ray.direction
        val r = radius

        /*
        sphere equation:
        (px - cx)^2 + (py - cy)^2 + (pz - cz)^2 = r^2
        <=> (p - c) ⋅ (p - c) = r^2
        <=> (o + t*d - c) ⋅ (o + t*d - c) = r^2
        <=> (d ⋅ d)*t^2 + (2d ⋅ (o - c))*t + (o - c) ⋅ (o - c) - r^2 = 0
         */
        val a = d dot d
        val b = 2*d dot co
        val c = (co dot co) - r*r

        val discriminant = b*b - 4*a*c
        if(discriminant >= 0) {  // there is at least 1 solution
            var t = (-b - sqrt(discriminant)) / (2*a)  // only the smaller t is interesting
            if(t < tMin) {
                t = (-b + sqrt(discriminant)) / (2*a)  // camera might be inside the sphere -> try the bigger solution
            }
            if(t in tMin..tMax) {
                val intersection = ray.pointAt(t)
                return Hit(intersection, intersection - center, ray, t, material)
            }
        }

        return null
    }

    override fun translate(offset: Vector3): Sphere {
        return Sphere(center + offset, radius, material)
    }

    override fun scale(factor: Double): Sphere {
        return Sphere(center, radius * factor, material)
    }

    override fun rotateX(angle: Double): Sphere = this

    override fun rotateY(angle: Double): Sphere = this

    override fun rotateZ(angle: Double): Sphere = this
}
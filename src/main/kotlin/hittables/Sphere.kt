package hittables

import image.Color
import kotlin.math.sqrt
import models.*

class Sphere(private val center: Vector3, private val radius: Double, private val color: Color) : Hittable {
    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val co = ray.origin - center
        val d = ray.direction
        val r = radius

        /*
        sphere equation:
        (px - cx)^2 + (py - cy)^2 + (pz - cz)^2 = r^2
        <=> (p - c) ⋅ (p - c) = r^2
        <=> (o + t*d - c) ⋅ (o + t*d - c) = r^2
        <=> (d ⋅ d)t^2 + (2d ⋅ (o - c))t + (o - c) ⋅ (o - c) - r^2 = 0
         */
        val a = d dot d
        val b = 2*d dot co
        val c = (co dot co) - r*r

        val discriminant = b*b - 4*a*c
        if(discriminant >= 0) {  // at least 1 solution
            val t = (-b - sqrt(discriminant)) / (2*a)  // only the smaller t is interesting -> no need to calculate second solution
            if(t in tMin..tMax) {
                val intersection = ray.pointAt(t)
                return Hit(intersection, normalAt(intersection), t, color)
            }
        }

        return null
    }

    private fun normalAt(point: Vector3): Vector3 {
        return (point - center).normalized()
    }
}
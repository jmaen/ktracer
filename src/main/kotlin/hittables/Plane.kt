package hittables

import kotlin.math.abs

import models.*

open class Plane(point: Vector3, private val normal: Vector3, private val color: Color) : Hittable {
    private val pn = point dot normal

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit?  {
        val o = ray.origin
        val d = ray.direction
        var n = normal

        /*
        plane equation:
        p ⋅ n = pn
        <=> (o + t*d) ⋅ n = pn
        <=> t = (pn - (o ⋅ n)) / (d ⋅ n)
         */
        val dn = d dot n
        if(abs(dn) > 0.0001) {  // ray is not (almost) parallel to plane
            val t = (pn - (o dot n)) / dn
            if(t in tMin..tMax) {
                val intersection = ray.pointAt(t)
                n = if(dn < 0) n else -n  // make sure normal points at the ray origin
                return Hit(intersection, n, t, color)
            }
        }

        return null
    }
}
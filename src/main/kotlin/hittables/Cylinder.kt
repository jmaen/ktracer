package hittables

import geometry.*
import shading.Material
import kotlin.math.sqrt

class Cylinder(private val center1: Vector3, private val center2: Vector3, private val radius: Double, private val material: Material) : Hittable {
    private val circle1 = Circle(center1, center1 - center2, radius, material)
    private val circle2 = Circle(center2, center2 - center1, radius, material)

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val ao = ray.origin - center1
        val n = (center2 - center1).normalized()
        val d = ray.direction

        val a = (d - (d dot n)*n) dot (d - (d dot n)*n)
        val b = 2*((d - (d dot n)*n) dot (ao - (ao dot n)*n))
        val c = ((ao - (ao dot n)*n) dot (ao - (ao dot n)*n)) - radius*radius

        var smallestT: Double? = null
        var normal: Vector3? = null
        val discriminant = b*b - 4*a*c
        if(discriminant >= 0) {  // there is at least 1 solution
            var t = (-b - sqrt(discriminant)) / (2*a)  // only the smaller t is interesting
            if(t < tMin) {
                t = (-b + sqrt(discriminant)) / (2*a)  // camera might be inside the cylinder -> try the bigger solution
            }
            if(t in tMin..tMax) {
                val intersection = ray.pointAt(t)
                val closestPoint = getClosestPointOnAxis(intersection)

                // check if closest point is inside the cylinder
                val d = (center2 - center1).length()
                val d1 = (closestPoint - center1).length()
                val d2 = (closestPoint - center2).length()
                if(d1 <= d && d2 <= d) {
                    smallestT = t
                    normal = intersection - closestPoint
                }
            }
        }

        val h1 = circle1.hit(ray, tMin, tMax)
        val t1 = h1?.t
        if(t1 != null && t1 in tMin..tMax && (smallestT == null || t1 < smallestT)) {
            smallestT = t1
            normal = h1.normal
        }
        val h2 = circle2.hit(ray, tMin, tMax)
        val t2 = h2?.t
        if(t2 != null && t2 in tMin..tMax && (smallestT == null || t2 < smallestT)) {
            smallestT = t2
            normal = h2.normal
        }

        if(smallestT != null && normal != null) {
            val intersection = ray.pointAt(smallestT)
            return Hit(intersection, normal, ray, smallestT, material)
        }

        return null
    }

    override fun checkPoint(point: Vector3): Boolean {
        TODO()
    }

    private fun getClosestPointOnAxis(point: Vector3): Vector3 {
        val n = (center2 - center1).normalized()
        return center1 + ((point - center1) dot n)*n
    }
}
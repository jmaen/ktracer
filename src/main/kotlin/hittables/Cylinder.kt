package hittables

import kotlin.math.sqrt

import geometry.*
import shading.Material
import util.*

class Cylinder(private val center1: Vector3, private val center2: Vector3, private val radius: Double, private val material: Material) : Hittable {
    private val disk1 = Disk(center1, center1 - center2, radius, material)
    private val disk2 = Disk(center2, center2 - center1, radius, material)

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val ao = ray.origin - center1
        val n = (center2 - center1).normalized()
        val d = ray.direction

        /*
        axis equation:
        c1 + u*(c2 - c1)

        distance point - line:
        line: x = a + u*b
        (p - a) - ((p - a) ⋅ b)*b

        cylinder equation: (vec^2 := vec ⋅ vec)
        |(p - a) - ((p - a) ⋅ b)*b| = r
        <=> (p - c1 - ((c2 - c1) ⋅ (p - c1))*(c2 - c1))^2 - r^2 = 0
        <=> ((o + t*d) - c1 - ((c2 - c1) ⋅ ((o + t*d) - c1))*(c2 - c1))^2 - r^2 = 0
        <=> (d - (d ⋅ (c2 - c1))*(c2 - c1)^2*t^2
            + 2*((d - (d ⋅ (c2 - c1))*(c2 - c1)) ⋅ ((o - c1) - ((o - c1) ⋅ (c2 - c1))*(c2 - c1)))*t
            + ((o - c1) - ((o - c1) ⋅ (c2 - c1))*(c2 - c1))^2 - r^2
        */
        val dn = d - (d dot n)*n
        val aon = ao - (ao dot n)*n
        val a = dn dot dn
        val b = 2*(dn dot aon)
        val c = (aon dot aon) - radius*radius

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

                // check if the closest point on the axis is inside the cylinder (between disk centers)
                val height = (center2 - center1).length()
                val d1 = (closestPoint - center1).length()
                val d2 = (closestPoint - center2).length()
                if(d1 <= height && d2 <= height) {
                    smallestT = t
                    normal = intersection - closestPoint
                }
            }
        }

        // check top and bottom disk
        val hit1 = disk1.hit(ray, tMin, tMax)
        val t1 = hit1?.t
        if(t1 != null && t1 in tMin..tMax && (smallestT == null || t1 < smallestT)) {
            smallestT = t1
            normal = hit1.normal
        }
        val hit2 = disk2.hit(ray, tMin, tMax)
        val t2 = hit2?.t
        if(t2 != null && t2 in tMin..tMax && (smallestT == null || t2 < smallestT)) {
            smallestT = t2
            normal = hit2.normal
        }

        if(smallestT != null && normal != null) {
            val intersection = ray.pointAt(smallestT)
            return Hit(intersection, normal, ray, smallestT, material)
        }

        return null
    }

    override fun checkPoint(point: Vector3): Boolean {
        // check top and bottom disk
        if(disk1.checkPoint(point)) {
            return true
        }
        if(disk2.checkPoint(point)) {
            return true
        }

        // check cylinder
        val closestPoint = getClosestPointOnAxis(point)
        val distance = (closestPoint - point).length()
        return distance == radius
    }

    private fun getClosestPointOnAxis(point: Vector3): Vector3 {
        val n = (center2 - center1).normalized()
        return center1 + ((point - center1) dot n)*n
    }
}
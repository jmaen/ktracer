package objects

import kotlin.math.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

import materials.Material
import models.*
import util.*

@Serializable
@SerialName("cone")
class Cone(
    private val center: Vector3,
    private val apex: Vector3,
    private val radius: Double,
    private val material: Material) : Transformable {
    @Transient
    private val disk = Disk(center, center - apex, radius, material)
    @Transient
    private val axis = (center - apex)
    @Transient
    private val normalizedAxis = axis.normalized()
    @Transient
    private val height = axis.length()
    @Transient
    private val m = (radius * radius) / (height * height)
    @Transient
    private val angle = atan(radius / height)

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val ao = ray.origin - apex
        val d = ray.direction
        val na = normalizedAxis
        val dna = d dot na
        val aona = ao dot na

        val a = (d dot d) - m*dna*dna - dna*dna
        val b = 2*((d dot ao) - m*dna*aona - dna*aona)
        val c = (ao dot ao) - m*aona*aona - aona*aona

        var coneT: Double? = null
        var normal: Vector3? = null

        val discriminant = b*b - 4*a*c
        if(discriminant >= 0) {  // there is at least 1 solution
            var t = (-b - sqrt(discriminant)) / (2*a)  // only the smaller t is interesting
            if(t < tMin) {
                t = (-b + sqrt(discriminant)) / (2*a)  // camera might be inside the cone -> try the bigger solution
            }
            if(t in tMin..tMax) {
                val intersection = ray.pointAt(t)
                val closestPoint = getClosestPointOnAxis(intersection)

                // check if the closest point on the axis is inside the cone (between apex and center)
                val d1 = (closestPoint - apex).length()
                val d2 = (closestPoint - center).length()
                if(d1 <= height && d2 <= height) {
                    coneT = t
                    normal = calculateNormal(intersection)
                }
            }
        }

        // check bottom disk
        val hit = disk.hit(ray, tMin, tMax)
        val t = hit?.t
        if(t != null && t in tMin..tMax && (coneT == null || t < coneT)) {
            return hit
        }

        // check cone
        if(coneT != null && normal != null) {
            val intersection = ray.pointAt(coneT)
            return Hit(intersection, normal, ray, coneT, material)
        }

        return null
    }

    private fun calculateNormal(point: Vector3): Vector3 {
        // find point X so that AXP is a right triangle with the right angle at point P and construct the normal as P-X
        val b = (apex - point).length()
        val c = b / cos(angle)
        val x = apex + c*normalizedAxis
        return point - x
    }

    private fun getClosestPointOnAxis(point: Vector3): Vector3 {
        // create plane from axis and point and find its intersection with the axis
        val d = point dot axis
        val t = (d - (apex dot axis)) / (axis dot axis)
        return apex + t*axis
    }

    override fun translate(offset: Vector3): Cone {
        return Cone(center + offset, apex + offset, radius, material)
    }

    override fun scale(factor: Double): Cone {
        val c = center + (apex - center)/2
        val scaledCenter = c + (apex - center)*factor
        val scaledApex = c + (center - apex)*factor

        return Cone(scaledCenter, scaledApex, radius * factor, material)
    }

    override fun rotateX(angle: Double): Cone {
        return rotate(Vector3::rotateX, angle)
    }

    override fun rotateY(angle: Double): Cone {
        return rotate(Vector3::rotateY, angle)
    }

    override fun rotateZ(angle: Double): Cone {
        return rotate(Vector3::rotateZ, angle)
    }

    private fun rotate(function: Vector3.(Double) -> Vector3, angle: Double): Cone {
        // offset to rotate around origin
        val c = center + (apex - center)/2

        // move disk centers so that cylinder center is at origin, rotate, move back
        var rotatedCenter = center - c
        rotatedCenter = rotatedCenter.function(angle)
        rotatedCenter += c

        var rotatedApex = apex - c
        rotatedApex = rotatedApex.function(angle)
        rotatedApex += c

        return Cone(rotatedCenter, rotatedApex, radius, material)
    }
}

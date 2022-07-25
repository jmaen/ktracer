package objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

import models.*
import materials.Material

@Serializable
@SerialName("triangle")
class Triangle(private val vertex0: Vector3, private val vertex1: Vector3, private val vertex2: Vector3, private val material: Material) : Hittable {
    @Transient
    private val normal = (vertex1 - vertex0) cross (vertex2 - vertex0)
    @Transient
    private val plane = Plane(vertex0, normal, material)
    @Transient
    private val edge0 = vertex1 - vertex0
    @Transient
    private val edge1 = vertex2 - vertex1
    @Transient
    private val edge2 = vertex0 - vertex2

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val hit = plane.hit(ray, tMin, tMax)

        // if plane is hit, check if point is inside the triangle
        if(hit != null) {
            if(checkTriangle(hit.point)) {
                return hit
            }
        }

        return null
    }

    override fun checkPoint(point: Vector3): Boolean {
        return plane.checkPoint(point) && checkTriangle(point)
    }

    fun checkTriangle(point: Vector3): Boolean {
        // check if point is inside the triangle
        val c0 = point - vertex0
        val c1 = point - vertex1
        val c2 = point - vertex2
        if(normal dot (edge0 cross c0) > 0 &&
            normal dot (edge1 cross c1) > 0 &&
            normal dot (edge2 cross c2) > 0) {
            return true
        }

        return false
    }
}
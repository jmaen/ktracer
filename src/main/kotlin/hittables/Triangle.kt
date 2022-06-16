package hittables

import geometry.*
import shading.Material

class Triangle(private val vertex0: Vector3, private val vertex1: Vector3, private val vertex2: Vector3, material: Material) : Hittable {
    private val normal = (vertex1 - vertex0) cross (vertex2 - vertex0)
    private val plane = Plane(vertex0, normal, material)
    private val edge0 = vertex1 - vertex0
    private val edge1 = vertex2 - vertex1
    private val edge2 = vertex0 - vertex2

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val hit = plane.hit(ray, tMin, tMax)

        // if plane is hit, check if point is in triangle
        if(hit != null) {
            val c0 = hit.point - vertex0
            val c1 = hit.point - vertex1
            val c2 = hit.point - vertex2
            if(normal dot (edge0 cross c0) > 0 &&
                normal dot (edge1 cross c1) > 0 &&
                normal dot (edge2 cross c2) > 0) {
                return hit
            }
        }

        return null
    }
}
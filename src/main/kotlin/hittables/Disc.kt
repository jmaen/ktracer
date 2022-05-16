package hittables

import geometry.*
import shading.Material

class Disc(private val center: Vector3, normal: Vector3, private val radius: Double, material: Material) : Hittable {
    private val plane: Plane = Plane(center, normal, material)

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val hit = plane.hit(ray, tMin, tMax)
        if(hit != null) {
            val distance = (hit.point - center).length()
            if(distance <= radius) {
                return hit
            }
        }

        return null
    }
}
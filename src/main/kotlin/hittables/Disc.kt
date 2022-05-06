package hittables

import image.Color
import models.*

class Disc(private val center: Vector3, normal: Vector3, private val radius: Double, color: Color) : Plane(center, normal, color) {
    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val hit = super.hit(ray, tMin, tMax)
        if(hit != null) {
            val distance = (hit.point - center).length()
            if(distance <= radius) {
                return hit
            }
        }

        return null
    }
}
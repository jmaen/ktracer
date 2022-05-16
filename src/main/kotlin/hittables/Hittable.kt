package hittables

import geometry.*

interface Hittable {
    fun hit(ray: Ray, tMin: Double, tMax: Double): Hit?
}
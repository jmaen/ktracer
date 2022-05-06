package hittables

import models.*

interface Hittable {
    fun hit(ray: Ray, tMin: Double, tMax: Double): Hit?
}
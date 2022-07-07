package objects

import kotlinx.serialization.Serializable

import models.*

@Serializable
sealed interface Hittable {
    fun hit(ray: Ray, tMin: Double, tMax: Double): Hit?

    fun checkPoint(point: Vector3): Boolean
}
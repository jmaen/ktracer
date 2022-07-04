package hittables

import kotlinx.serialization.Serializable

import geometry.*

@Serializable
sealed interface Hittable {
    fun hit(ray: Ray, tMin: Double, tMax: Double): Hit?

    fun checkPoint(point: Vector3): Boolean
}
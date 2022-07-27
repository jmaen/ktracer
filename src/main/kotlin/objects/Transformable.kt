package objects

import kotlinx.serialization.Serializable

import models.Vector3

@Serializable
sealed interface Transformable : Hittable {
    fun translate(offset: Vector3): Transformable

    fun scale(factor: Double): Transformable

    fun rotateX(angle: Double): Transformable

    fun rotateY(angle: Double): Transformable

    fun rotateZ(angle: Double): Transformable
}
package objects

import kotlinx.serialization.Serializable

import models.Rotation3
import models.Vector3

@Serializable
sealed interface Transformable : Hittable {
    fun translate(translate: Vector3): Transformable

    fun rotate(rotate: Rotation3): Transformable

    fun scale(scale: Double): Transformable
}
package lights

import kotlinx.serialization.Serializable

import models.Vector3

@Serializable
sealed interface PositionalLight {
    fun getPoints(): List<Vector3>
}
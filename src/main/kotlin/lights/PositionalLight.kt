package lights

import kotlinx.serialization.Serializable
import models.Color

import models.Vector3

@Serializable
sealed class PositionalLight {
    abstract val color: Color
    abstract val intensity: Double

    abstract fun getPoints(): List<Vector3>
}
package models

import kotlinx.serialization.Serializable

@Serializable
data class Rotation(val axis: String, val angle: Double) {
    init {
        if(axis !in listOf("x", "y", "z")) {
            throw IllegalArgumentException("Axis has to be one of {\"x\", \"y\", \"z\"}.")
        }
        if(angle < 0 || angle > 360) {
            throw IllegalArgumentException("Angle has to be in [0, 360].")
        }
    }
}
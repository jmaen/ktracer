package models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Rotation(val axis: Axis, val angle: Double) {
    init {
        if(angle < 0 || angle > 360) {
            throw IllegalArgumentException("Angle has to be in [0, 360].")
        }
    }

    @Serializable
    enum class Axis {
        @SerialName("x") X,
        @SerialName("y") Y,
        @SerialName("z") Z
    }
}
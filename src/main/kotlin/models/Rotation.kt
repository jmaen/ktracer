package models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Rotation(val axis: Axis, val angle: Double) {
    init {
        require(angle in 0.0..360.0) { "Angle has to be in [0, 360]." }
    }

    @Serializable
    enum class Axis {
        @SerialName("x") X,
        @SerialName("y") Y,
        @SerialName("z") Z
    }
}

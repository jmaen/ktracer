package models

import kotlin.math.PI
import kotlinx.serialization.Serializable

@Serializable
data class Rotation3(val x: Double, val y: Double, val z: Double) {
    constructor(x: Int, y: Int, z: Int) : this(x * (PI / 180), y * (PI / 180), z * (PI / 180))

    companion object {
        val ZERO = Rotation3(0.0, 0.0, 0.0)
    }
}
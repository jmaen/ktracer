package util

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

import geometry.Vector3
import shading.Color

operator fun Int.times(other: Vector3): Vector3 {
    return other * this
}

operator fun Double.times(other: Vector3): Vector3 {
    return other * this
}

operator fun Double.times(other: Color): Color {
    return other * this
}

fun Int.pow(n: Int): Double {
    return this.toDouble().pow(n)
}

fun Double.clamp(lower: Double, upper: Double): Double {
    return min(upper, max(lower, this))
}
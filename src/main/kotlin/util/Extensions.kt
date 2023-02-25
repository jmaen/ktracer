package util

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round

import models.Vector3
import models.Color

operator fun Int.times(other: Vector3): Vector3 {
    return other * this
}

operator fun Double.times(other: Vector3): Vector3 {
    return other * this
}

operator fun Int.times(other: Color): Color {
    return other * this
}

operator fun Double.times(other: Color): Color {
    return other * this
}

fun Int.pow(n: Int): Double {
    return this.toDouble().pow(n)
}

fun Int.pow(x: Double): Double {
    return this.toDouble().pow(x)
}

fun Double.clamp(lower: Double, upper: Double): Double {
    return min(upper, max(lower, this))
}

fun Double.roundTo(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

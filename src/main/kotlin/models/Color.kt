package models

import kotlin.math.pow
import kotlin.math.round
import kotlinx.serialization.Serializable

import util.*

@Serializable
data class Color(var red: Double, var green: Double, var blue: Double) {
    init {
        if(red < 0 || green < 0 || blue < 0) {
            throw IllegalArgumentException("All color components have to be >= 0.")
        }
    }

    constructor(r: Int, g: Int, b: Int) : this(r / 255.0, g / 255.0, b / 255.0)
    constructor(c: Double) : this(c, c, c)
    constructor(c: Int) : this(c, c, c)

    operator fun plus(other: Color): Color {
        return Color(red + other.red, green + other.green, blue + other.blue)
    }

    operator fun times(other: Int): Color {
        return Color(red * other, green * other, blue * other)
    }

    operator fun times(other: Double): Color {
        return Color(red * other, green * other, blue * other)
    }

    operator fun times(other: Color): Color {
        return Color(red * other.red, green * other.green, blue * other.blue)
    }

    operator fun div(other: Int): Color {
        return Color(red / other, green / other, blue / other)
    }

    operator fun div(other: Double): Color {
        return Color(red / other, green / other, blue / other)
    }

    operator fun div(other: Color): Color {
        return Color(red / other.red, green / other.green, blue / other.blue)
    }

    fun clamp(): Color {
        return Color(red.clamp(0.0, 1.0), green.clamp(0.0, 1.0), blue.clamp(0.0, 1.0))
    }

    fun gammaCorrect(gamma: Double = 2.0): Color {
        return Color(red.pow(1 / gamma), green.pow(1 / gamma), blue.pow(1 / gamma))
    }

    fun toRGB(): Int {
        var rgb = round(red * 255).toInt()
        rgb = (rgb shl 8) + round(green * 255).toInt()
        rgb = (rgb shl 8) + round(blue * 255).toInt()
        return rgb
    }

    override fun toString(): String {
        return "($red, $green, $blue)"
    }

    companion object {
        val BLACK = Color(0.0, 0.0, 0.0)
        val WHITE = Color(1.0, 1.0, 1.0)

        fun fromRGB(rgb: Int): Color {
            val mask = 0xFF
            val blue = rgb and mask
            val green = (rgb shr 8) and mask
            val red = (rgb shr 16) and mask
            return Color(red, green, blue)
        }
    }
}
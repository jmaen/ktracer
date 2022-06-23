package shading

import util.clamp
import kotlin.math.round

data class Color(var red: Double, var green: Double, var blue: Double) {
    init {
        red.clamp(0.0, 1.0)
        green.clamp(0.0, 1.0)
        blue.clamp(0.0, 1.0)
    }

    constructor(r: Int, g: Int, b: Int) : this(r / 255.0, g / 255.0, b / 255.0)
    constructor(c: Double) : this(c, c, c)
    constructor(c: Int) : this(c, c, c)

    operator fun plus(other: Color): Color {
        return Color(red + other.red, green + other.green, blue + other.blue)
    }
    operator fun times(other: Double): Color {
        return Color(red * other, green * other, blue * other)
    }

    operator fun times(other: Color): Color {
        return Color(red * other.red, green * other.green, blue * other.blue)
    }

    operator fun div(other: Double): Color {
        return Color(red / other, green / other, blue / other)
    }

    operator fun div(other: Color): Color {
        return Color(red / other.red, green / other.green, blue / other.blue)
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
    }
}
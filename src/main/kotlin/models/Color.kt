package models

import kotlin.math.round

data class Color(val red: Int, val green: Int, val blue: Int) {
    operator fun times(other: Double): Color {
        val r = round(red * other).toInt()
        val g = round(green * other).toInt()
        val b = round(blue * other).toInt()
        return Color(r, g, b)
    }

    fun toRGB(): Int {
        var rgb = red
        rgb = (rgb shl 8) + green
        rgb = (rgb shl 8) + blue
        return rgb
    }
}
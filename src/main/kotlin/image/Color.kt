package image

data class Color(val red: Int, val green: Int, val blue: Int) {
    fun toRGB(): Int {
        var rgb = red
        rgb = (rgb shl 8) + green
        rgb = (rgb shl 8) + blue
        return rgb
    }
}
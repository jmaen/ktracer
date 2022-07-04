package shading

import kotlinx.serialization.Serializable

import util.*

@Serializable
data class Material(val ambient: Color, val diffuse: Color, val specular: Color, val shininess: Double, val reflectiveness: Double) {
    constructor(diffuse: Color) : this(0.1 * diffuse, diffuse, Color.WHITE, 16.0, 0.0)
}
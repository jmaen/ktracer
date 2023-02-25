package materials

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*

@Serializable
@SerialName("emissive")
class Emissive(private val color: Color, private val intensity: Double) : Material() {
    init {
        require(intensity >= 0) { "Intensity has to be >= 0." }
    }

    override fun bsdf(hit: Hit): Sample? = null

    override fun emit(hit: Hit): Color {
        return color * intensity
    }
}

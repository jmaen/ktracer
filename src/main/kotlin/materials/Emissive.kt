package materials

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*

@Serializable
@SerialName("emissive")
class Emissive(private val color: Color, private val strength: Double) : Material() {
    override fun bsdf(hit: Hit): Sample? {
        return null
    }

    override fun emit(hit: Hit): Color {
        return color * strength
    }
}
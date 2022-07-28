package materials

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*

@Serializable
@SerialName("glass")
class Glass(private val color: Color, private val roughness: Double, private val IOR: Double) : Material() {
    init {
        if(roughness < 0 || roughness > 1) {
            throw IllegalArgumentException("Roughness has to be in [0, 1].")
        }
    }

    override fun bsdf(hit: Hit): Sample? {
        TODO("Not yet implemented")
    }
}
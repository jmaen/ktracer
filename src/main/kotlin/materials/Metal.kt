package materials

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*
import util.randomOnSphere

@Serializable
@SerialName("metal")
class Metal(private val color: Color, private val roughness: Double = 0.0) : Material() {
    init {
        if(roughness < 0 || roughness > 1) {
            throw IllegalArgumentException("Roughness has to be in [0, 1].")
        }
    }

    override fun bsdf(hit: Hit): Sample {
        var reflected = hit.ray.direction.reflect(hit.normal)

        // fuzzy reflection
        if(roughness != 0.0) {
            reflected += randomOnSphere(roughness)
        }

        return Sample(Ray(hit.point, reflected), color)
    }
}
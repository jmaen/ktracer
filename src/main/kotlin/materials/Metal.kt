package materials

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*
import util.*

@Serializable
@SerialName("metal")
class Metal(private val color: Color, private val roughness: Double = 0.0) : Material() {
    init {
        require(roughness in 0.0..1.0) { "Roughness has to be in [0, 1]." }
    }

    override fun bsdf(hit: Hit): Sample {
        var reflected = hit.ray.direction.reflect(hit.normal)

        // fuzzy reflection
        if(roughness != 0.0) {
            reflected += randomInSphere(roughness)
        }

        return Sample(Ray(hit.point, reflected), color)
    }
}

package materials

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*
import util.randomInSphere

@Serializable
@SerialName("metal")
class Metal(private val color: Color, private val roughness: Double) : Material() {
    override fun bsdf(hit: Hit): Sample? {
        var direction = hit.ray.direction.reflected(hit.normal)
        if(roughness != 0.0) {
            direction += randomInSphere(roughness)
        }

        return Sample(Ray(hit.point, direction), color)
    }
}
package materials

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*
import util.randomInDisk

@Serializable
@SerialName("metal")
class Metal(private val color: Color, private val roughness: Double) : Material() {
    override fun bsdf(hit: Hit): Sample? {
        var direction = hit.ray.direction.reflected(hit.normal)
        if(roughness != 0.0) {
            direction += randomInDisk(hit.normal, roughness)
        }

        return Sample(Ray(hit.point, direction), color)
    }
}
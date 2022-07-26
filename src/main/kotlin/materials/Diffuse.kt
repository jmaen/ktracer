package materials

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*
import util.randomOnSphere

@Serializable
@SerialName("diffuse")
class Diffuse(private val color: Color) : Material() {
    override fun bsdf(hit: Hit): Sample? {
        val direction = hit.normal + randomOnSphere(1.0)

        return Sample(Ray(hit.point, direction), color)
    }
}
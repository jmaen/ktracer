package materials

import kotlin.random.Random
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*
import util.randomOnSphere
import java.text.Normalizer
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
@SerialName("glass")
class Glass(private val color: Color, private val roughness: Double = 0.0, private val ior: Double = 1.4) : Material() {
    init {
        require(roughness in 0.0..1.0) { "Roughness has to be in [0, 1]." }
    }

    override fun bsdf(hit: Hit): Sample {
        val direction = hit.ray.direction
        var normal = hit.normal

        // check if the ray enters or exits the object, adjust refraction ratio and normal direction accordingly
        val refractionRatio: Double
        if(direction.enters(normal)) {
            refractionRatio = 1 / ior
        } else {
            refractionRatio = ior
            normal *= -1
        }

        // determine if ray is reflected or refracted
        val refracted = direction.refract(normal, refractionRatio)
        var scattered = if(refracted == null || Random.nextDouble() < schlick(direction, normal, refractionRatio)) {
            direction.reflect(normal)
        } else {
            refracted
        }

        // fuzzy reflection
        if(roughness != 0.0) {
            scattered += randomOnSphere(roughness)
        }

        return Sample(Ray(hit.point, scattered), color)
    }

    private fun schlick(direction: Vector3, normal: Vector3, refractionRatio: Double): Double {
        val cosTheta = min(-direction dot normal, 1.0)
        var r0 = (1 - refractionRatio) / (1 + refractionRatio)
        r0 *= r0
        return r0 + (1 - r0)*(1 - cosTheta).pow(5)
    }
}

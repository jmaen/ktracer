package lights

import kotlin.math.*
import kotlin.random.Random
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*

@Serializable
@SerialName("disk")
class DiskLight(val point: Vector3,
                val normal: Vector3,
                val radius: Double,
                override val color: Color,
                override val intensity: Double,
                private val sampleDensity: Int) : PositionalLight() {
    // calculate sample count from area and density
    private val sampleCount = ceil(PI * radius*radius * sampleDensity).toInt()
    // calculate two perpendicular vectors both perpendicular to normal
    private val u = normal cross Vector3(1, 1, 1)
    private val v = u cross normal

    override fun getPoints(): List<Vector3> {
        val samples = mutableListOf<Vector3>()
        for(i in 0 until sampleCount) {
            // calculate random point on disk
            val r = radius * sqrt(Random.nextDouble())
            val theta = Random.nextDouble(2 * PI)
            val offset = u*r*sin(theta) + v*r*cos(theta)
            val sample = point + offset
            samples.add(sample)
        }
        return samples
    }
}
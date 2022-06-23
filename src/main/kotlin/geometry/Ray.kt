package geometry

import util.*

data class Ray(val origin: Vector3, var direction: Vector3) {
    init {
        instanceCount++
        direction = direction.normalized()
    }

    fun pointAt(t: Double): Vector3 {
        return origin + t*direction
    }

    companion object {
        var instanceCount: Long = 0
    }
}
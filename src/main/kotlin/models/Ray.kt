package models

import java.util.concurrent.atomic.AtomicLong

import util.*

data class Ray(val origin: Vector3, var direction: Vector3) {
    init {
        instanceCount.incrementAndGet()
        direction = direction.normalized()
    }

    fun pointAt(t: Double): Vector3 {
        return origin + t*direction
    }

    companion object {
        var instanceCount = AtomicLong(0)
    }
}

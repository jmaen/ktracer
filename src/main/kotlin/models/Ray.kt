package models

class Ray(val origin: Vector3, var direction: Vector3) {
    init {
        direction = direction.normalized()
    }

    fun pointAt(t: Double): Vector3 {
        return origin + t*direction
    }
}
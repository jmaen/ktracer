package models

class Ray(val origin: Vector3, direction: Vector3) {
    val direction = direction.normalized()

    fun pointAt(t: Double): Vector3 {
        return origin + t*direction
    }
}
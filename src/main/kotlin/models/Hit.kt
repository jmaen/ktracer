package models

data class Hit(val point: Vector3, var normal: Vector3, val t: Double, val color: Color) {
    init {
        normal = normal.normalized()
    }
}
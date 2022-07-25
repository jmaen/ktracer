package models

import materials.Material

data class Hit(val point: Vector3, var normal: Vector3, val ray: Ray, val t: Double, val material: Material) {
    init {
        normal = normal.normalized()
    }
}
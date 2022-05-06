package models

operator fun Int.times(other: Vector3): Vector3 {
    return other * this
}

operator fun Double.times(other: Vector3): Vector3 {
    return other * this
}
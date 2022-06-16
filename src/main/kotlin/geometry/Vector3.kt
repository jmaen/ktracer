package geometry

import kotlin.math.sqrt

data class Vector3(val x: Double, val y: Double, val z: Double) {
    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor(x: Int, y: Int, z: Double) : this(x.toDouble(), y.toDouble(), z)
    constructor(x: Int, y: Double, z: Int) : this(x.toDouble(), y, z.toDouble())
    constructor(x: Int, y: Double, z: Double) : this(x.toDouble(), y, z)
    constructor(x: Double, y: Int, z: Int) : this(x, y.toDouble(), z.toDouble())
    constructor(x: Double, y: Int, z: Double) : this(x, y.toDouble(), z)
    constructor(x: Double, y: Double, z: Int) : this(x, y, z.toDouble())

    operator fun unaryMinus(): Vector3 {
        return Vector3(-x, -y, -z)
    }

    operator fun plus(other: Vector3): Vector3 {
        return Vector3(x + other.x, y + other.y, z + other.z)
    }

    operator fun minus(other: Vector3): Vector3 {
        return this + (-other)
    }

    operator fun times(other: Int): Vector3 {
        return Vector3(x * other, y * other, z * other)
    }

    operator fun times(other: Double): Vector3 {
        return Vector3(x * other, y * other, z * other)
    }

    operator fun div(other: Int): Vector3 {
        return Vector3(x / other, y / other, z / other)
    }

    operator fun div(other: Double): Vector3 {
        return Vector3(x / other, y / other, z / other)
    }

    infix fun dot(other: Vector3): Double {
        return x*other.x + y*other.y + z*other.z
    }

    infix fun cross(other: Vector3): Vector3 {
        val crossX = y*other.z - z*other.y
        val crossY = z*other.x - x*other.z
        val crossZ = x*other.y - y*other.x
        return Vector3(crossX, crossY, crossZ)
    }

    fun length(): Double {
        return sqrt(this dot this)
    }

    fun normalized(): Vector3 {
        return this / length()
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }
}
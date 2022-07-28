package models

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.serialization.Serializable

import util.*
import kotlin.random.Random

@Serializable
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

    fun reflected(normal: Vector3): Vector3 {
        return this - 2*(this dot normal)*normal
    }

    fun rotateX(theta: Double): Vector3 {
        val rotatedX = x
        val rotatedY = y*cos(theta) - z*sin(theta)
        val rotatedZ = y*sin(theta) + z*cos(theta)
        return Vector3(rotatedX, rotatedY, rotatedZ)
    }

    fun rotateY(theta: Double): Vector3 {
        val rotatedX = x*cos(theta) + z*sin(theta)
        val rotatedY = y
        val rotatedZ = -x*sin(theta) + z*cos(theta)
        return Vector3(rotatedX, rotatedY, rotatedZ)
    }

    fun rotateZ(theta: Double): Vector3 {
        val rotatedX = x*cos(theta) - y*sin(theta)
        val rotatedY = x*sin(theta) + y*cos(theta)
        val rotatedZ = z
        return Vector3(rotatedX, rotatedY, rotatedZ)
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }

    companion object {
        val ZERO = Vector3(0, 0, 0)
        val ONE = Vector3(1, 1, 1)

        fun random(min: Double, max: Double): Vector3 {
            return Vector3(Random.nextDouble(min, max), Random.nextDouble(min, max), Random.nextDouble(min, max))
        }
    }
}
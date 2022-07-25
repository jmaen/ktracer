package util

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

import models.Vector3

fun randomInDisk(normal: Vector3, radius: Double): Vector3 {
    // find two unit vectors perpendicular to the normal and to each other
    val u = (normal cross Vector3(1, 1, 1)).normalized()
    val v = (normal cross u).normalized()
    val r = radius * sqrt(Random.nextDouble())
    val theta = Random.nextDouble(2 * PI)
    return u*r*sin(theta) + v*r*cos(theta)
}

fun randomInXYDisk(radius: Double): Vector3 {
    // special case for a disk on the XY-plane
    val u = Vector3(1, 0, 0)
    val v = Vector3(0, 1, 0)
    val r = radius * sqrt(Random.nextDouble())
    val theta = Random.nextDouble(2 * PI)
    return u*r*sin(theta) + v*r*cos(theta)
}


fun randomInSphere(radius: Double): Vector3 {
    while(true) {
        val random = Vector3.random(-radius, radius)
        if(random.length() > 1) {
            continue
        }
        return random
    }
}

fun randomOnSphere(radius: Double): Vector3 {
    return randomInSphere(radius).normalized()
}
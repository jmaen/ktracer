package util

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

import models.Vector3

fun randomInXYDisk(radius: Double): Vector3 {
    val u = Vector3(1, 0, 0)
    val v = Vector3(0, 1, 0)
    val r = radius * sqrt(Random.nextDouble())
    val theta = Random.nextDouble(2 * PI)
    return u*r*sin(theta) + v*r*cos(theta)
}


fun randomInSphere(radius: Double): Vector3 {
    while(true) {
        val random = Vector3.random(-1.0, 1.0)
        if(random.length() > 1) {
            continue
        }
        return radius * random
    }
}

fun randomOnSphere(radius: Double): Vector3 {
    return randomInSphere(radius).normalized()
}

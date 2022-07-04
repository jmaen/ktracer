package shading

import kotlinx.serialization.Serializable

import geometry.Vector3

@Serializable
data class PointLight(val point: Vector3, val color: Color, val intensity: Double)
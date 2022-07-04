package shading

import kotlinx.serialization.Serializable

@Serializable
data class GlobalLight(val color: Color, val intensity: Double)
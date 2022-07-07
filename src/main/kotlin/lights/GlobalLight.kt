package lights

import kotlinx.serialization.Serializable

import models.Color

@Serializable
data class GlobalLight(val color: Color, val intensity: Double)
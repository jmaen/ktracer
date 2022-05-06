package models

import image.Color

data class Hit(val point: Vector3, val normal: Vector3, val t: Double, val color: Color)
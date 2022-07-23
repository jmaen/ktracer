package materials

import models.Color
import models.Hit

class Diffuse(private val color: Color) : Material() {
    override fun bsdf(hit: Hit): Color {
        TODO("Not yet implemented")
    }
}
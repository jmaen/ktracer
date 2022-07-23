package materials

import models.Color
import models.Hit

class Emissive : Material() {
    override fun bsdf(hit: Hit): Color {
        TODO("Not yet implemented")
    }

    override fun emit(hit: Hit): Color {
        TODO("Not yet implemented")
    }
}
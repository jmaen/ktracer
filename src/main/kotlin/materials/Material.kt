package materials

import models.*

abstract class Material {
    abstract fun bsdf(hit: Hit): Color

    open fun emit(hit: Hit): Color {
        return Color.BLACK
    }
}
package materials

import kotlinx.serialization.Serializable

import models.*

@Serializable
sealed class Material {
    abstract fun bsdf(hit: Hit): Sample?

    open fun emit(hit: Hit): Color = Color.BLACK
}

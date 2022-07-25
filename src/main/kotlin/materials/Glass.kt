package materials

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import models.*

@Serializable
@SerialName("glass")
class Glass : Material() {
    override fun bsdf(hit: Hit): Sample? {
        TODO("Not yet implemented")
    }
}
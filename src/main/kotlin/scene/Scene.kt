package scene

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round

import geometry.*
import hittables.*
import shading.Color
import shading.GlobalLight
import shading.PointLight


class Scene(
    private val camera: Camera,
    private val hittables: List<Hittable>,
    private val lights: List<PointLight>,
    private val globalLight: GlobalLight = GlobalLight(Color.WHITE, 0.1),
    private val voidColor: Color = Color.BLACK,
    private val renderDistance: Double = 50.0,
    private val maxDepth: Int = 5) {

    fun render(): Image {
        // compute image dimensions, initialize image array with background color
        val imageWidth = (camera.canvasWidth * camera.pixelsPerUnit).toInt()
        val imageHeight = (camera.canvasHeight * camera.pixelsPerUnit).toInt()
        val image: Array<Array<Color>> = Array(imageWidth) { _ -> Array(imageHeight) { _ -> voidColor } }

        val onePercent = round(imageWidth / 100.0).toInt()
        for(x in image.indices) {
            // status bar
            if(x % onePercent == 0) {
                print("Rendering: [")
                for(i in 0..19) {
                    val c = if(x >= i * onePercent * 5) '=' else ' '
                    print(c)
                }
                print("] (${round(x.toDouble() / imageWidth * 100)}%)\r")
            }

            for(y in image[0].indices) {
                // calculate 3d point for pixel
                val horizontal = (x.toDouble() / imageWidth) * Vector3(camera.canvasWidth, 0.0, 0.0)
                val vertical = (y.toDouble() / imageHeight) * Vector3(0.0, camera.canvasHeight, 0.0)
                val point = camera.canvasOrigin + horizontal + vertical

                // determine the nearest hit (if any)
                val ray = Ray(camera.point, point - camera.point)
                val nearestHit = trace(ray)

                // if there is a hit, calculate shading
                if(nearestHit != null) {
                    image[x][y] = shade(nearestHit, 0)
                }
            }
        }
        println("Rendering: [====================] (100%)")
        println("Finished rendering!")

        return Image(image)
    }

    private fun trace(ray: Ray): Hit? {
        // find the nearest object hit by the ray
        var nearestHit: Hit? = null
        for(hittable in hittables) {
            val hit = hittable.hit(ray, 0.0, renderDistance)
            if(hit != null && (nearestHit == null || hit.t < nearestHit.t)) {
                nearestHit = hit
            }
        }

        return nearestHit
    }

    private fun shade(hit: Hit, depth: Int): Color {
        val (ambientColor, diffuseColor, specularColor, shininess, reflectiveness) = hit.material
        // use ambient colors to account for environment light
        var color = ambientColor * globalLight.color * globalLight.intensity

        for (light in lights) {
            val lightDirection = light.point - hit.point

            // check if hittable is between intersection and light source
            val ray = Ray(hit.point + hit.normal*0.0001, lightDirection)  // add offset to hit so it does not hit itself
            var isShadowed = false
            for (hittable in hittables) {
                if(hittable.hit(ray, 0.0, lightDirection.length()) != null) {  // only search for hittables in light distance
                    isShadowed = true
                    break
                }
            }

            if(!isShadowed) {
                // calculate light with respect to light falloff
                val illumination = light.color * (light.intensity / (lightDirection.length()*lightDirection.length()))

                // lambertian shading
                val diffuse = diffuseColor * illumination * max(0.0, hit.normal dot lightDirection.normalized())

                // specular shading (Blinn-Phong)
                val cameraDirection = -hit.ray.direction
                val bisector = (lightDirection.normalized() + cameraDirection).normalized()
                val specular = specularColor * illumination * max(0.0, hit.normal dot bisector).pow(shininess)

                color += diffuse + specular
            }
        }

        // recursively calculate reflection
        if(depth < maxDepth && reflectiveness != 0.0) {
            val v = hit.ray.direction
            val n = hit.normal
            val reflection = v - 2*(v dot n)*n
            val reflectedHit = trace(Ray(hit.point + hit.normal*0.0001, reflection))
            if(reflectedHit != null) {
                color += shade(reflectedHit, depth + 1) * reflectiveness
            }
        }

        return color
    }
}
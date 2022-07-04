package scene

import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round
import kotlin.system.measureTimeMillis
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import geometry.*
import hittables.*
import shading.*
import util.*

@Serializable
class Scene(
    private val camera: Camera,
    private val hittables: List<Hittable>,
    private val lights: List<PointLight>,
    private val globalLight: GlobalLight = GlobalLight(Color.WHITE, 0.1),
    private val voidColor: Color = Color.BLACK,
    private val renderDistance: Double = 50.0,
    private val maxDepth: Int = 5) {
    private val horizontalVector = Vector3(camera.canvasWidth, 0.0, 0.0)
    private val verticalVector = Vector3(0.0, camera.canvasHeight, 0.0)

    fun render(): Image {
        // compute image dimensions, initialize image array with background color
        val imageWidth = (camera.canvasWidth * camera.pixelsPerUnit).toInt()
        val onePercent = round(imageWidth / 100.0).toInt()
        val imageHeight = (camera.canvasHeight * camera.pixelsPerUnit).toInt()
        val image: Array<Array<Color>> = Array(imageWidth) { Array(imageHeight) { voidColor } }

        val millis = measureTimeMillis {
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
                    var red = 0.0
                    var green = 0.0
                    var blue = 0.0
                    val sf = camera.sampleFactor
                    for(i in 0 until sf) {
                        for(j in 0 until sf) {
                            // calculate offsets for supersampling
                            val offsetX = i.toDouble() / sf
                            val offsetY = j.toDouble() / sf

                            // calculate 3d point for pixel (nextDouble() < 1, so numerator is < x+1)
                            val horizontal = ((x.toDouble() + offsetX) / imageWidth)
                            val vertical = ((y.toDouble() + offsetY) / imageHeight)
                            val point = camera.canvasOrigin + horizontal*horizontalVector + vertical*verticalVector

                            // determine the nearest hit (if any)
                            val ray = Ray(camera.point, point - camera.point)
                            val nearestHit = trace(ray)

                            // if there is a hit, calculate shading
                            if (nearestHit != null) {
                                val (r, g, b) = shade(nearestHit, 0)
                                red += r
                                green += g
                                blue += b
                            }
                        }
                    }

                    // take average of all samples
                    val samples = sf.pow(2)
                    red /= samples
                    green /= samples
                    blue /= samples
                    image[x][y] = Color(red, green, blue)
                }
            }
        }
        println("Rendering: [====================] (100%)")
        // print summary
        val rays = String.format(Locale.US, "%,d", Ray.instanceCount)
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes)
        val time = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        println("Finished rendering! Spawned $rays rays in $time.")

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
            val reflection = hit.ray.direction.reflected(hit.normal)
            val reflectedHit = trace(Ray(hit.point + hit.normal*0.0001, reflection))
            if(reflectedHit != null) {
                color += shade(reflectedHit, depth + 1) * reflectiveness
            }
        }

        return color
    }

    fun save(path: String) {
        val format = Json { prettyPrint = true }
        val json = format.encodeToString(this)
        File(path).writeText(json)
    }

    companion object {
        fun load(path: String): Scene {
            val json = File(path).readText()
            return Json.decodeFromString(json)
        }
    }
}
package scene

import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.*
import kotlin.system.measureTimeMillis
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

import models.*
import objects.*
import util.*

@Serializable
class Scene(
    private val camera: Camera,
    private val objects: List<Hittable>,
    private val samplesPerRay: Int,
    private val maxBounces: Int,
    private val voidColor: Color = Color.BLACK,
    private val renderDistance: Double = 100.0) {
    private val horizontalVector = Vector3(camera.canvasWidth, 0.0, 0.0)
    private val verticalVector = Vector3(0.0, camera.canvasHeight, 0.0)

    init {
        if(samplesPerRay < 1) {
            throw IllegalArgumentException("There has to be at least one sample per ray.")
        }
        if(maxBounces < 1) {
            throw IllegalArgumentException("There has to be at least one bounce.")
        }
    }

    fun render(): Image {
        // compute image dimensions, initialize image array
        val imageWidth = (camera.canvasWidth * camera.pixelsPerUnit).toInt()
        val onePercent = round(imageWidth / 100.0).toInt()
        val imageHeight = (camera.canvasHeight * camera.pixelsPerUnit).toInt()
        val image: Array<Array<Color>> = Array(imageWidth) { Array(imageHeight) { Color.BLACK } }

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
                    var color = Color.BLACK
                    val sf = camera.superSamplingFactor
                    for(i in 0 until sf) {
                        for(j in 0 until sf) {
                            // calculate offsets for supersampling
                            val offsetX = i.toDouble() / sf
                            val offsetY = j.toDouble() / sf

                            // calculate 3d point for pixel
                            val horizontal = ((x.toDouble() + offsetX) / imageWidth)
                            val vertical = ((y.toDouble() + offsetY) / imageHeight)
                            val point = camera.canvasOrigin + horizontal*horizontalVector + vertical*verticalVector

                            // calculate focal point
                            val direction = (point - camera.point).normalized()
                            val focalPoint = camera.point + camera.focalLength*direction

                            // calculate shading
                            var sampleColor = Color.BLACK
                            for(n in 0 until samplesPerRay) {
                                // calculate random point on aperture disk
                                val origin = camera.point + randomInXYDisk(camera.aperture / 2)

                                // shade point
                                val ray = Ray(origin, focalPoint - origin)
                                sampleColor += shade(ray)
                            }

                            // take average of all samples
                            sampleColor /= samplesPerRay
                            color += sampleColor
                        }
                    }

                    // take average of all samples (supersampling)
                    color /= sf.pow(2)

                    image[x][y] = color.clamp()
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

    private fun shade(ray: Ray, bounces: Int = 0): Color {
        // no contribution when max depth is exceeded
        if(bounces >= maxBounces) {
            return Color.BLACK
        }

        // no hit returns the void color
        val hit = trace(ray)
        if(hit == null) {
            return voidColor
        }

        // recursively add contributions
        val emission = hit.material.emit(hit)
        val sample = hit.material.bsdf(hit)
        if(sample == null) {
            return emission
        } else {
            val (shattered, color) = sample
            return emission + color*shade(shattered, bounces + 1)
        }
    }

    private fun trace(ray: Ray): Hit? {
        // find the nearest object hit by the ray
        var nearestHit: Hit? = null
        for(hittable in objects) {
            val hit = hittable.hit(ray, 0.0001, renderDistance) // don't start at 0 to avoid shadow acne
            if(hit != null && (nearestHit == null || hit.t < nearestHit.t)) {
                nearestHit = hit
            }
        }

        return nearestHit
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
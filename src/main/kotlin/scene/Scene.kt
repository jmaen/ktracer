package scene

import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.fixedRateTimer
import kotlin.math.*
import kotlin.system.measureTimeMillis
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.*

import models.*
import objects.*
import util.*

@Serializable
class Scene(
    private val camera: Camera,
    private val objects: List<Hittable>,
    private val samples: Int,
    private val ssaaFactor: Int,
    private val maxBounces: Int,
    private val voidColor: Color = Color.BLACK,
    private val renderDistance: Double = 100.0) {
    @Transient
    private val renderedColumns = AtomicInteger(0)
    @Transient
    private var print = false

    init {
        if(samples < 1) {
            throw IllegalArgumentException("There has to be at least one sample per ray.")
        }
        if(maxBounces < 1) {
            throw IllegalArgumentException("There has to be at least one bounce.")
        }
        if(ssaaFactor < 1) {
            throw IllegalArgumentException("Supersampling factor has to be >= 1.")
        }
    }

    suspend fun render(threads: Int = Runtime.getRuntime().availableProcessors()): Image {
        // compute image dimensions, initialize image array
        val imageWidth = (camera.canvasWidth * camera.pixelsPerUnit).toInt()
        val imageHeight = (camera.canvasHeight * camera.pixelsPerUnit).toInt()
        val image: Array<Array<Color>> = Array(imageWidth) { Array(imageHeight) { Color.BLACK } }

        // store render parameters for image metadata
        val summary = mutableMapOf(
            "samples" to samples.toString(),
            "ssaaFactor" to ssaaFactor.toString(),
            "maxBounces" to maxBounces.toString())

        // status bar timer
        fixedRateTimer(daemon = true, period = 500) {
            print = true
        }

        coroutineScope {
            val millis = measureTimeMillis {
                // render parts asynchronously
                val partList = mutableListOf<Deferred<Array<Array<Color>>>>()
                val partSize = imageWidth / threads
                for(i in 0 until threads - 1) {
                    partList.add(async { renderPart(i * partSize, (i + 1) * partSize, imageWidth, imageHeight) })
                }
                partList.add(async { renderPart((threads - 1) * partSize, imageWidth, imageWidth, imageHeight) })

                // wait for all parts, copy parts into final image
                val parts = awaitAll(*partList.toTypedArray()).toTypedArray()
                for(i in 0 until threads) {
                    parts[i].copyInto(image, i * partSize)
                }
            }

            // print summary
            val rays = String.format(Locale.US, "%,d", Ray.instanceCount.toLong())
            summary["rays"] = rays

            val hours = TimeUnit.MILLISECONDS.toHours(millis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes)
            val renderTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            summary["renderTime"] = renderTime

            println("Rendering: [====================] (100%)")
            println("Finished rendering! Spawned $rays rays in $renderTime.")
        }

        return Image(image, summary)
    }

    private fun renderPart(from: Int, to: Int, imageWidth: Int, imageHeight: Int): Array<Array<Color>> {
        // initialize array
        val part = Array(to - from) { Array(imageHeight) { Color.BLACK } }

        // render part
        for(x in from until to) {
            // status bar
            val count = renderedColumns.incrementAndGet()
            if(print) {
                print = false
                printStatusBar(count.toDouble() / imageWidth * 100)
            }

            for(y in 0 until imageHeight) {
                var color = Color.BLACK
                for(i in 0 until ssaaFactor) {
                    for(j in 0 until ssaaFactor) {
                        // calculate offsets for supersampling
                        val offsetX = i.toDouble() / ssaaFactor
                        val offsetY = j.toDouble() / ssaaFactor

                        // calculate 3d point for pixel
                        val horizontal = ((x.toDouble() + offsetX) / imageWidth) * camera.canvasWidth
                        val vertical = ((y.toDouble() + offsetY) / imageHeight) * camera.canvasHeight
                        val point = camera.canvasOrigin + horizontal*Vector3.UNIT_X + vertical*Vector3.UNIT_Y

                        // calculate focal point
                        val direction = (point - camera.point).normalized()
                        val focalPoint = camera.point + camera.focalLength*direction

                        // calculate shading
                        var sampleColor = Color.BLACK
                        for(n in 0 until samples) {
                            // calculate random point on aperture disk
                            val origin = camera.point + randomInXYDisk(camera.aperture / 2)

                            // shade point
                            val ray = Ray(origin, focalPoint - origin)
                            sampleColor += shade(ray)
                        }

                        // take average of all samples
                        sampleColor /= samples
                        color += sampleColor
                    }
                }

                // take average of all samples (supersampling)
                color /= ssaaFactor * ssaaFactor

                part[x - from][y] = color.clamp()
            }
        }

        return part
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
            val (scattered, color) = sample
            return emission + color*shade(scattered, bounces + 1)
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

    private fun printStatusBar(percentage: Double) {
        print("Rendering: [")
        for(i in 1..20) {
            val c = if(percentage >= i * 5) '=' else ' '
            print(c)
        }
        print("] (${round(percentage * 100) / 100}%)\r")
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
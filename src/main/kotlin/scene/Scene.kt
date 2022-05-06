package scene

import kotlin.math.round
import hittables.Hittable
import image.Color
import models.*

class Scene(val camera: Camera, val hittables: List<Hittable>, val lights: List<Light>, val backgroundColor: Color) {
    fun render(): Array<Array<Color>> {
        // compute image dimensions, initialize image array
        val imageWidth = (camera.canvasWidth * camera.pixelsPerUnit).toInt()
        val imageHeight = (camera.canvasHeight * camera.pixelsPerUnit).toInt()
        val image: Array<Array<Color>> = Array(imageWidth) { _ -> Array(imageHeight) { _ -> backgroundColor} }

        val onePercent = round(imageWidth / 100.0).toInt()
        for(x in image.indices) {
            if(x % onePercent == 0) {
                print('[')
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
                val pixelRay = Ray(camera.point, point - camera.point)
                var nearestHit: Hit? = null
                for(hittable in hittables) {
                    val hit = hittable.hit(pixelRay, 0.0, 20.0)
                    if(hit != null && (nearestHit == null || hit.t < nearestHit.t)) {
                        nearestHit = hit
                    }
                }

                // check if intersection is lighted, assign color
                val intersection = nearestHit
                if(intersection != null) {
                    var color = Color(0, 0, 0)
                    for (light in lights) {
                        val direction = light.point - intersection.point
                        // add offset to intersection so it does not hit itself
                        val lightRay = Ray(intersection.point + intersection.normal*0.0001, direction)
                        var lighted = true
                        for (hittable in hittables) {
                            val hit = hittable.hit(lightRay, 0.0, direction.length())
                            if (hit != null) {
                                lighted = false
                                break
                            }
                        }

                        if(lighted) {
                            color = intersection.color
                            break
                        }
                    }
                    image[x][y] = color
                }
            }
        }
        println("[====================] (100%)")

        return image
    }
}
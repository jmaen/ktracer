package objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

import materials.Material
import models.*

@Serializable
@SerialName("box")
class Box(private val corner1: Vector3, private val corner2: Vector3, private val material: Material) : Transformable {
    @Transient
    private lateinit var faces: List<Polygon>

    init {
        val directions = listOf(
            Vector3(corner2.x - corner1.x, 0, 0),
            Vector3(0, corner2.y - corner1.y,0),
            Vector3(0, 0, corner2.z - corner1.z)
        )

        faces = calculateFaces(directions)
    }

    private fun calculateFaces(directions: List<Vector3>): List<Polygon> {
        val polygonList = mutableListOf<Polygon>()
        val vertexList = mutableListOf<Vector3>()
        var currentVertex: Vector3

        // calculate the 4 vertices per face and create corresponding polygon
        for(i in 0..2) {
            currentVertex = corner1
            vertexList.add(currentVertex)
            currentVertex += directions[i]
            vertexList.add(currentVertex)
            currentVertex += directions[(i + 1) % 3]
            vertexList.add(currentVertex)
            currentVertex -= directions[i]
            vertexList.add(currentVertex)

            polygonList.add(Polygon(*vertexList.toTypedArray(), material = material))
            vertexList.clear()
        }

        for(i in 0..2) {
            currentVertex = corner2
            vertexList.add(currentVertex)
            currentVertex -= directions[i]
            vertexList.add(currentVertex)
            currentVertex -= directions[(i + 1) % 3]
            vertexList.add(currentVertex)
            currentVertex += directions[i]
            vertexList.add(currentVertex)

            polygonList.add(Polygon(*vertexList.toTypedArray(), material = material))
            vertexList.clear()
        }

        return polygonList
    }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        // find the nearest face hit, if any
        var nearestHit: Hit? = null
        for(face in faces) {
            val hit = face.hit(ray, tMin, tMax)
            if(nearestHit == null || (hit != null && hit.t < nearestHit.t)) {
                nearestHit = hit
            }
        }

        return nearestHit
    }

    override fun translate(offset: Vector3): Box {
        return Box(corner1 + offset, corner2 + offset, material)
    }

    override fun scale(factor: Double): Box {
        val center = corner1 + (corner2 - corner1)/2
        val scaledCorner1 = center + (corner1 - center)*factor
        val scaledCorner2 = center + (corner2 - center)*factor

        return Box(scaledCorner1, scaledCorner2, material)
    }

    override fun rotateX(angle: Double): Box {
        return rotate(Vector3::rotateX, angle)
    }

    override fun rotateY(angle: Double): Box {
        return rotate(Vector3::rotateY, angle)
    }

    override fun rotateZ(angle: Double): Box {
        return rotate(Vector3::rotateZ, angle)
    }

    private fun rotate(function: Vector3.(Double) -> Vector3, angle: Double): Box {
        // offset to rotate around origin
        val center = corner1 + (corner2 - corner1) / 2

        // rotate all vertices, create new corresponding polygons
        val faceList = mutableListOf<Polygon>()
        val vertexList = mutableListOf<Vector3>()
        for(face in faces) {
            for(vertex in face.vertices) {
                // move vertex so that center is at origin, rotate, move back
                var currentVertex = vertex - center
                currentVertex = currentVertex.function(angle)
                currentVertex += center

                vertexList.add(currentVertex)
            }
            faceList.add(Polygon(*vertexList.toTypedArray(), material = material))
            vertexList.clear()
        }
        faces = faceList

        return this
    }
}

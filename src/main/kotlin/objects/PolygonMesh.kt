package objects

import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

import materials.Material
import models.*

@Serializable
@SerialName("mesh")
class PolygonMesh(private val source: String,
                  private val format: Format,
                  private val material: Material) : Transformable {
    @Transient
    private lateinit var faces: List<Polygon>
    @Transient
    private lateinit var min: Vector3
    @Transient
    private lateinit var max: Vector3

    init {
        faces = when(format) {
            Format.OBJ -> parseOBJ()
            Format.OFF -> parseOFF()
            Format.PLY -> parsePLY()
        }

        max = calculateMaxBound()
        min = calculateMinBound()
    }

    private fun parseOBJ(): List<Polygon> {
        val vertexList = mutableListOf<Vector3>()
        val faceList = mutableListOf<Polygon>()
        File(source).forEachLine {
            if(it.startsWith("v ")) {
                val coordinates = it.split(" ")
                vertexList.add(Vector3(coordinates[1].toDouble(), coordinates[2].toDouble(), coordinates[3].toDouble()))
            } else if(it.startsWith("f ")){
                val indices = it.split(" ")
                val vertices = mutableListOf<Vector3>()
                for(i in 1 until indices.size) {
                    vertices.add(vertexList[indices[i].toInt() - 1])
                }
                faceList.add(Polygon(*vertices.toTypedArray(), material = material))
            }
        }

        return faceList
    }

    private fun parseOFF(): List<Polygon> {
        TODO("Not yet implemented")
    }

    private fun parsePLY(): List<Polygon> {
        TODO("Not yet implemented")
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

    override fun translate(offset: Vector3): PolygonMesh {
        val faceList = mutableListOf<Polygon>()
        for(face in faces) {
            faceList.add(face.translate(offset))
        }
        faces = faceList

        max = calculateMaxBound()
        min = calculateMinBound()

        return this
    }

    override fun scale(factor: Double): PolygonMesh {
        val center = min + (max - min)/2

        val faceList = mutableListOf<Polygon>()
        for(face in faces) {
            val vertexList = mutableListOf<Vector3>()
            for(vertex in face.vertices) {
                vertexList.add(center + (vertex - center)*factor)
            }
            faceList.add(Polygon(*vertexList.toTypedArray(), material = material))
        }
        faces = faceList

        max = calculateMaxBound()
        min = calculateMinBound()

        return this
    }

    override fun rotateX(angle: Double): PolygonMesh {
        return rotate(Vector3::rotateX, angle)
    }

    override fun rotateY(angle: Double): PolygonMesh {
        return rotate(Vector3::rotateY, angle)
    }

    override fun rotateZ(angle: Double): PolygonMesh {
        return rotate(Vector3::rotateZ, angle)
    }

    private fun rotate(rotate: Vector3.(Double) -> Vector3, angle: Double): PolygonMesh {
        // offset to rotate around origin
        val center = min + (max - min)/2

        // move vertices so that center is at origin, rotate, move back
        val faceList = mutableListOf<Polygon>()
        for(face in faces) {
            val vertexList = mutableListOf<Vector3>()
            for(vertex in face.vertices) {
                var rotatedVertex = vertex - center
                rotatedVertex = rotatedVertex.rotate(angle)
                rotatedVertex += center
                vertexList.add(rotatedVertex)
            }
            faceList.add(Polygon(*vertexList.toTypedArray(), material = material))
        }
        faces = faceList

        max = calculateMaxBound()
        min = calculateMinBound()

        return this
    }

    private fun calculateMaxBound(): Vector3 {
        return calculateBound { value, max -> value > max }
    }

    private fun calculateMinBound(): Vector3 {
        return calculateBound { value, min -> value < min }
    }

    private fun calculateBound(check: (Double, Double) -> Boolean): Vector3 {
        val firstVertex = faces[0].vertices[0]
        var x = firstVertex.x
        var y = firstVertex.y
        var z = firstVertex.z
        for(face in faces) {
            for(vertex in face.vertices) {
                if(check(vertex.x, x)) {
                    x = vertex.x
                }
                if(check(vertex.y, y)) {
                    y = vertex.y
                }
                if(check(vertex.z, z)) {
                    z = vertex.z
                }
            }
        }
        return Vector3(x, y, z)
    }

    @Serializable
    enum class Format {
        @SerialName("obj") OBJ,
        @SerialName("off") OFF,
        @SerialName("ply") PLY
    }
}
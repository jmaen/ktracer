package objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

import models.*
import shading.Material

@Serializable
@SerialName("polygon")
class Polygon(private vararg val vertices: Vector3, private val material: Material) : Hittable {
    @Transient
    private lateinit var triangles: List<Triangle>
    @Transient
    private lateinit var plane: Plane

    init {
        val vertexList = vertices.asList()
        if(vertexList.size < 3) {
            throw IllegalArgumentException("There have to be at least 3 vertices.")
        }

        val p = calculatePlane(vertexList)
        if(p != null) {
            plane = p
        } else {
            throw IllegalArgumentException("All vertices have to lie on the same plane.")
        }

        if(!isConvexPolygon(vertexList)) {
            throw IllegalArgumentException("The vertices have to form a convex polygon.")
        }

        triangles = convertToTriangles(vertexList)
    }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val hit = plane.hit(ray, tMin, tMax)

        // if plane is hit, check if hit point is inside one of the triangles
        if(hit != null) {
            if(checkPolygon(hit.point)) {
                return hit
            }
        }

        return null
    }

    override fun checkPoint(point: Vector3): Boolean {
        return plane.checkPoint(point) && checkPolygon(point)
    }

    private fun checkPolygon(point: Vector3): Boolean {
        // check if point is inside the polygon (i.e. inside one of the triangles)
        for(triangle in triangles) {
            if(triangle.checkTriangle(point)) {
                return true
            }
        }

        return false
    }

    private fun calculatePlane(vertices: List<Vector3>): Plane? {
        // construct plane out of first three vertices
        val v0 = vertices[0]
        val v1 = vertices[1]
        val v2 = vertices[2]
        val normal = (v1 - v0) cross (v2 - v0)
        val plane = Plane(v0, normal, material)

        // check if all other vertices lie on the plane
        for(i in 3 until vertices.size) {
            if(!plane.checkPoint(vertices[i])) {
                return null
            }
        }

        return plane
    }

    private fun isConvexPolygon(vertices: List<Vector3>): Boolean {
        // TODO
        return true
    }

    private fun convertToTriangles(vertices: List<Vector3>): List<Triangle> {
        val triangleList = mutableListOf<Triangle>()

        // divide polygon into triangles using fan triangulation
        val v0 = vertices[0]
        for(i in 2 until vertices.size) {
            triangleList.add(Triangle(v0, vertices[i - 1], vertices[i], material))
        }

        return triangleList
    }
}
package hittables

import geometry.*
import shading.Material

class Polygon(vararg vertices: Vector3, private val material: Material) : Hittable {
    private val triangles: List<Triangle>

    init {
        val vertexList = vertices.asList()
        if(vertexList.size < 3) {
            throw IllegalArgumentException("Polygon needs at least 3 vertices.")
        }
        if(!lieOnPlane(vertexList)) {
            throw IllegalArgumentException("All vertices have to lie on the same plane.")
        }
        // TODO check if polygon is convex

        triangles = convertToTriangles(vertexList)
    }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        for(triangle in triangles) {
            val hit = triangle.hit(ray, tMin, tMax)
            if(hit != null) {
                return hit
            }
        }

        return null
    }

    private fun lieOnPlane(vertices: List<Vector3>): Boolean {
        // construct plane out of first three vertices
        val v0 = vertices[0]
        val v1 = vertices[1]
        val v2 = vertices[2]
        val normal = (v1 - v0) cross (v2 - v0)
        val d = v0 dot normal

        // check plane equation for all other vertices
        for(i in 3 until vertices.size) {
            if(vertices[i] dot normal != d) {
                return false
            }
        }

        return true
    }

    private fun convertToTriangles(vertices: List<Vector3>): List<Triangle> {
        val triangleList = mutableListOf<Triangle>()

        // divide polygon into triangles of shape (v[0], v[n+1], v[n+2])
        // where n is the number of triangles already added
        val v0 = vertices[0]
        for(i in 2 until vertices.size) {
            triangleList.add(Triangle(v0, vertices[i - 1], vertices[i], material))
        }

        return triangleList
    }
}
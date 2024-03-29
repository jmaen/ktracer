package objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

import materials.Material
import models.*
import kotlin.math.PI
import kotlin.math.abs

@Serializable
@SerialName("polygon")
class Polygon(vararg val vertices: Vector3, private val material: Material) : Transformable {
    @Transient
    private lateinit var triangles: List<Triangle>
    @Transient
    private lateinit var plane: Plane

    init {
        val vertexList = vertices.asList()
        require(vertexList.size >= 3) { "There have to be at least 3 vertices." }

        val p = calculatePlane(vertexList)
        requireNotNull(p) { "The vertices have to be coplanar." }
        plane = p

        require(isConvexPolygon(vertexList)) { "The vertices have to form a convex polygon." }

        triangles = convertToTriangles(vertexList)
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
        var orientation = 0
        var sum = 0.0
        for(i in vertices.indices) {
            val a = vertices[(i - 1).mod(vertices.size)]
            val b = vertices[i]
            val c = vertices[(i + 1).mod(vertices.size)]

            // check if C lies right or left of AB in the plane
            val ab = b - a
            val ac = c - a
            val mp = (ab cross ac) dot plane.normal
            if(orientation == 0) {
                orientation = if(mp < 0) { -1 } else if(mp > 0) { 1 } else { 0 }
            } else {
                // for convex polygons the orientation doesn't change
                if((orientation < 0 && mp > 0) || (orientation > 0 && mp < 0)) {
                    return false
                }
            }

            // calculate PI - angle at each point
            val ba = a - b
            val bc = c - b
            val angle = ba angle bc
            sum += PI - angle
        }
        // for convex polygons the sum of all (PI - angle) values is 2*PI
        return abs(sum - 2*PI) < 0.0001
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

    private fun checkPolygon(point: Vector3): Boolean {
        // check if point is inside the polygon (i.e. inside one of the triangles)
        for(triangle in triangles) {
            if(triangle.checkTriangle(point)) {
                return true
            }
        }

        return false
    }

    override fun translate(offset: Vector3): Polygon {
        val vertexList = mutableListOf<Vector3>()
        for(vertex in vertices) {
            vertexList.add(vertex + offset)
        }

        return Polygon(*vertexList.toTypedArray(), material = material)
    }

    override fun scale(factor: Double): Polygon {
        var center = Vector3.ZERO
        for(vertex in vertices) {
            center += vertex
        }
        center /= vertices.size

        val vertexList = mutableListOf<Vector3>()
        for(vertex in vertices) {
            vertexList.add(center + (vertex - center)*factor)
        }

        return Polygon(*vertexList.toTypedArray(), material = material)
    }

    override fun rotateX(angle: Double): Polygon {
        return rotate(Vector3::rotateX, angle)
    }

    override fun rotateY(angle: Double): Polygon {
        return rotate(Vector3::rotateY, angle)
    }

    override fun rotateZ(angle: Double): Polygon {
        return rotate(Vector3::rotateZ, angle)
    }

    private fun rotate(function: Vector3.(Double) -> Vector3, angle: Double): Polygon {
        // offset to rotate around origin
        var center = Vector3.ZERO
        for(vertex in vertices) {
            center += vertex
        }
        center /= vertices.size

        // move vertices so that center is at origin, rotate, move back
        val vertexList = mutableListOf<Vector3>()
        for(vertex in vertices) {
            var rotatedVertex = vertex - center
            rotatedVertex = rotatedVertex.function(angle)
            rotatedVertex += center
            vertexList.add(rotatedVertex)
        }

        return Polygon(*vertexList.toTypedArray(), material = material)
    }
}

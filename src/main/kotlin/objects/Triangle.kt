package objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

import models.*
import materials.Material

@Serializable
@SerialName("triangle")
class Triangle(private val vertex1: Vector3, private val vertex2: Vector3, private val vertex3: Vector3, private val material: Material) : Transformable {
    @Transient
    private val normal = (vertex2 - vertex1) cross (vertex3 - vertex1)
    @Transient
    private val plane = Plane(vertex1, normal, material)
    @Transient
    private val edge0 = vertex2 - vertex1
    @Transient
    private val edge1 = vertex3 - vertex2
    @Transient
    private val edge2 = vertex1 - vertex3

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val hit = plane.hit(ray, tMin, tMax)

        // if plane is hit, check if point is inside the triangle
        if(hit != null) {
            if(checkTriangle(hit.point)) {
                return hit
            }
        }

        return null
    }

    override fun checkPoint(point: Vector3): Boolean {
        return plane.checkPoint(point) && checkTriangle(point)
    }

    fun checkTriangle(point: Vector3): Boolean {
        // check if point is inside the triangle
        val c0 = point - vertex1
        val c1 = point - vertex2
        val c2 = point - vertex3
        if(normal dot (edge0 cross c0) > 0 &&
            normal dot (edge1 cross c1) > 0 &&
            normal dot (edge2 cross c2) > 0) {
            return true
        }

        return false
    }

    override fun translate(offset: Vector3): Transformable {
        return Triangle(vertex1 + offset, vertex2 + offset, vertex3 + offset, material)
    }

    override fun scale(factor: Double): Transformable {
        val center = (vertex1 + vertex2 + vertex3) / 3
        val scaledVertex1 = center + (vertex1 - center)*factor
        val scaledVertex2 = center + (vertex2 - center)*factor
        val scaledVertex3 = center + (vertex3 - center)*factor

        return Triangle(scaledVertex1, scaledVertex2, scaledVertex3, material)
    }

    override fun rotateX(angle: Double): Transformable {
        return rotate(Vector3::rotateX, angle)
    }

    override fun rotateY(angle: Double): Transformable {
        return rotate(Vector3::rotateY, angle)
    }

    override fun rotateZ(angle: Double): Transformable {
        return rotate(Vector3::rotateZ, angle)
    }

    private fun rotate(function: Vector3.(Double) -> Vector3, angle: Double): Transformable {
        // offset to rotate around origin
        val center = (vertex1 + vertex2 + vertex3) / 3

        // move vertices so that center is at origin, rotate, move back
        var rotatedVertex1 = vertex1 - center
        rotatedVertex1 = rotatedVertex1.function(angle)
        rotatedVertex1 += center

        var rotatedVertex2 = vertex2 - center
        rotatedVertex2 = rotatedVertex2.function(angle)
        rotatedVertex2 += center

        var rotatedVertex3 = vertex3 - center
        rotatedVertex3 = rotatedVertex3.function(angle)
        rotatedVertex3 += center

        return Triangle(rotatedVertex1, rotatedVertex2, rotatedVertex3, material)
    }
}
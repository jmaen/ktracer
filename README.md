# ktracer
**kt**racer is a basic pathtracer written entirely in Kotlin. 

It currently supports:
- Multiple objects (spheres, cylinders, axis-aligned boxes, planes, disks, triangles and convex polygons)
- Transforms (translation, rotation and scaling)
- Multiple materials (diffuse, metal and emissive)
- Anti-aliasing (SSAA) 
- Depth of field
- JSON serialization for scene creation

## Example renders
<p align="center">
  <img src="https://user-images.githubusercontent.com/47495425/181196535-02636df5-d913-4893-9cff-59aea2e430f1.png" width=500/><br>
  (1000x1000, 2000 samples, 10 max bounces)
</p>

## About
### Why this project?
I always thought computer graphics was a really interesting field and wanted to learn more about it. Also, I happened to be in search of a new programming project, so my own path tracer seemed like a perfect fit. Keep in mind that this is my first computer graphics project, so the project is not so much about maximum performance, but more about developing a general understanding of how things work at a basic level, and exploring the different techniques used in the process.

### Why Kotlin?
First of all, perhaps the better question would be “Why not C++?”. The answer is simple: most similar projects are written in C++, and I wanted to create my own version and not find myself copying other projects or guides one by one. As I explained above, performance wasn’t really an issue either, so I decided to go with a different language. As for why I chose Kotlin specifically, I've been wanting to try Kotlin for quite a while now and simply decided to finally do so in this project.

### Sources
Sources that helped or inspired me in the development of this project include:
- Peter Shirley's [raytracing series](https://raytracing.github.io/)
- [Scratchapixel](https://scratchapixel.com)
- [C-ray](https://github.com/vkoskiv/c-ray)

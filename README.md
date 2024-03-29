# ktracer
**kt**racer is a basic pathtracer written entirely in Kotlin. 

It currently supports:
- Multiple objects (spheres, cylinders, cones, axis-aligned boxes, planes, disks, triangles and convex polygons)
- Polygon meshes (.obj format)
- Transforms (translation, rotation and scaling)
- Multiple materials (diffuse, metal, glass and emissive)
- Anti-aliasing (SSAA) 
- Depth of field
- Multithreading
- JSON serialization for scene saving/loading

## Example renders

<p align="center">
  <img src="https://user-images.githubusercontent.com/47495425/195702771-0d21b65f-147f-4944-a9b3-7825fbf89d02.png" width=500/><br>
  (1000x1000, 2000 samples, 2x supersampling, 10 max bounces)
</p>

## Usage
```
Usage: ktracer scene [-o output] [-t threads] [-g gamma]
Arguments:
    scene -> Scene file path { String }
Options:
    --output, -o -> Output file path { String }
    --threads, -t -> Amount of threads the program should run in { Int }
    --gamma, -g -> Value used for gamma correcting the image { Double }
    --help, -h -> Usage info
```

## About
### Why this project?
I always thought computer graphics was a really interesting field and wanted to learn more about it. I also happened to be in search of a new programming project, so my own path tracer seemed like a perfect fit. Keep in mind that this project is not so much about maximum performance, but more about developing a general understanding of how things work, and exploring the various techniques involved.

### Why Kotlin?
First of all, perhaps the better question would be “Why not C++?”, considering how widely used it is in computer graphics. As I explained above, performance wasn't really the main focus for this project, so I decided to go with a different language. Since I've wanted to try Kotlin for quite a while, I simply decided to finally do so in this project.

### Sources
Sources that helped or inspired me in the development of this project include:
- Peter Shirley's [raytracing series](https://raytracing.github.io/)
- [Scratchapixel](https://scratchapixel.com)
- [vkoskiv/c-ray](https://github.com/vkoskiv/c-ray)
- [hunterloftis/pathtracer](https://github.com/hunterloftis/pathtracer)

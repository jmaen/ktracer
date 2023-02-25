import java.io.File
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

import scene.Scene
import util.roundTo

suspend fun main(args: Array<String>) {
    val parser = ArgParser("ktracer")
    val scene by parser.argument(ArgType.String, description = "Scene file path")
    val output by parser.option(ArgType.String, shortName = "o", description = "Output file path")
    val threads by parser.option(ArgType.Int, shortName = "t",
        description = "Amount of threads the program should run in")
    val gamma by parser.option(ArgType.Double, shortName = "g",
        description = "Value used for gamma correcting the image")

    parser.parse(args)

    run(scene, output, threads, gamma)
}

private suspend fun run(scenePath: String, outputPath: String?, threads: Int?, gamma: Double?) {
    val sceneFile = File(scenePath)

    // create output file
    val outputFile = if(outputPath != null) {
        File(outputPath)
    } else {
        val sceneName = sceneFile.nameWithoutExtension
        var file = File(sceneFile.parentFile, "$sceneName.png")

        // make sure nothing is overwritten
        var i = 1
        while(file.exists()) {
            file = File(sceneFile.parentFile, "${sceneName}$i.png")
            i++
        }

        file
    }
    require(outputFile.extension == "png") { "Currently only PNG files are supported." }

    // render scene
    val scene = Scene.load(sceneFile)
    val image = if(threads != null) {
        require(threads > 0) { "At least one thread is needed to run the program." }
        scene.render(threads)
    } else {
        scene.render()
    }
    image.save(outputFile)

    // gamma correct the image
    if(gamma != null) {
        require(gamma > 0) { "Gamma value has to be > 0." }
        image.gammaCorrect(gamma)

        val gammaName = "${outputFile.nameWithoutExtension}_g${gamma.roundTo(1)}.png"
        val gammaFile = File(outputFile.parentFile, gammaName)
        image.save(gammaFile)
    }
}

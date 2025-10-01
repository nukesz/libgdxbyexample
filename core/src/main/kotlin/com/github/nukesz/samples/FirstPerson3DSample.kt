package com.github.nukesz.samples

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.github.nukesz.controller.FirstPersonCameraController
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.github.nukesz.LibGDXbyExample
import com.github.nukesz.graphics.clearScreen


class FirstPerson3DSample(game: LibGDXbyExample) : BaseScreen(game) {

    private val camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    private lateinit var cameraController: FirstPersonCameraController

    private lateinit var modelBatch: ModelBatch
    private lateinit var environment: Environment

    private lateinit var groundModel: Model
    private lateinit var groundInstance: ModelInstance

    private lateinit var boxModel: Model
    private val boxInstances = mutableListOf<ModelInstance>()

    override fun show() {
        super.show()

        // Camera: placed at y=2 so we stand above the ground
        camera.position.set(0f, 2f, 5f)
        camera.lookAt(0f, 1.5f, 0f)
        camera.near = 0.1f
        camera.far = 1000f
        camera.update()

        // Attach FPS camera controller
        cameraController = FirstPersonCameraController(camera)
        setupInputProcessor(cameraController)

        // Rendering utilities
        modelBatch = ModelBatch()

        environment = Environment().apply {
            set(ColorAttribute.createAmbientLight(0.8f, 0.8f, 0.8f, 1f))
            add(DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f))
        }

        // Build a very large ground plane (simulates "infinite" flat land)
        val modelBuilder = ModelBuilder()
        val groundMaterial = Material(ColorAttribute.createDiffuse(Color.FOREST))
        groundModel = modelBuilder.createBox(
            10000f, 1f, 10000f, // Width, height, depth
            groundMaterial,
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
        groundInstance = ModelInstance(groundModel)

        val boxMaterial = Material(ColorAttribute.createDiffuse(Color.BROWN))
        // A reusable cube model
        boxModel = modelBuilder.createBox(
            2f, 2f, 2f,
            boxMaterial,
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )

        // Scatter a few cubes as landmarks
        // Ground is 1 unit thick, so its top is at y = +0.5
        // Cube is 2 units tall, so half-height is 1
        // Correct Y position = groundTop + cubeHalfHeight = 0.5 + 1 = 1.5
        boxInstances.add(ModelInstance(boxModel, 5f, 1.5f, 0f))    // Right of start
        boxInstances.add(ModelInstance(boxModel, -5f, 1.5f, 0f))   // Left of start
        boxInstances.add(ModelInstance(boxModel, 0f, 1.5f, 10f))   // Forward
        boxInstances.add(ModelInstance(boxModel, 0f, 1.5f, -10f))  // Behind
        boxInstances.add(ModelInstance(boxModel, 10f, 1.5f, 10f))  // Diagonal
    }

    override fun render(delta: Float) {
        // Update camera movement
        cameraController.update(delta)

        // Clear screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        clearScreen(0.5f, 0.7f, 1f, 1f) // light blue sky

        // Render scene
        modelBatch.begin(camera)
        modelBatch.render(groundInstance, environment)
        for (box in boxInstances) {
            modelBatch.render(box, environment)
        }
        modelBatch.end()

        renderGui(delta)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }

    override fun dispose() {
        super.dispose()
        modelBatch.dispose()
        groundModel.dispose()
        boxModel.dispose()
    }
}
